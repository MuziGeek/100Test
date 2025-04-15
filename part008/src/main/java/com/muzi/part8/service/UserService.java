package com.muzi.part8.service;

import cn.hutool.core.collection.CollectionUtil;

import com.muzi.part8.dto.User;
import com.muzi.part8.dto.UserExportRequest;
import com.muzi.part8.excel.ExcelExportResponse;
import com.muzi.part8.excel.ExcelExportUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {
    public List<User> list = new ArrayList<>();

    public UserService() {
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUserId(i);
            user.setUserName("用户名-" + i);
            user.setAge(20 + i);
            user.setAddress("地址-" + i);
            list.add(user);
        }
    }

    public List<User> getUserList() {
        return list;
    }

    /**
     * 根据用户id列表查找用户列表
     *
     * @param userIdList
     * @return
     */
    public List<User> getUserList(List<Integer> userIdList) {
        return this.getUserList().stream().filter(item -> userIdList.contains(item.getUserId())).collect(Collectors.toList());
    }

    /**
     * 导出用户数据
     *
     * @param request
     * @return
     */
    public ExcelExportResponse userExport(UserExportRequest request) {
        List<Integer> userIdList = request.getUserIdList();
        //根据用户id列表获取用户列表
        List<User> userList;
        if (CollectionUtil.isEmpty(userIdList)) {
            userList = this.getUserList();
        } else {
            userList = this.getUserList(request.getUserIdList());
        }
        return ExcelExportUtils.build(userList, request);
    }

}
