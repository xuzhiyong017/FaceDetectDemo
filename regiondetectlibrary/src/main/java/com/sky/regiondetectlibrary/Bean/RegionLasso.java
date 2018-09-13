package com.sky.regiondetectlibrary.Bean;

import android.graphics.PointF;

import java.util.List;

/**
 * @author: xuzhiyong
 * @date: 2018/9/13 16:33
 * @Email: 18971269648@163.com
 * @description: 描述
 */
public class RegionLasso {

    private float[] mPolyX, mPolyY;
    private int mPolySize;
    public RegionLasso(float[] px, float[] py, int ps) {
        this.mPolyX = px;
        this.mPolyY = py;
        this.mPolySize = ps;
    }
    public RegionLasso(List<PointF> pointFs) {
        this.mPolySize = pointFs.size();
        this.mPolyX = new float[this.mPolySize];
        this.mPolyY = new float[this.mPolySize];

        for (int i = 0; i < this.mPolySize; i++) {
            this.mPolyX[i] = pointFs.get(i).x;
            this.mPolyY[i] = pointFs.get(i).y;
        }
    }

    //判断是否再区域内
    public boolean contains(float x, float y) {
        boolean result = false;
        for (int i = 0, j = mPolySize - 1; i < mPolySize; j = i++) {
            if ((mPolyY[i] < y && mPolyY[j] >= y)
                    || (mPolyY[j] < y && mPolyY[i] >= y)) {
                if (mPolyX[i] + (y - mPolyY[i]) / (mPolyY[j] - mPolyY[i])
                        * (mPolyX[j] - mPolyX[i]) < x) {
                    result = !result;
                }
            }
        }
        return result;
    }
}
