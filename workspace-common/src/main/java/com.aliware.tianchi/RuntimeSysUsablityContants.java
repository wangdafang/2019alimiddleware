package com.aliware.tianchi;

import org.apache.dubbo.common.utils.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dafang
 */
public class RuntimeSysUsablityContants {

    private static int systemUsability = 0;


    public static int getSystemUsability() {
        return systemUsability;
    }

    public static void setSystemUsability(int systemUsability) {
        RuntimeSysUsablityContants.systemUsability = systemUsability;
    }
}
