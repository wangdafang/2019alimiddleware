package com.aliware.tianchi.runner;


import com.aliware.tianchi.Counter;
import com.aliware.tianchi.RingBufferTable;
import com.aliware.tianchi.domain.ProviderAgent;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dafang
 */
public class ShowRingBufferStatusRunner {

    private static final Logger logger = LoggerFactory.getLogger(ShowRingBufferStatusRunner.class);

    private Timer timer = new Timer();

    public ShowRingBufferStatusRunner() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showRingbufferTable();
            }
        }, 5000, 10);
    }

    public void showRingbufferTable() {
        int count = 0;
        for (ProviderAgent agent : RingBufferTable.forTestGetRingTable()) {
            if (agent.isValid()) {
                count++;
            }
        }
        logger.info("current ringbuffer valid size : " + count + ",total run in ringbuffer size :" + Counter.runInRingBuffer.get() + ",random run size : " + Counter.randomCount.get());


    }

}
