package com.muzi.part13.controller;


import com.muzi.part13.common.BusinessExceptionUtils;
import com.muzi.part13.common.Result;
import com.muzi.part13.common.ResultUtils;
import com.muzi.part13.dto.UserRegisterRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return ResultUtils.success("test100");
    }

    /**
     * 登录校验，用户名为路人的时候，登录成功，否则提示用户名错误
     *
     * @param name
     * @return
     */
    @GetMapping("/login")
    public Result<String> login(String name) {
        if (!"木子".equals(name)) {
            throw BusinessExceptionUtils.businessException("1001", "用户名错误");
        } else {
            return ResultUtils.success("登录成功");
        }
    }

    /**
     * 下面是一个注册接口，注册需要用户名和密码，这两个参数不能为空，这里我们使用springboot自带的校验功能
     *
     * @param req
     * @return
     */
    @PostMapping("/userRegister")
    public Result<Void> userRegister(@Validated @RequestBody UserRegisterRequest req) {
        return ResultUtils.success();
    }

}
