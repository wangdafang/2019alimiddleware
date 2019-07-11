package com.aliware.tianchi.runner;


import com.aliware.tianchi.RuntimeMaxThreadContants;
import com.aliware.tianchi.ThreadCollector;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dafang
 * 定时任务：定时采集状态为TIMED_WAITING和WAITING的线程数量
 */
public class ThreadCollectRunner {

    private Timer timer = new Timer();

    public ThreadCollectRunner() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                collectThreadNums();
            }
        }, 0, 2);
    }

    public void collectThreadNums() {
        int maxThread = ThreadCollector.getInstance().getWaitThreadCount();

        RuntimeMaxThreadContants.Client.setMaxThreadNums(maxThread);
    }

}
