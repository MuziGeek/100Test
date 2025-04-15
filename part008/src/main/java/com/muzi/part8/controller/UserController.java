package com.muzi.part8.controller;

import cn.hutool.json.JSONUtil;
import com.muzi.part8.dto.UserExportRequest;
import com.muzi.part8.excel.ExcelExportResponse;
import com.muzi.part8.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@Controller
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/userList")
    public String userList(Model model) {
        model.addAttribute("userList", this.userService.getUserList());
        return "userList";
    }

    @PostMapping("/userExport")
    @ResponseBody
    public ExcelExportResponse userExport(@RequestBody UserExportRequest userExportRequest) throws IOException {
        LOGGER.info("userExportRequest:{}", JSONUtil.toJsonPrettyStr(userExportRequest));
        return this.userService.userExport(userExportRequest);
    }

}
