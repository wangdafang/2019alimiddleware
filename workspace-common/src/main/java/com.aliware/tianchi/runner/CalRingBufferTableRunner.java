package com.aliware.tianchi.runner;

import com.aliware.tianchi.*;
import com.sun.org.apache.xpath.internal.operations.String;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dafang
 * 定时任务：负责根据客户端上报的数据，试试计算出各个provider的插槽装填因子
 */
public class CalRingBufferTableRunner {

    private Timer timer = new Timer();

    private static int runTimes = 0;

    public CalRingBufferTableRunner() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (runTimes++>2000){
                        this.cancel();
                        return;
                    }
                    calculateRingBufferTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5);
    }

    /**
     * 计算ringbufferTable的大小
     */
    public void calculateRingBufferTable(){
        RingBufferTable.resetNewRingTableSize(RuntimeMaxThreadContants.Server.getMaxThreadNums());

        RingBufferTable.expandTable();
    }

}
