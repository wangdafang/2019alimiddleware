package com.aliware.tianchi;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.transport.RequestLimiter;

/**
 * @author daofeng.xjf
 *
 * 服务端限流
 * 可选接口
 * 在提交给后端线程池之前的扩展，可以用于服务端控制拒绝请求
 */
public class TestRequestLimiter implements RequestLimiter {
    private static final Logger logger = LoggerFactory.getLogger(TestRequestLimiter.class);


    private static final String[] turntableNames = new String[]{"small","medium","large"};

    /**
     * @param request 服务请求
     * @param activeTaskCount 服务端对应线程池的活跃线程数
     * @return  false 不提交给服务端业务线程池直接返回，客户端可以在 Filter 中捕获 RpcException
     *          true 不限流
     */
    @Override
    public boolean tryAcquire(Request request, int activeTaskCount) {
//        System.out.println("ratio:"+RuntimeThreadContants.Client.getThreadRatio());
//        System.out.println("cpu:"+RuntimeCpuContants.Client.getCpuUsage());
        String quota = System.getProperty("quota");
        int index = 0;
        for (int i=0;i<turntableNames.length;i++){
            if (turntableNames[i].equals(quota)){
                index = i;
            }
        }
        int maxValue = 0;
        switch(index%3){
            case 0:
                maxValue = Contants.PROVIDER_MAX_THREAD_NUM_SMALL;
                break;
            case 1:
                maxValue = Contants.PROVIDER_MAX_THREAD_NUM_MEDIUM;
                break;
            case 2:
                maxValue = Contants.PROVIDER_MAX_THREAD_NUM_LARGE;
                break;
            default:
                throw new IllegalArgumentException("can't find key:"+quota+" in turntable indexes");
        }
//        System.out.println("quota" + quota + "active:" + activeTaskCount + ",max:" + maxValue + ",max85%:" + maxValue*85*0.01);
        if (activeTaskCount > maxValue*85*0.01){
            return false;
        }
//        System.out.println("total call:" + Counter.currentIndex.getAndIncrement());
        if ((Counter.currentIndex.getAndIncrement() % 1000) == 0){
            logger.info("quota:"+quota + ",total call:"+ Counter.currentIndex.get());
        }
        return true;
    }

}
