package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.*;

import java.util.Map;

/**
 * @author daofeng.xjf
 *
 * 服务端过滤器
 * 可选接口
 * 用户可以在服务端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.PROVIDER)
public class TestServerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try{
//            invocation = new RpcInvocation(invocation.getMethodName(),invocation.getParameterTypes(),invocation.getArguments(),invocation.getAttachments(),invocation.getInvoker());
            Map<String, String> attachments = invocation.getAttachments();
            attachments.put("startTime",String.valueOf(System.currentTimeMillis()));
            Result result = invoker.invoke(invocation);
            return result;
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        String startTimeStr = invocation.getAttachment("startTime");
        Long startTime = System.currentTimeMillis();
        if (!StringUtils.isBlank(startTimeStr)){
            startTime = Long.parseLong(startTimeStr);
        }
        long cost = System.currentTimeMillis()-startTime;

        RuntimeAvgContants.Client.setAvgCosts(cost>Integer.MAX_VALUE?0:(int)(cost&Integer.MAX_VALUE));
        return result;
    }

}
