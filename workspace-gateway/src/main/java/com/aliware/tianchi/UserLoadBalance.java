package com.aliware.tianchi;

import com.sun.tracing.ProviderFactory;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author daofeng.xjf
 *
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {



    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {

        //查看系统总可用性，当系统总可用性低于10%时，拒绝所有请求
//        System.out.println("system usablity:" + RuntimeSysUsablityContants.getSystemUsability());
//        if ((Contants.MAX_USABILITY * 3 - RuntimeSysUsablityContants.getSystemUsability()) < Contants.MAX_USABILITY*0.2*3){
//            return null;
//        }
//        System.out.println("system usablity:" + RuntimeSysUsablityContants.getSystemUsability());
//        if ((Contants.MAX_USABILITY * 3 - RuntimeSysUsablityContants.getSystemUsability()) <50){
//            throw new IllegalAccessError("STSTEM IS NOT VALID");
//        }

        return invokers.get(getNextIndex());
    }


    public int getNextIndex(){

        /**
         * 随机算法(Contants.TURNTABLE_SIZE_PER_PROVIDER *3 个插槽)
         */
//        int index = ThreadLocalRandom.current().nextInt(0,Contants.TURNTABLE_SIZE_PER_PROVIDER *3);
//
//        ProviderAgent agent = TurntableUtils.getIndexProviderAgent(index);
//        while(!agent.isValid()){
//            index = ThreadLocalRandom.current().nextInt(0,Contants.TURNTABLE_SIZE_PER_PROVIDER *3);
//            agent = TurntableUtils.getIndexProviderAgent(index);
//        }
//        return agent.getGroup();


        /**
         * 一致性哈希算法
         */
        int index = TurntableUtils.currentIndex.getAndIncrement();
//        System.out.println("0:"+TurntableUtils.getIndexProviderAgent(0)+"1:"+TurntableUtils.getIndexProviderAgent(1)+"2:"+TurntableUtils.getIndexProviderAgent(2));
        ProviderAgent returnValue = TurntableUtils.getNextValidPrivoderAgent(index%3);
        if (returnValue == null){
            throw new IllegalAccessError("there is no volid agent");
        }
        return returnValue.getGroup();

    }
}
