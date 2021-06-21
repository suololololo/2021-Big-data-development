package com.jiajun;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 16:53
 */
public class Area {
    private int areaId;
    private String areaName;
    private int priority;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Area(int areaId, String areaName, int priority) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.priority = priority;
    }
}
