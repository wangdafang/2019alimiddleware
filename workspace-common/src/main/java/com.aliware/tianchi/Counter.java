

package com.aliware.tianchi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dafang
 */
public class Counter {

    public static AtomicInteger currentIndex = new AtomicInteger(0);

    public static AtomicInteger randomCount = new AtomicInteger(0);

    public static AtomicInteger blockSize = new AtomicInteger(0);

    public static AtomicInteger runInRingBuffer = new AtomicInteger(0);

    public static ConcurrentMap<Integer,AtomicInteger> exceptionList = new ConcurrentHashMap<>();

}
