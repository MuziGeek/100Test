package com.muzi.part8.excel;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import com.muzi.part8.utils.CollUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ExcelExportUtils {
    public static ExcelExportResponse build(List<?> dataList, ExcelExportRequest request) {
        //1、组装excel导出的结果
        ExcelExportResponse result = new ExcelExportResponse();
        result.setExcelName(request.getExcelName());

        //2、组装sheet
        List<ExcelSheet> sheetList = new ArrayList<>();
        result.setSheetList(sheetList);

        //第1个sheet
        ExcelSheet excelSheet = new ExcelSheet();
        //设置sheet的名称
        excelSheet.setSheetName(request.getSheetName());
        //设置sheet的头
        excelSheet.setHeadList(buildSheetHeadList(request));
        //设置sheet中表格的数据，是个二位数组，类型 List<Map<String, Object>>
        excelSheet.setDataList(buildSheetDataList(dataList, request));
        //将第1个sheet放入sheet列表
        sheetList.add(excelSheet);

        return result;
    }

    public static List<ExcelHead> buildSheetHeadList(ExcelExportRequest request) {
        //排序
        List<ExcelExportField> fieldList = request.getFieldList();
        List<ExcelHead> excelHeadList = new ArrayList<>(fieldList.size());
        for (ExcelExportField excelExportField : fieldList) {
            ExcelHead excelHead = new ExcelHead();
            excelHead.setFieldName(excelExportField.getFieldName());
            excelHead.setFieldDesc(excelExportField.getFieldDesc());
            excelHeadList.add(excelHead);
        }
        return excelHeadList;
    }

    public static List<Map<String, String>> buildSheetDataList(List<?> dataList, ExcelExportRequest request) {
        if (CollUtils.isEmpty(dataList)) {
            return CollUtils.emptyArrayList();
        }
        List<Map<String, String>> sheetDataList = new ArrayList<>(dataList.size());
        List<ExcelExportField> fieldList = request.getFieldList();
        List<String> exportFieldNameList = CollUtils.convertList(fieldList, ExcelExportField::getFieldName);

        for (Object data : dataList) {
            Map<String, String> dataMap = new HashMap<>();
            for (String fileName : exportFieldNameList) {
                Object filedValue = ReflectUtil.getFieldValue(data, fileName);
                dataMap.put(fileName, convertToString(filedValue));
            }
            sheetDataList.add(dataMap);
        }
        return sheetDataList;
    }

    private static String convertToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }


    public static void writeExcelToResponse(ExcelExportResponse excelExportResult) throws IOException {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        OutputStream outputStream = response.getOutputStream();

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncodeUtil.encode(excelExportResult.getExcelName() + ".xlsx"));
        write(excelExportResult, outputStream);
    }

    /**
     * 将excel写入到指定的流中
     *
     * @param result
     * @param outputStream
     */
    public static void write(ExcelExportResponse result, OutputStream outputStream) {
        List<ExcelSheet> sheetList = result.getSheetList();
        try (ExcelWriter writer = EasyExcel.write(outputStream).build();) {
            for (int sheetNo = 0; sheetNo < sheetList.size(); sheetNo++) {
                ExcelSheet excelSheet = sheetList.get(sheetNo);
                List<List<String>> head = ExcelExportUtils.buildEasyExcelHead(excelSheet);
                List<List<String>> dataList = ExcelExportUtils.buildEasyExcelDataList(excelSheet);

                WriteSheet writeSheet = EasyExcel
                        .writerSheet(sheetNo, excelSheet.getSheetName())
                        .head(head).build();
                writer.write(dataList, writeSheet);
            }
        }
    }

    /**
     * 通过 ExcelSheet 得到easyExcel中当前sheet需要的头
     *
     * @param excelSheet
     * @return
     */

    public static List<List<String>> buildEasyExcelHead(ExcelSheet excelSheet) {
        if (excelSheet == null || excelSheet.getHeadList() == null) {
            return CollUtils.newArrayList();
        }
        return excelSheet.getHeadList().stream().map(item -> CollUtils.newArrayList(item.getFieldDesc())).collect(Collectors.toList());
    }

    /**
     * 通过 ExcelSheet 得到easyExcel中当前sheet需要的数据
     *
     * @param excelSheet
     * @return
     */
    public static List<List<String>> buildEasyExcelDataList(ExcelSheet excelSheet) {
        if (excelSheet == null || excelSheet.getHeadList() == null || excelSheet.getDataList() == null) {
            return CollUtils.newArrayList();
        }
        List<String> filedNameList = CollUtils.convertList(excelSheet.getHeadList(), ExcelHead::getFieldName);
        List<List<String>> dataList = new ArrayList<>(excelSheet.getDataList().size());

        for (Map<String, String> row : excelSheet.getDataList()) {
            List<String> list = new ArrayList<>();
            for (String filedName : filedNameList) {
                list.add(row.get(filedName));
            }
            dataList.add(list);
        }
        return dataList;
    }

}
