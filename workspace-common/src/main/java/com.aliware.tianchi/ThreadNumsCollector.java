package com.aliware.tianchi;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author dafang
 */
public class ThreadNumsCollector {
    private static ThreadNumsCollector instance = new ThreadNumsCollector();

    private ThreadMXBean threadBean;

    private ThreadNumsCollector() {
        threadBean = ManagementFactory.getThreadMXBean();
    }

    public static ThreadNumsCollector getInstance() {
        return instance;
    }

    public int[] getWaitThreadCount() {
        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds());
        int[] threadCount = new int[]{0,0};
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo.getThreadName().startsWith("DubboServerHandler")){
                threadCount[1]++;
                if (Thread.State.WAITING.equals(threadInfo.getThreadState()) ) {
                    threadCount[0]++;
                }
            }
        }
        return threadCount;
    }
}
