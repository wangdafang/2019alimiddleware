package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dafang
 */
public class RuntimeMaxThreadContants {

    private static Object block = new Object();

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

        private static volatile ConcurrentMap<String, Integer> lastMaxThreadMap = new ConcurrentHashMap();
        private static volatile ConcurrentMap<String, Integer> lastChangeThreadMap = new ConcurrentHashMap();

        public static int getMaxThreadNums() {
            return totalMaxThread;
        }

        public static synchronized void setMaxThreadNums(String quota, int maxThread) {
            if (!lastMaxThreadMap.containsKey(quota)) {
                synchronized (block) {
                    lastMaxThreadMap.putIfAbsent(quota, maxThread);
                    lastChangeThreadMap.putIfAbsent(quota, maxThread);
                    RuntimeMaxThreadContants.Server.totalMaxThread = RuntimeMaxThreadContants.Server.totalMaxThread + maxThread;
                }
                return;
            }
            int lastMaxThread = lastMaxThreadMap.get(quota);
            if (maxThread > lastMaxThread) {
                int changeValue = maxThread - lastMaxThread;
                synchronized (block){
                    RuntimeMaxThreadContants.Server.totalMaxThread = RuntimeMaxThreadContants.Server.totalMaxThread + changeValue;
                    lastMaxThreadMap.put(quota,maxThread);
                    int lastChange = lastChangeThreadMap.get(quota);
                    lastChangeThreadMap.put(quota,changeValue + lastChange);
                }
            }
        }

        private static Map<String, Integer>  getChangeValue(){
            ConcurrentMap<String, Integer> tmpLastChangeMap = new ConcurrentHashMap();
            tmpLastChangeMap.putAll(lastChangeThreadMap);
            clearLastChangeThreadMap(tmpLastChangeMap);//TODO 此处应该保证lastChangeThread当时没有在被写入
            return Collections.unmodifiableMap(tmpLastChangeMap);
        }

        public static synchronized Map<String, Object> getCurrData(){
            Map<String, Object> tmpData = new HashMap<>();
            tmpData.put("totalMaxThread",totalMaxThread);
            tmpData.put("lastChangeThreadMap",getChangeValue());
            return tmpData;
        }

        public static void clearLastChangeThreadMap(Map<String, Integer> tmpLastChangeMap){
            for (Map.Entry<String,Integer> entry : tmpLastChangeMap.entrySet()){
                lastChangeThreadMap.put(entry.getKey(),lastChangeThreadMap.get(entry.getKey()) - entry.getValue());
            }

        }

    }

}
