package com.ruoyi.device.utils;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.opc.ua.ClientGen;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.service.IDmDeviceAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class AttributeSubscription {

    @Autowired
    private IDmDeviceAttributeService attributeService;

    @Autowired
    private ClientGen clientGen;

    // 推荐每批 500 个，可根据网络情况调整（不要超过 1000）
    private static final int BATCH_SIZE = 500;
    // 生成唯一的 clientHandle
    private final AtomicLong clientHandles = new AtomicLong(1);

    // 订阅在独立线程执行，避免阻塞 ApplicationReadyEvent 发布线程
    private final ExecutorService subscribeExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "opcua-subscribe");
        t.setDaemon(true);
        return t;
    });

    private static Map<String, DmDeviceAttribute> attributeMap = new HashMap<>();

    /**
     * 项目启动后订阅：等待 ClientGen 注册（创建 + 连接）完成再执行。
     * 顺序由 ClientGen.whenReady() 保证，与 Bean 初始化先后无关。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        if(true)return;
        log.info("OPCUA订阅推送","开始订阅点位");
        //开启数据同步
        SyncDataStart();
        //启动后台数据库批量同步线程池
        DataSyncManager.startBatchProcessor();
        //开启订阅
        clientGen.whenReady()
                .thenRunAsync(this::subscribeAll, subscribeExecutor)
                .exceptionally(ex -> {
                    log.error("OPC UA 批量订阅启动失败（客户端未就绪）", ex);
                    return null;
                });
    }

    public void subscribeAll() {
        try {
            // 已注册完成时立即返回；未完成时等待，拿到的一定是已连接的客户端
            OpcUaClient client = clientGen.whenReady().join();
            //获取注册的点位
            List<NodeId> nodeList = getNodeList();
            if (nodeList.isEmpty()) {
                log.warn("没有可订阅的设备点位，跳过 OPC UA 订阅");
                return;
            }
            //创建订阅（每 1000ms 发布一次）
            UaSubscription subscription = client.getSubscriptionManager()
                    .createSubscription(1000.0).get();

            // 2. 将 NodeId 转换成 MonitoredItemCreateRequest 列表
            List<MonitoredItemCreateRequest> requests = new ArrayList<>();
            for (NodeId nodeId : nodeList) {
                ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
                UInteger clientHandle = Unsigned.uint(clientHandles.getAndIncrement());
                MonitoringParameters parameters = new MonitoringParameters(
                        clientHandle,
                        1000.0, // 采样间隔（ms），建议与发布间隔一致或更小
                        null,
                        Unsigned.uint(10),     // 队列大小，防止网络抖动丢数据
                        true    // 丢弃旧数据
                );

                requests.add(new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters));
            }

            // 3. 定义回调（所有节点共享此回调）
            UaSubscription.ItemCreationCallback onItemCreated = (item, id) -> {
                // 当服务器推送数据时，执行这里
                item.setValueConsumer(AttributeSubscription::handleDataChange);
            };

            // 4. 分批发送订阅请求
            int total = requests.size();
            log.info("准备订阅节点总数: {}", total);

            for (int i = 0; i < total; i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, total);
                List<MonitoredItemCreateRequest> batch = requests.subList(i, end);

                try {
                    // 发送这一批订阅请求
                    List<UaMonitoredItem> items = subscription.createMonitoredItems(
                            TimestampsToReturn.Both,
                            batch,
                            onItemCreated
                    ).get();

                    log.info("批次 [{}-{}] 订阅成功，共 {} 个点", i + 1, end, items.size());
                } catch (Exception e) {
                    log.error("批次 [{}-{}] 订阅失败", i + 1, end, e);
                }
            }
            log.info("OPC UA 批量订阅完成，共提交 {} 个点位", total);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<NodeId> getNodeList() {
        List<NodeId> nodeList = new ArrayList<>();
        List<DmDeviceAttribute> list = attributeService.list();
        for (DmDeviceAttribute attribute : list) {
            if (StrUtil.isBlank(attribute.getAddress())) {
                log.warn("点位[{}]地址为空，跳过订阅", attribute.getName());
                continue;
            }
            nodeList.add(new NodeId(2, attribute.getAddress()));
        }
        return nodeList;
    }

    // 数据变化回调
    private static void handleDataChange(UaMonitoredItem item, DataValue value) {
        // 1. 获取节点名称与最新值
        String address = item.getReadValueId().getNodeId().getIdentifier().toString();
        Object rawValue = value.getValue().getValue();
        String valStr = rawValue != null ? rawValue.toString() : "";

        // 2. 入队（非阻塞）
        DmDeviceAttribute attribute = attributeMap.get(address);
        String plcData = attribute.getPlcData();
        if(StrUtil.equals(plcData,valStr))return;
        attribute.setPlcData(valStr);

        boolean success = DataSyncManager.syncQueue.offer(attribute);
        if (!success) {
            System.err.println("警告：缓冲区已满，丢弃数据 " + address);
        }
    }

    //每60秒同步一次点位数据
    public void SyncDataStart(){
        List<DmDeviceAttribute> list = attributeService.list();
        for (DmDeviceAttribute attribute : list) {
            attributeMap.put(attribute.getAddress(), attribute);
        }

        Thread dispatcherThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);
                    List<DmDeviceAttribute> attributeList = attributeService.list();
                    for (DmDeviceAttribute attribute : attributeList) {
                        attributeMap.put(attribute.getAddress(), attribute);
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        subscribeExecutor.shutdownNow();
    }

}
