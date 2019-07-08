package com.aliware.tianchi;

import com.aliware.tianchi.runner.CalFactorTimeRunner;
import com.aliware.tianchi.runner.CpuUsageCollectTimeRunner;
import com.aliware.tianchi.runner.ThreadNumsCollectTimeRunner;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.threadpool.ThreadPool;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Dispatcher;
import org.apache.dubbo.remoting.transport.dispatcher.WrappedChannelHandler;
import org.apache.dubbo.remoting.transport.dispatcher.all.AllChannelHandler;
import org.apache.dubbo.rpc.listener.CallbackListener;
import org.apache.dubbo.rpc.service.CallbackService;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author daofeng.xjf
 * @update dafang
 * <p>
 * 服务端回调服务
 * 可选接口
 * 用户可以基于此服务，实现服务端向客户端动态推送的功能
 *
 *
 */
public class CallbackServiceImpl implements CallbackService {

    public CallbackServiceImpl() {

        CpuUsageCollectTimeRunner cpuCollectRunner = new CpuUsageCollectTimeRunner();
        ThreadNumsCollectTimeRunner threadCollectRunner = new ThreadNumsCollectTimeRunner();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!listeners.isEmpty()) {
                    for (Map.Entry<String, CallbackListener> entry : listeners.entrySet()) {
                        try {
                            //数据格式：small:{avgRt}:{cpu}:{thread}
                            StringBuffer sb = new StringBuffer();
//                            sb.append(System.getProperty("quota"))
//                                    .append(":")
//                                    .append(RuntimeAvgContants.Client.getAvgCosts())
//                                    .append(":")
//                                    .append(RuntimeCpuContants.Client.getCpuUsage())
//                                    .append(":")
//                                    .append(RuntimeThreadContants.Client.getThreadRatio());
//                            System.out.println("send to client:"+sb.toString());
                            entry.getValue().receiveServerMsg(sb.toString());
                        } catch (Throwable t1) {
                            //TODO 此处会导致当线程池满之后，不再向服务端推送信息
//                            listeners.remove(entry.getKey());
                            t1.printStackTrace();
                        }
                    }
                }
            }
        }, 0, 1);

    }

    private Timer timer = new Timer();

    /**
     * key: listener type
     * value: callback listener
     */
    private final Map<String, CallbackListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void addListener(String key, CallbackListener listener) {
        listeners.put(key, listener);
        listener.receiveServerMsg(new Date().toString()); // send notification for change
    }
}
