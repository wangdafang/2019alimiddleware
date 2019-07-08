package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dafang
 */
public class RuntimeThreadContants {

    public static class Client{
        private static volatile int threadRatio = 0;

        public static int getThreadRatio() {
            return threadRatio;
        }

        public static void setThreadRatio(int threadRatio) {
            if(threadRatio>100){
                return;
            }
            if (threadRatio <0 ){
                threadRatio = 0;
            }
            RuntimeThreadContants.Client.threadRatio = threadRatio;
        }
    }

    public static class Server{

        private static ConcurrentMap<String, Integer> currentThreadRatioMap = new ConcurrentHashMap();
        private static ConcurrentMap<String, Integer> lastThreadRatioMap = new ConcurrentHashMap();

        static{
            currentThreadRatioMap.putIfAbsent(Contants.PROVIDER_KEY_SMALL,100);
            currentThreadRatioMap.putIfAbsent(Contants.PROVIDER_KEY_MEDIUM,100);
            currentThreadRatioMap.putIfAbsent(Contants.PROVIDER_KEY_LARGE,100);
        }

        public static int getCurrThreadRatio(String key) {
            return currentThreadRatioMap.get(key);
        }

        public static int getLastThreadRatio(String key) {
            return lastThreadRatioMap.get(key);
        }

        public static void setThreadRatio(String key,int threadRatio) {
            if (StringUtils.isBlank(key) || !currentThreadRatioMap.containsKey(key)){
                return;
            }
            int lastValue = RuntimeThreadContants.Server.currentThreadRatioMap.get(key);
            RuntimeThreadContants.Server.lastThreadRatioMap.put(key,RuntimeThreadContants.Server.currentThreadRatioMap.get(key) );
            RuntimeThreadContants.Server.currentThreadRatioMap.put(key,(threadRatio+lastValue)/2);
        }
        public static Set<Map.Entry<String, Integer>> getThreadNumsMapEntry() {
            return currentThreadRatioMap.entrySet();
        }


    }

}
