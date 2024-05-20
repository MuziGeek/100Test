package com.muzi.part8.excel;


public class ExcelExportField {
    /**
     * 字段的名称
     */
    private String fieldName;

    /**
     * 字段描述
     */
    private String fieldDesc;

    public ExcelExportField() {
    }

    public ExcelExportField(String fieldName, String fieldDesc) {
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }
}
