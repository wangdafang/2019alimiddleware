package com.aliware.tianchi;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

/**
 * @author dafang
 */
public class CpuUsageCollector {
    private static CpuUsageCollector instance = new CpuUsageCollector();

    private OperatingSystemMXBean osMxBean;
    private ThreadMXBean threadBean;
    private long preTime = System.nanoTime();
    private long preUsedTime = 0;

    private CpuUsageCollector() {
        osMxBean = ManagementFactory.getOperatingSystemMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
    }

    public static CpuUsageCollector getInstance() {
        return instance;
    }

    public double getProcessCpuUsage() {
        long totalTime = 0;
        for (long id : threadBean.getAllThreadIds()) {
            totalTime += threadBean.getThreadCpuTime(id);
        }
        long curtime = System.nanoTime();
        long usedTime = totalTime - preUsedTime;
        long totalPassedTime = curtime - preTime;
        preTime = curtime;
        preUsedTime = totalTime;
        return (((double) usedTime) / totalPassedTime / osMxBean.getAvailableProcessors()) * 100;
    }
}
