package com.zhexiao;

import com.zhexiao.DAO.userDAO;
import com.zhexiao.pojo.User;
import com.zhexiao.utils.MyBatisUtil;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        userDAO userDAO = MyBatisUtil.getSqlSession().getMapper(userDAO.class);
        List<User> userList = userDAO.getUserList();
        System.out.println(userList);
    }
}
