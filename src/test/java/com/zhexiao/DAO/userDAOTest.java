package com.zhexiao.DAO;

import com.zhexiao.pojo.User;
import com.zhexiao.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class userDAOTest {


    @Test
    public void test(){
        /**
         * 1. get the sqlSession
         */

        /**
         * execute the sql in the mapper
         */
        userDAO userDAO = MyBatisUtil.getSqlSession().getMapper(userDAO.class);
        List<User> userList =
                userDAO.getUserList();
        System.out.println(userList);

        System.out.println(userDAO.findUser(new User("1","2")));

        if (userDAO.findUser(new User("1", "1"))==null) {
            System.out.println("不存在该用户");
        }else{
            System.out.println("该用户存在");
        }
    }

    @Test
    public void test2(){
        userDAO mapper = MyBatisUtil.getSqlSession().getMapper(userDAO.class);


    }
}