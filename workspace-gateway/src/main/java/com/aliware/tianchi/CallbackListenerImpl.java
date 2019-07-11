package com.aliware.tianchi;

import com.aliware.tianchi.runner.CalRingBufferTableRunner;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.listener.CallbackListener;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 */
public class CallbackListenerImpl implements CallbackListener {

    public CallbackListenerImpl() {
        CalRingBufferTableRunner calRunner = new CalRingBufferTableRunner();
    }

    @Override
    public void receiveServerMsg(String msg) {
        if (StringUtils.isBlank(msg) || msg.indexOf(" ") >= 0) {
            return;
        }
        String[] params = msg.split(":");
        String providerKey = params[0];

        RingBufferTable.getAndSetGroup(providerKey);
        RuntimeMaxThreadContants.Server.setMaxThreadNums(providerKey, Integer.parseInt(params[1]));

    }
}
