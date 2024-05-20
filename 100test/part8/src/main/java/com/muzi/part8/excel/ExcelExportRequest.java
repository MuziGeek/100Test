package com.muzi.part8.excel;

import java.util.List;


public class ExcelExportRequest {
    /**
     * excel名称
     */
    private String excelName;

    /**
     * sheet的名称
     */
    private String sheetName;

    /**
     * 导出字段有序列表
     */
    private List<ExcelExportField> fieldList;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public List<ExcelExportField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<ExcelExportField> fieldList) {
        this.fieldList = fieldList;
    }
}
