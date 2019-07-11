package com.aliware.tianchi;

import com.aliware.tianchi.domain.ProviderAgent;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.Map;
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
        int group = 0;
        try {
            ProviderAgent agent = getProviderAgent();
            group = agent.getGroup();
            int index = agent.getIndex();
            Map<String, String> attachments = invocation.getAttachments();
            attachments.put("ringbuffer_index",index+"");
        } catch (Exception e) {
            return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
        }
        return invokers.get(group);
    }

    public ProviderAgent getProviderAgent(){
        ProviderAgent agent = RingBufferTable.getNextValidProvider();
        if (agent == null){
            throw new IllegalArgumentException("no valid provide thread exist!!!!");
        }
        return agent;
    }
}


