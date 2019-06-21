package com.aliware.tianchi;

/**
 * @author dafang
 */
public class ProviderAgent {
    private int group;
    private int index;
    private String name;
    private boolean isValid;

    public ProviderAgent(int group, int index, String name) {
        this.group = group;
        this.index = index;
        this.name = name;
        this.isValid = true;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
