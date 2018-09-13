package com.sky.regiondetectlibrary.Bean;

import android.graphics.Path;

import java.util.List;

/**
 * @author: xuzhiyong
 * @date: 2018/9/13 16:36
 * @Email: 18971269648@163.com
 * @description: 描述
 */
public class IrregularArea {
    private String name;
    private List<Path> listpath;
    private int linecolor;

    public int getLinecolor() {
        return linecolor;
    }

    public void setLinecolor(int linecolor) {
        this.linecolor = linecolor;
    }

    public List<Path> getListpath() {
        return listpath;
    }
    public void setListpath(List<Path> listpath) {
        this.listpath = listpath;
    }
    private List<RegionLasso> pathLasso;
    public List<RegionLasso> getPathLasso() {
        return pathLasso;
    }
    public void setPathLasso(List<RegionLasso> pathLasso) {
        this.pathLasso = pathLasso;
    }
    private int color;
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
