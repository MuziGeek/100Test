package com.muzi.part11.enums;


public enum JobStatusEnums {
    START(1, "启动"),
    STOP(0, "停止");
    private Integer status;
    private String description;

    JobStatusEnums(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 状态是否有效
     *
     * @param status
     * @return
     */
    public static boolean isValid(Integer status) {
        for (JobStatusEnums item : values()) {
            if (item.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
