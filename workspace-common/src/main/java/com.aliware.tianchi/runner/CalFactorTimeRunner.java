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
        }, 0, 1);
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
            RuntimeSysUsablityContants.setCurrentUsablityMap(TurntableUtils.turntableNames[i],usability);
            totalUsability += usability;
            usabilityMap.put(i,usability);
//            sb.append("group:").append(TurntableUtils.turntableNames[i]).append(",usability:").append(usability).append("\n");
        }
//        System.out.println(sb.toString());
        //计算总占比
        Map<Integer ,Double > proportionMap = new HashMap<>();
        double totalProportion = 0d;
        for(Map.Entry<Integer ,Integer> entry:usabilityMap.entrySet()) {
            Double providerProportion = calProportion(entry.getKey(),entry.getValue());
            proportionMap.put(entry.getKey(),providerProportion);
            totalProportion += providerProportion;
        }

        //空出来需要变换的插槽位置
        for(Map.Entry<Integer ,Double> entry:proportionMap.entrySet()) {
            int turntableNums = calTurntableNums(entry.getValue(), totalProportion);

            int size = TurntableUtils.getIndexesSize(entry.getKey());

            if (size > turntableNums) {
                TurntableUtils.batchDeleteProviderAgent(size - turntableNums, entry.getKey());
            }
        }


        //重新分配空闲的插槽
        for(Map.Entry<Integer ,Double> entry:proportionMap.entrySet()) {
            int turntableNums = calTurntableNums(entry.getValue(), totalProportion);

            int size = TurntableUtils.getIndexesSize(entry.getKey());

            if (size == turntableNums){
                continue;
            }
            //插拔插槽
            if (size < turntableNums){
                TurntableUtils.batchAddProviderAgent(turntableNums - size,entry.getKey());

            }

        }
//        RuntimeSysUsablityContants.setSystemUsability(totalUsability);

    }

    /**
     */
//    public void calculateIndex(){
//
//        Map<Integer ,Integer > usabilityMap = new HashMap<>();
//        int totalUsability = 0;
//        //计算usability
//        for(int i=0;i<3;i++) {
//            int rt = RuntimeAvgContants.Server.getCurrAvgCosts(TurntableUtils.turntableNames[i]);
//            int cpu = RuntimeCpuContants.Server.getCurrCpuUsage(TurntableUtils.turntableNames[i]);
//            int thread = 100 - RuntimeThreadContants.Server.getCurrThreadRatio(TurntableUtils.turntableNames[i]);
////            int usability = 0 ;
////            if (thread>75){
////                usability = 0;
////            } else {
////                usability = calUsability(rt, cpu, thread);
////            }
//            int usability = calUsability(rt, cpu, thread);
////            System.out.println("rt:" + rt + ",cpu:" + cpu + ",thread:" + thread);
//            totalUsability += usability;
//            usabilityMap.put(i,usability);
//        }
//
//        RuntimeSysUsablityContants.setSystemUsability(totalUsability);
//
//        //空出来需要变换的插槽位置
//        for(Map.Entry<Integer ,Integer> entry:usabilityMap.entrySet()) {
//            int turntableNums = calTurntableNums(entry.getValue(), totalUsability);
//
//            int size = TurntableUtils.getIndexesSize(entry.getKey());
//
//            if (size > turntableNums) {
//                TurntableUtils.batchDeleteProviderAgent(size - turntableNums, entry.getKey());
//            }
//        }
//
//
//        //重新分配空闲的插槽
//        for(Map.Entry<Integer ,Integer> entry:usabilityMap.entrySet()) {
//            int turntableNums = calTurntableNums(entry.getValue(), totalUsability);
//
//            int size = TurntableUtils.getIndexesSize(entry.getKey());
//
//            if (size == turntableNums){
//                continue;
//            }
//            //插拔插槽
//            if (size < turntableNums){
//                TurntableUtils.batchAddProviderAgent(turntableNums - size,entry.getKey());
//
//            }
//
//        }
//
//        System.out.println("added:smallIndexes:"+TurntableUtils.getIndexesSize(0)+",mediumIndexes:"+
//                TurntableUtils.getIndexesSize(1)+",largeIndexes:"+
//                TurntableUtils.getIndexesSize(2)+",blankIndexes:"+
//                TurntableUtils.getIndexesSize(3));
//
//    }

    /**
     * 根据可用率计算插槽个数
     * @param usability
     * @return
     */
    private int calTurntableNums(int usability , int totalUsability) {
        return (usability * Contants.TURNTABLE_SIZE_PER_PROVIDER * 3)  / (totalUsability==0?1:totalUsability) ;
    }

    /**
     * 根据可用率计算插槽个数
     * @param proportion 占比
     * @param totalProportion 总占比
     * @return
     */
    private int calTurntableNums(double proportion , double totalProportion) {
        return (int)((proportion * Contants.TURNTABLE_SIZE_PER_PROVIDER * 3)  / (totalProportion==0d?1d:totalProportion)) ;
    }

    /**
     * 计算占比
     * @param usability
     * @return
     */
    private Double calProportion(int key,int usability ) {
//        double providerKeyPercent = (double)(key+1) /(double)6;
        if (RuntimeSysUsablityContants.getSystemUsablity(TurntableUtils.turntableNames[key]) < 15){//如果系统可用率小于15%，直接占比为0
            return 0d;
        }

//        double providerKeyPercent = 0d;
//        switch(key%3){
//            case 0:
////                providerKeyPercent = (double)140/(double)1150;
//                providerKeyPercent = (double)6/(double)29;
//                break;
//            case 1:
////                providerKeyPercent = (double)430/(double)1150;
//                providerKeyPercent = (double)10/(double)29;
//                break;
//            case 2:
////                providerKeyPercent = (double)580/(double)1150;
//                providerKeyPercent = (double)13/(double)29;
//                break;
//            default:
//                break;
//        }
        double providerKeyPercent = 1d;

        return providerKeyPercent * (double)usability;
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
//        return Contants.MAX_USABILITY - (rt * 30 + cpu * 10 + (100-thread) * 60) /100;
//        return Contants.MAX_USABILITY - (thread * 100) /100;
//        return (thread * 100) /100;
        if (cpu > 80 || thread < 20 || rt > 400){//如果cpu超过90或者可用线程数小于20，则直接认为系统可用率为0
            return 0;
        }
        return ((100 - rt) * 55 + thread * 10 +  (100 - cpu) * 35) /100;

    }


}
