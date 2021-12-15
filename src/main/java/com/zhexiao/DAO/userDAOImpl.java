package com.zhexiao.DAO;

import com.zhexiao.pojo.User;
import com.zhexiao.utils.MyBatisUtil;

import java.util.List;

public class userDAOImpl implements userDAO{

    userDAO userDAO = MyBatisUtil.getSqlSession().getMapper(userDAO.class);

    @Override
    public User findUser(User user) {
         return userDAO.findUser(user);
    }

    @Override
    public List<User> getUserList() {
        return userDAO.getUserList();
    }

    public boolean userExists(User user){
        return findUser(user)!=null;
    }
}
