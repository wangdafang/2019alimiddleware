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
        }, 0, 500);
    }


    /**
     *
     */
    public void calculateIndex() {

        System.out.println("added:smallIndexes:" + TurntableUtils.getIndexesSize(0) + ",mediumIndexes:" +
                TurntableUtils.getIndexesSize(1) + ",largeIndexes:" +
                TurntableUtils.getIndexesSize(2) + ",blankIndexes:" +
                TurntableUtils.getIndexesSize(3));
//
        System.out.println("avgCosts:small:" + RuntimeAvgContants.Server.getCurrAvgCosts(Contants.PROVIDER_KEY_SMALL) + ",medium:" +
                RuntimeAvgContants.Server.getCurrAvgCosts(Contants.PROVIDER_KEY_MEDIUM) + ",large:" +
                RuntimeAvgContants.Server.getCurrAvgCosts(Contants.PROVIDER_KEY_LARGE) );

        System.out.println("cpu:small:" + RuntimeCpuContants.Server.getCurrCpuUsage(Contants.PROVIDER_KEY_SMALL) + ",medium:" +
                RuntimeCpuContants.Server.getCurrCpuUsage(Contants.PROVIDER_KEY_MEDIUM) + ",large:" +
                RuntimeCpuContants.Server.getCurrCpuUsage(Contants.PROVIDER_KEY_LARGE) );

        System.out.println("thread:small:" + RuntimeThreadContants.Server.getCurrThreadRatio(Contants.PROVIDER_KEY_SMALL) + ",medium:" +
                RuntimeThreadContants.Server.getCurrThreadRatio(Contants.PROVIDER_KEY_MEDIUM) + ",large:" +
                RuntimeThreadContants.Server.getCurrThreadRatio(Contants.PROVIDER_KEY_LARGE) );

        System.out.println("system-usablity:small:" + RuntimeSysUsablityContants.getSystemUsablity((Contants.PROVIDER_KEY_SMALL)) + ",medium:" +
                RuntimeSysUsablityContants.getSystemUsablity(Contants.PROVIDER_KEY_MEDIUM) + ",large:" +
                RuntimeSysUsablityContants.getSystemUsablity(Contants.PROVIDER_KEY_LARGE) );

        System.out.println();

//        System.out.print("[");
//        for(int i=0;i<30;i++){
//            System.out.print(TurntableUtils.getIndexProviderAgent(i).getGroup()+",");
//        }
//        System.out.println("]");

    }


}
