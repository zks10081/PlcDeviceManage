package com.ruoyi.common.opc.ua;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    public String  readPLC(int namespaceIndex, String nameIndex) {
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
     * 批量读取 PLC
     * @param namespaceIndex
     * @param nodeStrs 点位list
     * @return
     */
    public Map<String,Object> readBatchPLC(int namespaceIndex,List<String> nodeStrs) {
        OpcUaClient client = ClientGen.opcUaClient;
        List<NodeId> nodeIdList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();

        for (String nodeStr : nodeStrs) {
            NodeId nodeId = new NodeId(namespaceIndex, nodeStr);
            nodeIdList.add(nodeId);
        }

        try {
            client.connect().get();
            CompletableFuture<List<DataValue>> listCompletableFuture = client.readValues(0.0, TimestampsToReturn.Both, nodeIdList);

            List<DataValue> dataValues = listCompletableFuture.get();
            for (int i = 0; i < nodeStrs.size(); i++) {
                String key = nodeStrs.get(i);
                DataValue dataValue = dataValues.get(i);
                String plcValue = dataValue.getValue().getValue().toString();
                String statusCode = dataValue.getStatusCode().toString();
                if (dataValue.getStatusCode() != null && !dataValue.getStatusCode().isGood()) {
                    plcValue = "";
                    log.error("=====》  点位：{} ，读数据异常，未获取到数据。。。",key);
                }
                resultMap.put(key,plcValue);
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
        }
        return resultMap;
    }

    /***
     * 写入 PLC
     * @param namespaceIndex
     * @param itemIndex
     * @param editValue
     * @return
     */
    public String writePLC(int namespaceIndex, String itemIndex,String dataType,  String editValue) {
        try {
//          创建连接
            OpcUaClient client = ClientGen.opcUaClient;
            client.connect().get();
//          创建节点
            NodeId nodeId = new NodeId(namespaceIndex, itemIndex);
            //获取节点的数据类型
            UaVariableNode node = client.getAddressSpace().getVariableNode(nodeId);
            NodeId dataTypeId = node.getDataType();

            //根据数据类型转换 rawValue
            Object convertedValue = convertValue(editValue, dataTypeId);

            //创建Variant对象和DataValue对象
            Variant v = new Variant(convertedValue);

            //写入数据
            DataValue dv = new DataValue(v,  StatusCode.GOOD, null);
            StatusCode statusCode = client.writeValue(nodeId, dv).get();
            if (statusCode.isGood()) {
                log.info("========== >  it means successfully Wrote '{}' to nodeId={}, statusCodes = {}", v, nodeId, statusCode);
                return StrUtil.format("successfully Wrote 点位：{}，执行成功",itemIndex);
            } else {
                log.error("statusCodes: {}", statusCode);
            }
            return statusCode.toString();

        }  catch (InterruptedException | ExecutionException | UaException e) {
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

    public void getbrowseNode(){
        OpcUaClient client = ClientGen.opcUaClient;
        try {
            client.connect().get();
            List<ReferenceDescription> browse = client.getAddressSpace().browse(Identifiers.RootFolder);
            browse.forEach(ref -> {
                System.out.println("nodeId = " + ref.getNodeId());
                System.out.println("name = " + ref.getDisplayName().getText());
            });
        }catch (InterruptedException | ExecutionException | UaException e) {
            log.error("Browsing nodeId={} failed: {}", "browseRoot", e.getMessage(), e);
        }
    }

    private Object convertValue(Object rawValue, NodeId dataTypeId) {
        // 使用 Milo 的 Identifiers 类进行类型判断
        if (Identifiers.UInt16.equals(dataTypeId)) {
            // 针对 PLC 的 "Word" 或 UInt16 类型
            return Unsigned.ushort((int) parseToDouble(rawValue));
        } else if (Identifiers.Int16.equals(dataTypeId)) {
            return (short) parseToDouble(rawValue);
        } else if (Identifiers.UInt32.equals(dataTypeId)) {
            return Unsigned.uint(((Number) rawValue).longValue());
        } else if (Identifiers.Int32.equals(dataTypeId)) {
            return (int) parseToDouble(rawValue);
        } else if (Identifiers.Float.equals(dataTypeId)) {
            return (float) parseToDouble(rawValue);
        } else if (Identifiers.Double.equals(dataTypeId)) {
            return parseToDouble(rawValue);
        } else if (Identifiers.String.equals(dataTypeId)) {
            return rawValue.toString();
        } else if (Identifiers.Boolean.equals(dataTypeId)) {
            return Boolean.parseBoolean(rawValue.toString());
        }
        // ... 添加其他类型的处理
        return rawValue;
    }

    private double parseToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            return Double.parseDouble((String) value);
        }
        throw new IllegalArgumentException("不支持的数据类型: " + value.getClass());
    }

}
