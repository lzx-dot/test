package com.zhexiao.DAO;

import com.zhexiao.pojo.Area;

public interface areaDAO {

    /**
     * 根据区域的名字获取区域编号
     */
    public String queryNoByAreaName(Area area);


}
