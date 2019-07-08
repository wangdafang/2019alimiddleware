package com.aliware.tianchi.runner;

import com.aliware.tianchi.CpuUsageCollector;
import com.aliware.tianchi.RuntimeCpuContants;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dafang
 * 定时任务：定时采集cpu使用率
 */
public class CpuUsageCollectTimeRunner implements Runner  {

    private Timer timer = new Timer();

    public CpuUsageCollectTimeRunner() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                collectCpuUsage();
            }
        }, 0, 1);
    }

    public void collectCpuUsage(){
        RuntimeCpuContants.Client.setCpuUsage((int) (CpuUsageCollector.getInstance().getProcessCpuUsage()/1));
    }

}
