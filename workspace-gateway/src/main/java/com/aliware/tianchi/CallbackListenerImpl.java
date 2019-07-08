package com.aliware.tianchi;

import com.aliware.tianchi.runner.CalFactorTimeRunner;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.listener.CallbackListener;

/**
 * @author daofeng.xjf
 *
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 *
 */
public class CallbackListenerImpl implements CallbackListener {

    public CallbackListenerImpl() {
        CalFactorTimeRunner calFactorRunner = new CalFactorTimeRunner();
    }

    @Override
    public void receiveServerMsg(String msg) {
        if (StringUtils.isBlank(msg)){
            return;
        }
        String[] params = msg.split(":");
        String providerKey = params[0];

        RuntimeAvgContants.Server.setAvgCosts(providerKey,Integer.parseInt(params[1]));
        RuntimeCpuContants.Server.setCpuUsage(providerKey,Integer.parseInt(params[2]));
        RuntimeThreadContants.Server.setThreadRatio(providerKey,Integer.parseInt(params[3]));
//        StringBuffer sb = new StringBuffer();
//        sb.append(providerKey)
//        .append("-avgRt:")
//        .append(RuntimeAvgContants.Server.getCurrAvgCosts(providerKey))
//        .append(",cpu:")
//        .append(RuntimeCpuContants.Server.getCurrCpuUsage(providerKey))
//        .append(",thread:")
//        .append(RuntimeThreadContants.Server.getCurrThreadRatio(providerKey));
//        System.out.println(sb.toString());
    }

}
