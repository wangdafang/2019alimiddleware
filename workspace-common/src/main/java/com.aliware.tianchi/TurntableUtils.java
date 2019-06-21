package com.aliware.tianchi;

import sun.management.resources.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dafang
 */
public class TurntableUtils {

    public static AtomicInteger currentIndex=new AtomicInteger(0);

    private static final String[] turntableNames = new String[]{"small","medium","large","blank"};

    private static final int turntableSize = 30;

    private static final int smallGroup = 0;
    private static final int mediumGroup = 1;
    private static final int largeGroup = 2;
    private static final int blankGroup = 3;

    private volatile static ProviderAgent[] turntable = new ProviderAgent[turntableSize];


    private static List<Integer> smallIndexes = new ArrayList<>();
    private static List<Integer> mediumIndexes = new ArrayList<>();
    private static List<Integer> largeIndexes = new ArrayList<>();
    private static List<Integer> blankIndexes = new ArrayList<>();

    static{
        for (int i=0;i<turntableSize;i++){
            switch(i%3){
                case smallGroup:
                    turntable[i] = new ProviderAgent(smallGroup,i,turntableNames[smallGroup]);
                    smallIndexes.add(i);
                    break;
                case mediumGroup:
                    turntable[i] = new ProviderAgent(mediumGroup,i,turntableNames[mediumGroup]);
                    mediumIndexes.add(i);
                    break;
                case largeGroup:
                    turntable[i] = new ProviderAgent(largeGroup,i,turntableNames[largeGroup]);
                    largeIndexes.add(i);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 通过key获取provider的名字
     * @param key
     * @return
     */
    public static String getProviderName(int key){
        return turntableNames[key];
    }

    /**
     * 删除某一组的n个元素
     * @param nums 删除几个
     * @param group 删除哪个组的元素
     */
    public static void batchDeleteProviderAgent(int nums,int group){
        if (group>2){
            return ;
        }
        switch(group%3){
            case smallGroup:
                removeFromTruntable(nums,smallIndexes);
                break;
            case mediumGroup:
                removeFromTruntable(nums,mediumIndexes);
                break;
            case largeGroup:
                removeFromTruntable(nums,largeIndexes);
                break;
            default:
                break;
        }
    }

    /**
     * 从指定组中移除指定个元素
     * @param nums
     * @param indexes
     */
    private static void removeFromTruntable(int nums,List<Integer> indexes) {
        if (indexes.size()-nums<0){
            nums = indexes.size();
        }
        for (int i=0;(i<indexes.size() && i<nums);i++){
            int index = indexes.size()-(i+1);
            ProviderAgent agent = turntable[indexes.get(index)];
            blankIndexes.add(agent.getIndex());
            agent.setName(turntableNames[blankGroup]);
            agent.setValid(false);
            agent.setGroup(blankGroup);
        }
    }


    /**
     * 添加某一组的n个元素
     * @param nums 添加几个
     * @param group 添加哪个组的元素
     */
    public static void batchAddProviderAgent(int nums,int group){
        if (group>2){
            return ;
        }

        switch(group%3){
            case smallGroup:
                addToTurntable(nums,smallIndexes,smallGroup);
                break;
            case mediumGroup:
                addToTurntable(nums,mediumIndexes,mediumGroup);
                break;
            case largeGroup:
                addToTurntable(nums,largeIndexes,largeGroup);
                break;
            default:
                break;
        }

    }

    /**
     * 向指定组添加元素
     * @param nums 添加个数
     * @param indexes 待添加的组
     * @param group 组
     */
    private static void addToTurntable(int nums, List<Integer> indexes,int group) {
        if (nums>blankIndexes.size()){
            nums = blankIndexes.size();
        }
        for (int i=nums;i<0;i--){
            ProviderAgent agent = turntable[blankIndexes.get(i-1)];
            indexes.add(agent.getIndex());
            agent.setName(turntableNames[group]);
            agent.setValid(true);
            agent.setGroup(group);
        }
    }


    public static int getTurntableSize() {
        return turntableSize;
    }

    /**
     * 根据传入的位置返回turntable对应的provider组
     * @param index
     * @return
     */
    public static int getIndexValue(int index) {
        if (index>turntableSize) {
            throw new IllegalArgumentException("a value that out of bounds is not allow,value:"+index);
        }

        if (turntable[index].isValid()){
            return turntable[index].getGroup();
        }
        throw new IllegalArgumentException("provider agent is not valid");
    }

    public static int getTurntableNameIndex(String name){
        for (int i=0;i<turntableNames.length;i++){
            if (turntableNames[i]==name){
                return i;
            }
        }
        throw new IllegalArgumentException("the name:"+name+" is not support");
    }

    public static int getIndexesSize(int key) {
        switch(key%4){
            case smallGroup:
                return smallIndexes.size();
            case mediumGroup:
                return mediumIndexes.size();
            case largeGroup:
                return largeIndexes.size();
            case blankGroup:
                return blankIndexes.size();
            default:
                throw new IllegalArgumentException("can't find key:"+key+" in turntable indexes");
        }
    }
}
