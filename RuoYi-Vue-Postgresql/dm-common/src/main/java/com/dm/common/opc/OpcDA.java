package com.dm.common.opc;

import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class OpcDA {

    private ConnectionInformation connectionInfo;
    private Server server;



    public Server connect(){
        try {
            //设置OPC配置信息
            connectionInfo = new ConnectionInformation();
            connectionInfo.setHost(OpcProperties.ip);
            connectionInfo.setDomain("");
            connectionInfo.setUser(OpcProperties.user);
            connectionInfo.setPassword(OpcProperties.password);
            connectionInfo.setClsid(OpcProperties.code);
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            server = new Server(connectionInfo,executor);
            server.connect();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        catch (JIException e) {
            throw new RuntimeException(e);
        }
        catch (AlreadyConnectedException e) {
            throw new RuntimeException(e);
        } finally {
            server = null;
        }

        return server;
    }

    public void disconnect(){
        if (server == null) {
            return;
        }
        server.dispose();
        server.disconnect();
        server = null;
    }

    public Map<String,String> read(List<String> list) throws DuplicateGroupException, UnknownHostException, NotConnectedException, JIException, AddFailedException {
        Map<String,String> dataList = new HashMap<>();

        Group group = server.addGroup();
        String[] items = list.toArray(new String[]{});
        Map<String, Item> itemResult = group.addItems(items);
        Set itemSet = new HashSet(itemResult.values());
        Item[] itemArr = new Item[itemSet.size()];
        itemSet.toArray(itemArr);
        Map<Item, ItemState> resultMap = group.read(true, itemArr);
        for (Item key : resultMap.keySet()) {
            ItemState itemState = resultMap.get(key);
            String data = dumpItem(itemState.getValue());
            dataList.put(key.getId(),data);
        }
        return dataList;
    }

    public String read(String address){
        try {
            Group group = server.addGroup();
            Item item = group.addItem(address);
            Item[] itemArr = new Item[]{item};
            ItemState itemState = group.read(true, itemArr).get(item);
            String data = dumpItem(itemState.getValue());
            return data;
        }
        catch (Exception e) {
            log.error("OPC读取错误：",e.getMessage());
            disconnect();
            return null;
        }
    }

    public boolean write(String address,String value){
        try {
            Group group = server.addGroup();
            Item item = group.addItem(address);
            JIVariant variant = new JIVariant(value);
            item.write(variant);
        }
        catch (Exception e) {
            log.error("OPC写入错误：",e.getMessage());
            disconnect();
            return false;
        }

        return true;
    }


    private String dumpItem(JIVariant var) throws JIException {


        String value = "";
        int type = 0;
        try {
            type = var.getType(); // 类型实际是数字，用常量定义的
        } catch (JIException e) {
            e.printStackTrace();
        }


        // 如果读到是short类型的值
        if (type == JIVariant.VT_I2) {
            try {
                value = var.getObjectAsShort() + "";
            } catch (JIException e) {
                e.printStackTrace();
            }
        }
        if (type == JIVariant.VT_BOOL) {
            try {
                value = var.getObjectAsBoolean() + "";
            } catch (JIException e) {
                e.printStackTrace();
            }
        }
        //如果读到是World类型的值
        if (type == JIVariant.VT_I4) {
            try {

                value = var.getObjectAsInt() + "";
            } catch (JIException e) {
                e.printStackTrace();
            }
        }
        //如果读到是World类型的值
        if (type == JIVariant.VT_UI2) {
            try {

                value = var.getObjectAsUnsigned().getValue() + "";
            } catch (JIException e) {
                e.printStackTrace();
            }
        }
        if (type == JIVariant.VT_UI4) {
            try {

                value = var.getObjectAsUnsigned().getValue() + "";
            } catch (JIException e) {
                e.printStackTrace();
            }
        }
        //如果读到是Float类型的值
        if (type == JIVariant.VT_R4) {
            try {

                value = String.format("%.3f", var.getObjectAsFloat());
            } catch (JIException e) {
                e.printStackTrace();
            }
        }
        // 如果读到是字符串类型的值
        if (type == JIVariant.VT_BSTR) {  // 字符串的类型是8

            try {
                value = var.getObjectAsString().getString();
            } catch (JIException e) {
                e.printStackTrace();
            } // 按字符串读取
        }
        // 读取Float类型的数组
        if (type == 8194) { // 8196是打印state.getValue().getType()得到的
            JIArray jarr = null; // 按数组读取
            try {
                jarr = var.getObjectAsArray();
            } catch (JIException e) {
                e.printStackTrace();
            }
            Object[] le = (Object[]) jarr.getArrayInstance();
            for (Object f : le) {
                value = value + f + ",";
            }
        }
        return value;
    }
}
