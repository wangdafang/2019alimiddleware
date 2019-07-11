
package com.aliware.tianchi;


import com.aliware.tianchi.domain.ProviderAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dafang
 */
public class RingBufferTable {

    public static AtomicInteger currentIndex = new AtomicInteger(0);
    public static AtomicInteger enableSize = new AtomicInteger(0);
    public static AtomicInteger disableSize = new AtomicInteger(0);

    public static AtomicInteger groupIndex = new AtomicInteger(0);

    private volatile static int ringTableSize = Contants.RINGBUFFER_TABLE_SIZE;
    private volatile static int newRingTableSize = Contants.RINGBUFFER_TABLE_SIZE;

    private static ConcurrentMap<String, Integer> providerGroup = new ConcurrentHashMap();

    public volatile static ProviderAgent[] ringTable = new ProviderAgent[ringTableSize];

//    static{
//        for (int i=0;i<Contants.RINGBUFFER_TABLE_SIZE;i++){
//            ringTable[i] = new ProviderAgent(i%3,true,(i%3)+"",i);//FIXME 此处的用意是为了让程序在初始化的时候，可以有请求打到每个provider上，后面需要优化
//        }
//    }

    private static void initRingTable() {
        if (ringTable.length < newRingTableSize) {
            int shouleInitSize = newRingTableSize - ringTable.length;
//            System.out.println("prepare expand ringbuffer table,should operate:" + shouleInitSize);
            Map<String,Integer> changeMap = RuntimeMaxThreadContants.Server.getChangeValue();
            if (changeMap!= null && changeMap.size()>0){
                int totalChangeValue = 0;
                for (Map.Entry<String,Integer> entry : changeMap.entrySet()){
                    totalChangeValue += entry.getValue();
                }
                if (totalChangeValue != shouleInitSize){
//                    System.out.println("operate nums is not equals,totalChangeValue:"+totalChangeValue+",shouleInitSize:"+shouleInitSize);
                    return;
                }
            }

            ProviderAgent[] ringTable = new ProviderAgent[newRingTableSize];
            int ringSize = RingBufferTable.ringTable.length;

            for (Map.Entry<String,Integer> entry : changeMap.entrySet()){
                int begin = ringSize;
                int end = ringSize + entry.getValue();
                for (int i = begin; i < end; i++) {
                    ringTable[i] = new ProviderAgent(RingBufferTable.getAndSetGroup(entry.getKey()),true,entry.getKey(),i);
                }
                ringSize = end;
            }
//            System.out.println("new ring table size : " + ringTable.length);
            System.arraycopy(RingBufferTable.ringTable, 0, ringTable, 0, RingBufferTable.ringTable.length);
            //对新数组打乱顺序
            ringTable = RingBufferTable.randomRingTable(ringTable,RingBufferTable.newRingTableSize);
            RingBufferTable.ringTable = ringTable;
            RingBufferTable.ringTableSize = RingBufferTable.newRingTableSize;
            RuntimeMaxThreadContants.Server.clearLastChangeThreadMap();
        }
    }

    private static ProviderAgent[] randomRingTable(ProviderAgent[] ringTable,int size) {
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int p = random.nextInt(size);
            ProviderAgent tmp = ringTable[i];
            ringTable[i] = ringTable[p];
            ringTable[p] = tmp;
            ringTable[i].setIndex(i);
            ringTable[p].setIndex(p);

        }
        random = null;
        return ringTable;
    }

    public static void resetNewRingTableSize(int newRingTableSize) {
        RingBufferTable.newRingTableSize = newRingTableSize;
    }

    public static int getRingTableSize() {
        return ringTableSize;
    }

    /**
     * table扩容
     */
    public static void expandTable() {
        initRingTable();
    }

    public static ProviderAgent getNextValidProvider() {
        int index = currentIndex.get();
        int ringTableSize = RingBufferTable.ringTableSize;
        if (ringTableSize == 0){
            return null;
        }
        int i = (index) % ringTableSize;
        do {
            if (ringTable[i].isValid()) {
                ringTable[i].disable();
                return ringTable[i];
            }
            i = (++i) % ringTableSize;
        } while (i != index);

        return null;
    }

    public static int getAndSetGroup(String quota){
        if (providerGroup.containsKey(quota)){
            return providerGroup.get(quota);
        }
        int group = groupIndex.getAndIncrement();
        providerGroup.putIfAbsent(quota,group);
        return group;
    }

    public static void disableOne(int index) {
        if (index < 0 || index > RingBufferTable.ringTableSize) {
            return;
        }
        ringTable[index].disable();
    }

    public static void enableOne(int index) {
        if (index < 0 || index > RingBufferTable.ringTableSize) {
            return;
        }
        ringTable[index].enable();
    }

    public static void main(String[] args) {

        int total = 0;
        for(int i=0;i<10;i++){
            int group = i%3;
            total = i+1;
            RuntimeMaxThreadContants.Server.setMaxThreadNums(group+"",total);

            RingBufferTable.resetNewRingTableSize(RuntimeMaxThreadContants.Server.getMaxThreadNums());

            RingBufferTable.expandTable();
            System.out.print("group-[");
            for(int j=0;j<ringTable.length;j++){
                System.out.print(ringTable[j].getGroup()+",");
            }
            System.out.println("]");
//            System.out.print("index-[");
//            for(int j=0;j<ringTable.length;j++){
//                System.out.print(ringTable[j].getIndex()+",");
//            }
//            System.out.println("]");

        }


    }

}
