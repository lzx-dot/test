package com.zhexiao.pojo;

public class Area {
    String name;
    String no;  //区域的编号

    @Override
    public String toString() {
        return "Area{" +
                "name='" + name + '\'' +
                ", no='" + no + '\'' +
                '}';
    }

    public Area(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Area(String name, String no) {
        this.name = name;
        this.no = no;
    }
}
