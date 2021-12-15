package com.zhexiao.DAO;

import com.zhexiao.pojo.User;

import java.util.List;

public interface userDAO {

    public User findUser(User user);

    public List<User> getUserList();
}
