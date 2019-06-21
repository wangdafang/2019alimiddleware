package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.listener.CallbackListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author daofeng.xjf
 *
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 *
 */
public class CallbackListenerImpl implements CallbackListener {
    private Timer timer = new Timer();

    public CallbackListenerImpl() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                calculateIndex();
            }
        }, 0, 300);
    }

    @Override
    public void receiveServerMsg(String msg) {
        if (StringUtils.isBlank(msg)){
            return;
        }
        String[] params = msg.split(":");
        RuntimeContants.Server.setAvgCosts(params[0],Integer.parseInt(params[1]));
    }

    /**
     * 计算下一个节点的方式：
     *  如果响应时间大于50ms，删除turntable的20%个格子，以后每加10ms删除10%
     *  如果响应时间下降10ms，则添加10%的格子，每下降10ms添加10%的格子
     */
    public void calculateIndex(){
        for(Map.Entry<String, Integer> entry : RuntimeContants.Server.getAvgMapEntry()){
            int costChange = RuntimeContants.Server.getCurrAvgCosts(entry.getKey()) - RuntimeContants.Server.getLastAvgCosts(entry.getKey());
            int sign = 0;
            if (costChange<0){ //说明性能提升了
                int index = TurntableUtils.getTurntableNameIndex(entry.getKey());
                TurntableUtils.batchAddProviderAgent(TurntableUtils.getIndexesSize(index)/10,index);
            } else { // 说明性能下降了
                int index = TurntableUtils.getTurntableNameIndex(entry.getKey());
                TurntableUtils.batchDeleteProviderAgent(TurntableUtils.getIndexesSize(index)/10,index);
            }
        }

        System.out.println("smallIndexes:"+TurntableUtils.getIndexesSize(0)+",mediumIndexes:"+
                TurntableUtils.getIndexesSize(1)+",largeIndexes:"+
                TurntableUtils.getIndexesSize(2)+",blankIndexes:"+
                TurntableUtils.getIndexesSize(3));

    }


















//
//    public static AtomicLong currentIndex=new AtomicLong(1);
//    public static void main(String[] args) {
//
//        Thread run = new Thread() {
//            @Override
//            public void run() {
//                synchronized (currentIndex){
//                    int i = 0;
//                    while(!currentIndex.compareAndSet(currentIndex.get(),0) && i<10000){
//                        System.out.println("inner:"+currentIndex.get() + "" + i);
//                        currentIndex.getAndIncrement();
//                    }
//                }
//            }
//        };
//        run.start();
//        int i = 0;
//        System.out.println(currentIndex.get());
//        System.out.println(currentIndex.getAndIncrement());
//        System.out.println(currentIndex.get());
//        while(!currentIndex.compareAndSet(currentIndex.get(),0) && i<10000){
//            System.out.println("outer"+currentIndex.get() + "" + i);
//            currentIndex.getAndIncrement();
//        }
//        System.out.println(currentIndex.get());
//        System.out.println(currentIndex.getAndIncrement());
//        System.out.println(currentIndex.get());
//    }


}
