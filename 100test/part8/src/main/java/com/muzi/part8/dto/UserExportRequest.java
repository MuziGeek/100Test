package com.muzi.part8.dto;


import com.muzi.part8.excel.ExcelExportRequest;

import java.util.List;


public class UserExportRequest extends ExcelExportRequest {
    /**
     * 要导出的用户id列表，不传，则导出所有用户记录
     */
    private List<Integer> userIdList;

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }
}
