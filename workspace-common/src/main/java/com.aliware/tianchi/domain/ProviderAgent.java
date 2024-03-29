package com.aliware.tianchi.domain;

import com.aliware.tianchi.Contants;

/**
 * @author dafang
 */
public class ProviderAgent{
    private int group;
    private boolean isValid;
    private String name;
    private int index;
    private long timestamp;

    public ProviderAgent(int group, boolean isValid, String name, int index) {
        this.group = group;
        this.isValid = isValid;
        this.name = name;
        this.index = index;
        this.timestamp = System.currentTimeMillis();
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void disable(){
        this.isValid = false;
    }

    public void enable(){
        this.isValid = true;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
