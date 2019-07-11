


package com.aliware.tianchi;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author dafang
 */
public class ThreadCollector {

    private static class ThreadCollectHoler{
        private static ThreadCollector INSTANCE = new ThreadCollector();
    }

    public static ThreadCollector getInstance(){
        return ThreadCollectHoler.INSTANCE;
    }

    private ThreadMXBean threadBean;

    private ThreadCollector() {
        threadBean = ManagementFactory.getThreadMXBean();
    }

    public int getWaitThreadCount() {
        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds());
        int threadCount = 0;
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo.getThreadName().startsWith("DubboServerHandler")) {
                ++threadCount;
            }
        }
        return threadCount;
    }
}
