package com.zhexiao.DAO;

import com.zhexiao.pojo.Area;
import com.zhexiao.utils.MyBatisUtil;

public class areaDAOImpl implements areaDAO{

    areaDAO areaDAO = MyBatisUtil.getSqlSession().getMapper(areaDAO.class);
    @Override
    public String queryNoByAreaName(Area area) {
        return areaDAO.queryNoByAreaName(area);
    }
}
