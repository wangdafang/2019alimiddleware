package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dafang
 */
public class RuntimeMaxThreadContants {

    public static class Client {
        private static volatile int maxThread = 0;

        public static int getMaxThreadNums() {
            return maxThread;
        }

        public static void setMaxThreadNums(int maxThread) {
            RuntimeMaxThreadContants.Client.maxThread = maxThread;
        }
    }

    public static class Server {

        private static volatile int totalMaxThread = 0;
        private static volatile int lastMaxThread = 0;

        private static ConcurrentMap<String, Integer> lastMaxThreadMap = new ConcurrentHashMap();
        private static ConcurrentMap<String, Integer> lastChangeThreadMap = new ConcurrentHashMap();

        public static int getMaxThreadNums() {
            return totalMaxThread;
        }

        public static void setMaxThreadNums(String quota, int maxThread) {
            if (!lastMaxThreadMap.containsKey(quota)) {
                lastMaxThreadMap.putIfAbsent(quota, maxThread);
                lastChangeThreadMap.putIfAbsent(quota, maxThread);
                RuntimeMaxThreadContants.Server.totalMaxThread = RuntimeMaxThreadContants.Server.totalMaxThread + maxThread;
                return;
            }
            int lastMaxThread = lastMaxThreadMap.get(quota);
            if (maxThread > lastMaxThread) {
                int changeValue = maxThread - lastMaxThread;
                RuntimeMaxThreadContants.Server.totalMaxThread = RuntimeMaxThreadContants.Server.totalMaxThread + changeValue;
                lastMaxThreadMap.put(quota,maxThread);
                int lastChange = lastChangeThreadMap.get(quota);
                lastChangeThreadMap.put(quota,changeValue + lastChange);
            }
        }

        public static Map<String, Integer>  getChangeValue(){
            return Collections.unmodifiableMap(lastChangeThreadMap);
        }

        public static void clearLastChangeThreadMap(){
            for (Map.Entry<String,Integer> entry : lastChangeThreadMap.entrySet()){
                lastChangeThreadMap.put(entry.getKey(),0);
            }
        }

    }

}
