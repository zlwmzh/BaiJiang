package com.baijiang.www.recyclerviewpager;

/**
 * Created by Micky on 2018/12/3.
 */

public class ItemBean {
    private String cover;
    private String name;

    public ItemBean(String cover, String name) {
        this.cover = cover;
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
