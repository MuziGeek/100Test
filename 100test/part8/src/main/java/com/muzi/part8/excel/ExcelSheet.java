package com.muzi.part8.excel;

import java.util.List;
import java.util.Map;


public class ExcelSheet {
    //sheet名称
    private String sheetName;

    //sheet的头
    private List<ExcelHead> headList;

    /**
     * sheet中的数据是一个表格，这里我们使用List<Map<String, String>>类型表示的
     * 每行数据库，我们是放在Map<String,String>中，key就是头的filedName，value就是这个字段的值
     */
    private List<Map<String, String>> dataList;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    public List<ExcelHead> getHeadList() {
        return headList;
    }

    public void setHeadList(List<ExcelHead> headList) {
        this.headList = headList;
    }
}
