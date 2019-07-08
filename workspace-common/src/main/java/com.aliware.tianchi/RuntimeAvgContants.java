package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dafang
 */
public class RuntimeAvgContants {

    public static class Client{
        private static volatile int avgCosts = 0;

        public static int getAvgCosts() {
            return avgCosts;
        }

        public static void setAvgCosts(int avgCosts) {
            if(avgCosts>5000){
                return;
            }
            if (avgCosts > 100){
                avgCosts = 100;
            }
            RuntimeAvgContants.Client.avgCosts = (RuntimeAvgContants.Client.avgCosts + avgCosts)/2;
        }
    }

    public static class Server{

        private static ConcurrentMap<String, Integer> currentAvgMap = new ConcurrentHashMap();
        private static ConcurrentMap<String, Integer> lastAvgMap = new ConcurrentHashMap();

        static{
            currentAvgMap.putIfAbsent(Contants.PROVIDER_KEY_SMALL,0);
            currentAvgMap.putIfAbsent(Contants.PROVIDER_KEY_MEDIUM,0);
            currentAvgMap.putIfAbsent(Contants.PROVIDER_KEY_LARGE,0);
            lastAvgMap.putIfAbsent(Contants.PROVIDER_KEY_SMALL,0);
            lastAvgMap.putIfAbsent(Contants.PROVIDER_KEY_MEDIUM,0);
            lastAvgMap.putIfAbsent(Contants.PROVIDER_KEY_LARGE,0);
        }

        public static int getCurrAvgCosts(String key) {
            return currentAvgMap.get(key);
        }

        public static int getLastAvgCosts(String key) {
            return lastAvgMap.get(key);
        }

        public static void setAvgCosts(String key,int avgCosts) {
            if (StringUtils.isBlank(key) || !currentAvgMap.containsKey(key)){
                return;
            }
            RuntimeAvgContants.Server.lastAvgMap.put(key,RuntimeAvgContants.Server.currentAvgMap.get(key));
            RuntimeAvgContants.Server.currentAvgMap.put(key,(RuntimeAvgContants.Server.currentAvgMap.get(key) + avgCosts)/2);
        }
        public static Set<Map.Entry<String, Integer>> getAvgMapEntry() {
            return currentAvgMap.entrySet();
        }


    }

}
