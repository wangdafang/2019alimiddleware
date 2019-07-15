package com.aliware.tianchi;

import com.aliware.tianchi.runner.CalRingBufferTableRunner;
import com.aliware.tianchi.runner.DocterCheckerRunner;
import com.aliware.tianchi.runner.ShowRingBufferStatusRunner;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CallbackListenerImpl.class);


    public CallbackListenerImpl() {
        CalRingBufferTableRunner calRunner = new CalRingBufferTableRunner();
//        ShowRingBufferStatusRunner showRunner = new ShowRingBufferStatusRunner();
//        DocterCheckerRunner docterRunner = new DocterCheckerRunner();
    }

    @Override
    public void receiveServerMsg(String msg) {
        if (StringUtils.isBlank(msg) || msg.indexOf(" ") >= 0) {
            return;
        }
        if (msg.startsWith("stop")){
//            logger.info("stop get provider message,reset ring table");
            RingBufferTable.resetRingTable();
            return;
        }
        String[] params = msg.split(":");
        String providerKey = params[0];

//        logger.info("got message from server : " + msg);

        RingBufferTable.getAndSetGroup(providerKey);
        RuntimeMaxThreadContants.Server.setMaxThreadNums(providerKey, Integer.parseInt(params[1]));

    }
}
