package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dafang
 */
public class RuntimeContants {

    static class Client{
        private static volatile int avgCosts = 0;

        public static int getAvgCosts() {
            return avgCosts;
        }

        public static void setAvgCosts(int avgCosts) {
            if(avgCosts>5000){
                return;
            }
            RuntimeContants.Client.avgCosts = (RuntimeContants.Client.avgCosts + avgCosts)/2;
        }
    }

    static class Server{

        private static ConcurrentMap<String, Integer> currentAvgMap = new ConcurrentHashMap();
        private static ConcurrentMap<String, Integer> lastAvgMap = new ConcurrentHashMap();

        static{
            currentAvgMap.putIfAbsent("small",0);
            currentAvgMap.putIfAbsent("medium",0);
            currentAvgMap.putIfAbsent("large",0);
        }

        public static int getCurrAvgCosts(String key) {
            return currentAvgMap.get(key);
        }

        public static int getLastAvgCosts(String key) {
            return lastAvgMap.get(key);
        }

        public static void setAvgCosts(String key,int avgCosts) {
            if (StringUtils.isBlank(key) || !currentAvgMap.containsKey(currentAvgMap)){
                return;
            }
            RuntimeContants.Server.lastAvgMap.put(key,RuntimeContants.Server.currentAvgMap.get(key));
            RuntimeContants.Server.currentAvgMap.put(key,(RuntimeContants.Server.currentAvgMap.get(key) + avgCosts)/2);
        }
        public static Set<Map.Entry<String, Integer>> getAvgMapEntry() {
            return currentAvgMap.entrySet();
        }


    }

}
