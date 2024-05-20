package com.muzi.part8.excel;

import java.util.List;


public class ExcelExportResponse {
    //导出的excel文件名称
    private String excelName;
    // sheet列表数据
    private List<ExcelSheet> sheetList;

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public List<ExcelSheet> getSheetList() {
        return sheetList;
    }

    public void setSheetList(List<ExcelSheet> sheetList) {
        this.sheetList = sheetList;
    }
}
