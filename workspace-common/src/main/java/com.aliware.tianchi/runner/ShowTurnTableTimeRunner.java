package com.aliware.tianchi.runner;

import com.aliware.tianchi.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dafang
 * 定时任务：负责根据客户端上报的数据，试试计算出各个provider的插槽装填因子
 */
public class ShowTurnTableTimeRunner implements Runner {

    private Timer timer = new Timer();

    public ShowTurnTableTimeRunner() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    calculateIndex();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 200);
    }


    /**
     */
    public void calculateIndex(){

        System.out.println("added:smallIndexes:"+TurntableUtils.getIndexesSize(0)+",mediumIndexes:"+
                TurntableUtils.getIndexesSize(1)+",largeIndexes:"+
                TurntableUtils.getIndexesSize(2)+",blankIndexes:"+
                TurntableUtils.getIndexesSize(3));

    }


}
