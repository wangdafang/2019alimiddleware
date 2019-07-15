package com.aliware.tianchi;

import com.aliware.tianchi.domain.ProviderAgent;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        int index = 0;
        try {
            String providerIndex = invocation.getAttachment("ringbuffer_index");
            if (!StringUtils.isBlank(providerIndex)) {
                index = Integer.parseInt(providerIndex);
                RingBufferTable.disableOne(index);
            }
            Result result = invoker.invoke(invocation);
//            if (!StringUtils.isBlank(providerIndex)){
////                Counter.enableCount.getAndIncrement();
//                RingBufferTable.enableOne(index);
//            }
            return result;
        } catch (Exception e) {
//            ProviderAgent agent = RingBufferTable.getProviderByIndex(index);
//            if (agent != null) {
//                if (Counter.exceptionList.get(agent.getGroup())==null){
//                    Counter.exceptionList.put(agent.getGroup(),new AtomicInteger(1));
//                } else{
//                    Counter.exceptionList.get(agent.getGroup()).getAndIncrement();
//                }
//            }
            RingBufferTable.enableOne(index);
            throw e;
//            return null;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        String providerIndex = invocation.getAttachment("ringbuffer_index");
        if (!StringUtils.isBlank(providerIndex)) {
            int index = Integer.parseInt(providerIndex);
//            Counter.enableCount.getAndIncrement();

            RingBufferTable.enableOne(index);
        }

        return result;
    }
}
