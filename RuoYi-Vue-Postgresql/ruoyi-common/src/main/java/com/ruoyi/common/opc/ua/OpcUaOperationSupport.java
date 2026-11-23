package com.ruoyi.common.opc.ua;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.alibaba.fastjson2.util.TypeUtils.toList;
import static org.apache.commons.compress.utils.Lists.newArrayList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;


@Slf4j
@Component
public class OpcUaOperationSupport {

    /**
     * 浏览节点
     */
    public String browseNode() {
        OpcUaClient client = ClientGen.opcUaClient;
        try {
            client.connect().get();
            return browseNode("", client, Identifiers.RootFolder);
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            return e.getMessage();
        }
    }

    /***
     * 单个读取 PLC
     * @param namespaceIndex
     * @param nameIndex
     * @return
     */
    public String  readPLC(int namespaceIndex, int nameIndex) {
        OpcUaClient client = ClientGen.opcUaClient;
        try {
            client.connect().get();
            NodeId nodeId = new NodeId(namespaceIndex, nameIndex);
            CompletableFuture<DataValue> readValue = client.readValue(0.0, TimestampsToReturn.Both, nodeId);
            DataValue value = readValue.get();
            String plcValue = value.getValue().getValue().toString();
            String statusCode = value.getStatusCode().toString();
            if (value.getStatusCode() != null) {
                if (value.getStatusCode().isGood()) {
                    log.info("获取数据成功。plcValue={}", plcValue);
                } else {
                    log.info("获取数据失败。");
                }
                log.info("======== >  it means successfully read StatusCode = {}", statusCode);
            } else {
                log.error("=====》  读数据异常，未获取到数据。。。");
            }
            return plcValue;

        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            return e.getMessage();
        }
    }

    /***
     * 写入 PLC
     * @param namespaceIndex
     * @param itemIndex
     * @param valueType
     * @param editValue
     * @return
     */
    public String  writePLC(int namespaceIndex, int itemIndex, String valueType, String editValue) {
        try {
//          创建连接
            OpcUaClient client = ClientGen.opcUaClient;
            client.connect().get();
//          创建节点
            NodeId nodeId = new NodeId(namespaceIndex, itemIndex);
//          创建Variant对象和DataValue对象
            Variant v;
            boolean b = true;
            switch (editValue) {
                case "1":
                    v = new Variant(b);
                    break;
                case "0":
                    b = false;
                    v = new Variant(b);
                    break;
                default:
                    v = new Variant(editValue);
                    break;
            }
            DataValue dv = new DataValue(v, null, null);
            StatusCode statusCode = client.writeValue(nodeId, dv).get();
            if (statusCode.isGood()) {
                log.info("========== >  it means successfully Wrote '{}' to nodeId={}, statusCodes = {}", v, nodeId, statusCode);
            } else {
                log.error("statusCodes: {}", statusCode);
            }
            return statusCode.toString();

        }  catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            return e.getMessage();
        }
    }

    /***
     * 单个读取 PLC（字符串点位地址重载，与 dispatch 项目保持一致）
     * @param namespaceIndex 命名空间索引
     * @param address 点位地址
     * @return
     */
    public String readPLC(int namespaceIndex, String address) {
        OpcUaClient client = ClientGen.opcUaClient;
        try {
            client.connect().get();
            NodeId nodeId = new NodeId(namespaceIndex, address);
            CompletableFuture<DataValue> readValue = client.readValue(0.0, TimestampsToReturn.Both, nodeId);
            DataValue value = readValue.get();
            if (value.getValue() == null || value.getValue().getValue() == null) {
                return "";
            }
            return value.getValue().getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            return e.getMessage();
        }
    }

    /***
     * 写入 PLC（字符串点位地址重载，与 dispatch 项目保持一致）
     * @param namespaceIndex 命名空间索引
     * @param address 点位地址
     * @param valueType 值类型
     * @param editValue 写入值
     * @return
     */
    public String writePLC(int namespaceIndex, String address, String valueType, String editValue) {
        try {
            OpcUaClient client = ClientGen.opcUaClient;
            client.connect().get();
            NodeId nodeId = new NodeId(namespaceIndex, address);
            Variant v;
            boolean b = true;
            switch (editValue) {
                case "1":
                    v = new Variant(b);
                    break;
                case "0":
                    b = false;
                    v = new Variant(b);
                    break;
                default:
                    v = new Variant(editValue);
                    break;
            }
            DataValue dv = new DataValue(v, null, null);
            StatusCode statusCode = client.writeValue(nodeId, dv).get();
            if (statusCode.isGood()) {
                log.info("========== >  it means successfully Wrote '{}' to nodeId={}, statusCodes = {}", v, nodeId, statusCode);
            } else {
                log.error("statusCodes: {}", statusCode);
            }
            return statusCode.toString();
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            return e.getMessage();
        }
    }

    /***
     * 订阅变量
     * @param namespaceIndex
     * @param itemIndex
     */
    public void createSubscription(int namespaceIndex, int itemIndex) {
        try {
            OpcUaClient client = ClientGen.opcUaClient;
            client.connect().get();

            //创建发布间隔1000ms的订阅对象
            UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();

            //创建监控的参数
            MonitoringParameters parameters = new MonitoringParameters(
                    uint(1),
                    // 发布间隔
                    1000.0,
                    // filter, 空表示用默认值
                    null,
                    // 队列大小
                    uint(10),
                    //放弃旧配置
                    true
            );
            //创建订阅的变量
            NodeId nodeId = new NodeId(namespaceIndex, itemIndex);
            ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
            //创建监控项请求
            //该请求最后用于创建订阅。
            MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
            List<MonitoredItemCreateRequest> requests = new ArrayList<>();
            requests.add(request);
            //创建监控项，并且注册变量值改变时候的回调函数。
            UaSubscription.ItemCreationCallback onItemCreated =
                    (subscriptionItem, id) -> subscriptionItem.setValueConsumer((UaMonitoredItem item1, DataValue value) -> {
                                log.info("===== >  here is callbacks ... 订阅的回调函数。 ");
                                log.info("订阅成功, item1 : {}", item1.getReadValueId().getNodeId().toString());
                                log.info("subscription value received: item={}, value={}", item1.getReadValueId().getNodeId(), value.getValue());
                            }
                    );
            List<UaMonitoredItem> monitoredItems = subscription.createMonitoredItems(
                    TimestampsToReturn.Both,
                    requests,
                    onItemCreated
            ).get();

            for (UaMonitoredItem monitoredItem : monitoredItems) {
                if (monitoredItem.getStatusCode().isGood()) {
                    log.info("item created for nodeId={}", monitoredItem.getReadValueId().getNodeId());
                } else {
                    log.warn(
                            "failed to create item for nodeId={} (status={})",
                            monitoredItem.getReadValueId().getNodeId(), monitoredItem.getStatusCode());
                }
            }
        } catch (Exception e) {
            log.error("订阅变量失败");
        }
    }


    /**
     * 浏览节点（抽取方法）
     *
     * @param indent
     * @param client
     * @param browseRoot
     */
    private String  browseNode(String indent, OpcUaClient client, NodeId browseRoot) {
        BrowseDescription browse = new BrowseDescription(
                browseRoot,
                BrowseDirection.Forward,
                Identifiers.References,
                true,
                uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue())
        );
        try {
            BrowseResult browseResult = client.browse(browse).get();
            ReferenceDescription[] references = browseResult.getReferences();
            StringBuilder nodesInfo = new StringBuilder();
            for (ReferenceDescription rd : references) {
                log.info("{} Node={}", indent, rd.getBrowseName().getName());

                // recursively browse to children
                rd.getNodeId().toNodeId(client.getNamespaceTable())
                        .ifPresent(nodeId -> browseNode(indent + "  ", client, nodeId));
            }
            return nodesInfo.toString();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Browsing nodeId={} failed: {}", browseRoot, e.getMessage(), e);
            return e.getMessage();
        }
    }

}
