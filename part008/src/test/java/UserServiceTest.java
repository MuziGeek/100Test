import cn.hutool.json.JSONUtil;
import com.muzi.part8.dto.UserExportRequest;
import com.muzi.part8.excel.ExcelExportField;
import com.muzi.part8.excel.ExcelExportResponse;
import com.muzi.part8.excel.ExcelExportUtils;
import com.muzi.part8.service.UserService;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class UserServiceTest {
    public static void main(String[] args) throws Exception {
        UserService userService = new UserService();
        UserExportRequest request = new UserExportRequest();
        request.setExcelName("用户列表");
        request.setSheetName("用户列表");
        List<ExcelExportField> fieldList = new ArrayList<>();
        fieldList.add(new ExcelExportField("userName", "用户名"));
        fieldList.add(new ExcelExportField("age", "年龄"));
        fieldList.add(new ExcelExportField("address", "地址"));
        request.setFieldList(fieldList);
        ExcelExportResponse excelExportResponse = userService.userExport(request);
        System.out.println(JSONUtil.toJsonPrettyStr(excelExportResponse));
        writeExcelToFile(excelExportResponse);
    }

    public static void writeExcelToFile(ExcelExportResponse result) throws Exception {
        String fileName = "E:\\tmp\\" + System.currentTimeMillis() + ".xlsx";
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
            ExcelExportUtils.write(result, fileOutputStream);
        }
    }

}
