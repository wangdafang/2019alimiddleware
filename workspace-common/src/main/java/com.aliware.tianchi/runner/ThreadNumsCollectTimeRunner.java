package com.aliware.tianchi.runner;

import com.aliware.tianchi.RuntimeThreadContants;
import com.aliware.tianchi.ThreadNumsCollector;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dafang
 * 定时任务：定时采集状态为TIMED_WAITING和WAITING的线程数量
 */
public class ThreadNumsCollectTimeRunner implements Runner  {

    private Timer timer = new Timer();

    public ThreadNumsCollectTimeRunner() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                collectThreadNums();
            }
        }, 0, 1);
    }

    public void collectThreadNums(){
        int[] threadNums = ThreadNumsCollector.getInstance().getWaitThreadCount();
        int currentWaitThreadNums = threadNums[0];
        int currentMaxThreadNums = threadNums[1]==0?1:threadNums[1];
        int ratio = (currentWaitThreadNums*100)/currentMaxThreadNums;
        if (ratio<10 && currentMaxThreadNums<10){
            ratio = 1;
        }
        RuntimeThreadContants.Client.setThreadRatio(ratio);
    }


    public static void main(String[] args) {
        System.out.println(5*100/12);
    }

}
