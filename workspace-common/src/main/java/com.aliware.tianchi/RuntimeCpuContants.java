package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dafang
 */
public class RuntimeCpuContants {

    public static class Client{
        private static volatile int cpuUsage = 0;

        public static int getCpuUsage() {
            return cpuUsage;
        }

        public static void setCpuUsage(int cpuUsage) {
            if(cpuUsage>100){
                return;
            }
            if (cpuUsage <1 ){
                cpuUsage = 0;
            }
            RuntimeCpuContants.Client.cpuUsage = cpuUsage;
        }
    }

    public static class Server{

        private static ConcurrentMap<String, Integer> currentCpuMap = new ConcurrentHashMap();
        private static ConcurrentMap<String, Integer> lastCpuMap = new ConcurrentHashMap();

        static{
            currentCpuMap.putIfAbsent(Contants.PROVIDER_KEY_SMALL,0);
            currentCpuMap.putIfAbsent(Contants.PROVIDER_KEY_MEDIUM,0);
            currentCpuMap.putIfAbsent(Contants.PROVIDER_KEY_LARGE,0);
        }

        public static int getCurrCpuUsage(String key) {
            return currentCpuMap.get(key);
        }

        public static int getLastCpuUsage(String key) {
            return lastCpuMap.get(key);
        }

        public static void setCpuUsage(String key,int cpu) {
            if (StringUtils.isBlank(key) || !currentCpuMap.containsKey(key)){
                return;
            }
            RuntimeCpuContants.Server.lastCpuMap.put(key, RuntimeCpuContants.Server.currentCpuMap.get(key));
            RuntimeCpuContants.Server.currentCpuMap.put(key,cpu);
        }
        public static Set<Map.Entry<String, Integer>> getCpuMapEntry() {
            return currentCpuMap.entrySet();
        }


    }

}
