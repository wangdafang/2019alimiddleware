package com.aliware.tianchi;

import com.aliware.tianchi.runner.ThreadCollectRunner;
import org.apache.dubbo.rpc.listener.CallbackListener;
import org.apache.dubbo.rpc.service.CallbackService;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author daofeng.xjf
 * <p>
 * 服务端回调服务
 * 可选接口
 * 用户可以基于此服务，实现服务端向客户端动态推送的功能
 */
public class CallbackServiceImpl implements CallbackService {

    public CallbackServiceImpl() {

        ThreadCollectRunner threadRunner = new ThreadCollectRunner();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!listeners.isEmpty()) {
                    for (Map.Entry<String, CallbackListener> entry : listeners.entrySet()) {
                        try {
                            //数据格式：small:{avgRt}:{cpu}:{thread}
                            StringBuffer sb = new StringBuffer();
                            sb.append(System.getProperty("quota"))
                                    .append(":")
                                    .append(RuntimeMaxThreadContants.Client.getMaxThreadNums());
//                            System.out.println(sb.toString());
                            entry.getValue().receiveServerMsg(sb.toString());
                        } catch (Throwable t1) {
                            t1.printStackTrace();
//                            listeners.remove(entry.getKey());
                        }
                    }
                }
            }
        }, 0, 5);
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
