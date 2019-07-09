package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dafang
 */
public class RuntimeSysUsablityContants {

    private static ConcurrentMap<String, Integer> currentUsablityMap = new ConcurrentHashMap();

    static{
        currentUsablityMap.putIfAbsent(Contants.PROVIDER_KEY_SMALL,300);
        currentUsablityMap.putIfAbsent(Contants.PROVIDER_KEY_MEDIUM,300);
        currentUsablityMap.putIfAbsent(Contants.PROVIDER_KEY_LARGE,300);
    }

    public static void setCurrentUsablityMap(String key,Integer usablity) {
        if (StringUtils.isBlank(key) || !currentUsablityMap.containsKey(key)){
            return;
        }
        RuntimeSysUsablityContants.currentUsablityMap.put(key,usablity);
    }

    public static Integer getSystemUsablity(String key) {
        return currentUsablityMap.get(key);
    }

    public static Set<Map.Entry<String, Integer>> getCpuMapEntry() {
        return currentUsablityMap.entrySet();
    }

    public static int getTotalSystemUsablity(){
        int total = 0;
        for (Map.Entry<String, Integer> entry : getCpuMapEntry()){
            total += entry.getValue();
        }
        return total;
    }

}
