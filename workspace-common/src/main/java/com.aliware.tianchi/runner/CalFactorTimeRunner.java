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
public class CalFactorTimeRunner implements Runner {

    private Timer timer = new Timer();

    public CalFactorTimeRunner() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
//                    calculateIndex();
                    calculateIndexConsisHash();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5);
    }

    /**
     */
    public void calculateIndexConsisHash(){

        Map<Integer ,Integer > usabilityMap = new HashMap<>();
        int totalUsability = 0;
        int totalUnUsability = 0;
        StringBuffer sb = new StringBuffer();
        //计算usability
        for(int i=0;i<3;i++) {
            int rt = RuntimeAvgContants.Server.getCurrAvgCosts(TurntableUtils.turntableNames[i]);
            int cpu = RuntimeCpuContants.Server.getCurrCpuUsage(TurntableUtils.turntableNames[i]);
            int thread = RuntimeThreadContants.Server.getCurrThreadRatio(TurntableUtils.turntableNames[i]);
            int usability = calUsability(rt, cpu, thread);
            totalUsability += usability;
            usabilityMap.put(i,usability);
//            sb.append("group:").append(TurntableUtils.turntableNames[i]).append(",usability:").append(usability).append("\n");
//            if (usability >80) {
//                totalUnUsability ++ ;
//                TurntableUtils.setIndexVolidAttr(i, false);
//            } else {
//                if (usability < 30){
//                    TurntableUtils.setIndexVolidAttr(i, true);
//                }
//            }
        }
//        System.out.println(sb.toString());

        //空出来需要变换的插槽位置
        for(Map.Entry<Integer ,Integer> entry:usabilityMap.entrySet()) {
            int turntableNums = calTurntableNums(entry.getValue(), totalUsability);

            int size = TurntableUtils.getIndexesSize(entry.getKey());

            if (size > turntableNums) {
                TurntableUtils.batchDeleteProviderAgent(size - turntableNums, entry.getKey());
            }
        }


        //重新分配空闲的插槽
        for(Map.Entry<Integer ,Integer> entry:usabilityMap.entrySet()) {
            int turntableNums = calTurntableNums(entry.getValue(), totalUsability);

            int size = TurntableUtils.getIndexesSize(entry.getKey());

            if (size == turntableNums){
                continue;
            }
            //插拔插槽
            if (size < turntableNums){
                TurntableUtils.batchAddProviderAgent(turntableNums - size,entry.getKey());

            }

        }
//        RuntimeSysUsablityContants.setSystemUsability(totalUnUsability * 33);

    }

    /**
     */
    public void calculateIndex(){

        Map<Integer ,Integer > usabilityMap = new HashMap<>();
        int totalUsability = 0;
        //计算usability
        for(int i=0;i<3;i++) {
            int rt = RuntimeAvgContants.Server.getCurrAvgCosts(TurntableUtils.turntableNames[i]);
            int cpu = RuntimeCpuContants.Server.getCurrCpuUsage(TurntableUtils.turntableNames[i]);
            int thread = 100 - RuntimeThreadContants.Server.getCurrThreadRatio(TurntableUtils.turntableNames[i]);
//            int usability = 0 ;
//            if (thread>75){
//                usability = 0;
//            } else {
//                usability = calUsability(rt, cpu, thread);
//            }
            int usability = calUsability(rt, cpu, thread);
//            System.out.println("rt:" + rt + ",cpu:" + cpu + ",thread:" + thread);
            totalUsability += usability;
            usabilityMap.put(i,usability);
        }

        RuntimeSysUsablityContants.setSystemUsability(totalUsability);

        //空出来需要变换的插槽位置
        for(Map.Entry<Integer ,Integer> entry:usabilityMap.entrySet()) {
            int turntableNums = calTurntableNums(entry.getValue(), totalUsability);

            int size = TurntableUtils.getIndexesSize(entry.getKey());

            if (size > turntableNums) {
                TurntableUtils.batchDeleteProviderAgent(size - turntableNums, entry.getKey());
            }
        }


        //重新分配空闲的插槽
        for(Map.Entry<Integer ,Integer> entry:usabilityMap.entrySet()) {
            int turntableNums = calTurntableNums(entry.getValue(), totalUsability);

            int size = TurntableUtils.getIndexesSize(entry.getKey());

            if (size == turntableNums){
                continue;
            }
            //插拔插槽
            if (size < turntableNums){
                TurntableUtils.batchAddProviderAgent(turntableNums - size,entry.getKey());

            }

        }

        System.out.println("added:smallIndexes:"+TurntableUtils.getIndexesSize(0)+",mediumIndexes:"+
                TurntableUtils.getIndexesSize(1)+",largeIndexes:"+
                TurntableUtils.getIndexesSize(2)+",blankIndexes:"+
                TurntableUtils.getIndexesSize(3));

    }

    /**
     * 根据可用率计算插槽个数
     * @param usability
     * @return
     */
    private int calTurntableNums(int usability , int totalUsability) {
        return (usability * Contants.TURNTABLE_SIZE_PER_PROVIDER * 3)  / (totalUsability==0?1:totalUsability) ;
    }

    /**
     * 根据算法计算可用率
     *  计算下一个节点的方式：
     *      rt*10% + cpu*20% +（100-thread）*70% = usability
     * @param rt
     * @param cpu
     * @param thread
     * @return
     */
    private int calUsability(int rt, int cpu, int thread) {
//        return Contants.MAX_USABILITY - (rt * 10 + cpu * 20 + thread * 70) /100;
//        return Contants.MAX_USABILITY - (thread * 100) /100;
        return (thread * 100) /100;
    }

}
