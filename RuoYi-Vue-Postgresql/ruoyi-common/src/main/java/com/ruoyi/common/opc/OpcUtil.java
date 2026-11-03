package com.ruoyi.common.opc;

import lombok.extern.slf4j.Slf4j;
import org.openscada.opc.lib.da.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class OpcUtil {

    private List<OpcDA> opcDALoop = new ArrayList<>();

    public static String read(String address){
        OpcDA opcDA = new OpcDA();
        opcDA.connect();
        String read = opcDA.read(address);
        opcDA.disconnect();
        return read;
    }

    public static Map<String,String> read(List<String> list) {
        OpcDA opcDA = new OpcDA();
        Map<String, String> read = null;
        try{
            opcDA.connect();
            read = opcDA.read(list);
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            opcDA.disconnect();
        }
        return read;
    }

    public static boolean write(String address,String value){
        OpcDA opcDA = new OpcDA();
        opcDA.connect();
        boolean result = opcDA.write(address, value);
        return result;
    }
}
