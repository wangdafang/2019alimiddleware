
package com.aliware.tianchi;


import com.aliware.tianchi.domain.ProviderAgent;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import sun.management.resources.agent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dafang
 */
public class RingBufferTable {

//    private static final Logger logger = LoggerFactory.getLogger(RingBufferTable.class);

    private static Object block = new Object();

    public static AtomicInteger currentIndex = new AtomicInteger(0);
    public static AtomicInteger enableSize = new AtomicInteger(0);
    public static AtomicInteger disableSize = new AtomicInteger(0);

    public static AtomicInteger groupIndex = new AtomicInteger(0);

    private volatile static int ringTableSize = Contants.RINGBUFFER_TABLE_SIZE;
    private volatile static int newRingTableSize = Contants.RINGBUFFER_TABLE_SIZE;

    private static ConcurrentMap<String, Integer> providerGroup = new ConcurrentHashMap();

    private volatile static ProviderAgent[] ringTable = new ProviderAgent[ringTableSize];

    private static void initRingTable() {
//        logger.info("current ringbuffer table size:" + ringTable.length + ",newRingTableSize:" + newRingTableSize);

//        logger.info("current ringbuffer table size:" + ringTable.length
//                + ",newRingTableSize:" + newRingTableSize
//                + ",blockSize:" + Counter.blockSize.get()
//                + ",randomCount:" + Counter.randomCount.getAndIncrement());

        if (RingBufferTable.ringTable.length >= newRingTableSize) {
            return;
        }
//        logger.info("current ringbuffer table size:" + ringTable.length
//                + ",newRingTableSize:" + newRingTableSize
//                + ",blockSize:" + Counter.blockSize.get()
//                + ",randomCount:" + Counter.randomCount.getAndIncrement());

        Map<String,Object> currData = RuntimeMaxThreadContants.Server.getCurrData();
        int newRingTableSize = (int)currData.get("totalMaxThread");
        Map<String,Integer> changeMap = (Map<String,Integer>)currData.get("lastChangeThreadMap");
        int shouleInitSize = newRingTableSize - RingBufferTable.ringTable.length;
//            logger.info("prepare expand ringbuffer table,should operate:" + shouleInitSize);
        int totalChangeValue = 0;
        if (changeMap!= null && changeMap.size()>0){
            for (Map.Entry<String,Integer> entry : changeMap.entrySet()){
                totalChangeValue += entry.getValue();
            }

            if (totalChangeValue != shouleInitSize){
//                    logger.info("operate nums is not equals,totalChangeValue:"+totalChangeValue+",shouleInitSize:"+shouleInitSize);
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
//            logger.info("new ring table size : " + ringTable.length);
        System.arraycopy(RingBufferTable.ringTable, 0, ringTable, 0, RingBufferTable.ringTable.length);
        //对新数组打乱顺序
        ringTable = RingBufferTable.randomRingTable(ringTable,newRingTableSize);
        RingBufferTable.ringTable = ringTable;
        RingBufferTable.ringTableSize = newRingTableSize;
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

    public static ProviderAgent getProviderByIndex(int index){
        if (index <0 || index > RingBufferTable.ringTableSize){
            return null;
        }
        return RingBufferTable.ringTable[index];
    }

    public static ProviderAgent getNextValidProvider() {
        int index = RingBufferTable.currentIndex.getAndIncrement();
        int ringTableSize = RingBufferTable.ringTableSize;
        if (ringTableSize == 0){
            return null;
        }
        int i = (index) % ringTableSize;
        do {
            if (RingBufferTable.ringTable[i].isValid()) {
                RingBufferTable.ringTable[i].disable();
//                RingBufferTable.ringTable[i].setTimestamp(System.currentTimeMillis());
                return RingBufferTable.ringTable[i];
            }
            i = (++i) % ringTableSize;
        } while (i != index);

        return null;
    }

    public static void resetRingTable(){
        for (ProviderAgent agent : RingBufferTable.ringTable) {
            agent.enable();
        }
    }

    public static void mkProviderDisable(int group){
        for (ProviderAgent agent : RingBufferTable.ringTable) {
            if (agent.getGroup() == group){
                agent.disable();
            }
        }
    }

    public static void mkProviderEnable(int group){
        for (ProviderAgent agent : RingBufferTable.ringTable) {
            if (agent.getGroup() == group){
                agent.enable();
            }
        }
    }

    public static void clearTimeout(){
        int currentTimeStamp = (int)(System.currentTimeMillis()%10000);
        for (ProviderAgent agent : RingBufferTable.ringTable) {
            int tmpTimeStamp = (int)(agent.getTimestamp()%10000);
            if (currentTimeStamp-tmpTimeStamp > 100){
                agent.enable();
            }
        }
    }

    public static int getAndSetGroup(String quota){
        if (RingBufferTable.providerGroup.containsKey(quota)){
            return RingBufferTable.providerGroup.get(quota);
        }
        int group = RingBufferTable.groupIndex.getAndIncrement();
        RingBufferTable.providerGroup.putIfAbsent(quota,group);
        return group;
    }

    public static Set<String> getAllGroupKey(){
        return RingBufferTable.providerGroup.keySet();
    }

    public static void disableOne(int index) {
        if (index < 0 || index > RingBufferTable.ringTableSize) {
            return;
        }
        RingBufferTable.ringTable[index].disable();
//        Counter.blockSize.getAndIncrement();
    }

    public static void enableOne(int index) {
        if (index < 0 || index > RingBufferTable.ringTableSize) {
            return;
        }
        RingBufferTable.ringTable[index].enable();
//        Counter.blockSize.getAndDecrement();
    }

    public static  ProviderAgent[] forTestGetRingTable(){
        return RingBufferTable.ringTable;
    }

    public static void main(String[] args) {

        int total = 0;
        for(int i=0;i<10;i++){
            int group = i%3;
            total = i+1;
            RuntimeMaxThreadContants.Server.setMaxThreadNums(group+"",total);

//            RingBufferTable.resetNewRingTableSize(RuntimeMaxThreadContants.Server.getMaxThreadNums());

            RingBufferTable.expandTable();
            System.out.print("group-[");
            for(int j=0;j<RingBufferTable.ringTable.length;j++){
                System.out.print(RingBufferTable.ringTable[j].getGroup()+",");
            }
            System.out.println("]");
//            System.out.print("index-[");
//            for(int j=0;j<ringTable.length;j++){
//                System.out.print(ringTable[j].getIndex()+",");
//            }
//            logger.info("]");

        }


    }

}
