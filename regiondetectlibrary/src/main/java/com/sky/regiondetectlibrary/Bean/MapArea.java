package com.sky.regiondetectlibrary.Bean;

import java.util.List;

/**
 * @author: xuzhiyong
 * @date: 2018/9/13 16:34
 * @Email: 18971269648@163.com
 * @description: 描述
 */
public class MapArea {

    private float Max_x;
    private float Min_x;
    private float Max_y;
    private float Min_y;

    public float getMin_x() {
        return Min_x;
    }

    public void setMin_x(float min_x) {
        Min_x = min_x;
    }

    public float getMax_y() {
        return Max_y;
    }

    public void setMax_y(float max_y) {
        Max_y = max_y;
    }

    public float getMin_y() {
        return Min_y;
    }

    public void setMin_y(float min_y) {
        Min_y = min_y;
    }

    private List<IrregularArea> IrregularAreaslist;

    public float getMax_x() {
        return Max_x;
    }

    public List<IrregularArea> getIrregularAreaslist() {
        return IrregularAreaslist;
    }

    public void setIrregularAreaslist(List<IrregularArea> IrregularAreaslist) {
        this.IrregularAreaslist = IrregularAreaslist;
    }

    public void setMax_x(float max_x) {
        Max_x = max_x;
    }
}
