package com.zhexiao.DAO;

import com.zhexiao.pojo.Area;
import com.zhexiao.utils.MyBatisUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class areaDAOTest {

    areaDAO areaDAO = MyBatisUtil.getSqlSession().getMapper(areaDAO.class);

    @Test
    public void test(){
        System.out.println(areaDAO.queryNoByAreaName(new Area("6D")));
    }

}