package com.aliware.tianchi.runner;


import com.aliware.tianchi.Counter;
import com.aliware.tianchi.RingBufferTable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dafang
 * 定时任务：对系统进行健康检查，发现有线程池满等现象， 停止这台机器的分发
 */
public class DocterCheckerRunner {

    private Timer timer = new Timer();

    private static List<Integer> lastDisabled = new ArrayList<>();

    public DocterCheckerRunner() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                healthCheck();
            }
        }, 10000, 2);
    }

    public void healthCheck() {
        ConcurrentMap<Integer, AtomicInteger> exceptionList = Counter.exceptionList;
        if (exceptionList != null || exceptionList.size()>0) {
            for(Map.Entry<Integer, AtomicInteger> entry : exceptionList.entrySet()){
                if (entry.getValue().get() > 0){
                    if (lastDisabled.contains(entry.getKey())){
                        continue;
                    }
                    RingBufferTable.mkProviderDisable(entry.getKey());
                    lastDisabled.add(entry.getKey());
                } else if (lastDisabled.contains(entry.getKey())){
                    RingBufferTable.mkProviderEnable(entry.getKey());
                    lastDisabled.remove(entry.getKey());
                    Counter.exceptionList.put(entry.getKey(),new AtomicInteger(0));
                }
            }
        }

    }

}
