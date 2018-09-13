package com.sky.regiondetectlibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sky.regiondetectlibrary.Bean.IrregularArea;
import com.sky.regiondetectlibrary.Bean.MapArea;
import com.sky.regiondetectlibrary.Bean.RegionLasso;
import com.sky.regiondetectlibrary.R;
import com.sky.regiondetectlibrary.svgparse.SVGXmlParserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xuzhiyong
 * @date: 2018/9/13 16:39
 * @Email: 18971269648@163.com
 * @description: 描述
 */
public class CustomDetectView extends View{


    //模式 NONE：无 MOVE：移动 ZOOM:缩放
    private static final int NONE = 0;
    private static final int MOVE = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;// 默认模式
    private float beforeLength = 0,afterLength = 0;	// 两触点距离
    private float downX = 0;	//单触点x坐标
    private float downY = 0;	//单触点y坐标
    private float scale_temp=1; //缩放比例
    private float downMidX=0,downMidY=0;  //缩放的中心位置坐标
    private static float scale_max = 3;	//scale的最大值
    private  static float scale_min = 1;	//scale的最小值
    private float OnMoveX=0,OnMoveY=0;          //正在单指滑动的总XY距离
    private float UpMoveX=0,UpMoveY=0;          //完成单指滑动的总XY距离
    private float offX=0,offY=0;                //单指滑动的XY距离
    private float Width=0,Height=0;             //View的宽度和高度
    private Paint paint,linepaint;
    private Matrix myMatrix;                    //用来完成缩放
    private boolean isFirst;               //只在第一次传数据绘图时加载
    private String IrregularAreaname="";
    private boolean criticalflag;          //拖拽的临界值标志位
    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private MapArea map;
    private float map_scale=0;
    private onIrregularAreaClickLisener onIrregularAreaClickLisener;
    public void setOnChoseIrregularArea(onIrregularAreaClickLisener lisener){
        this.onIrregularAreaClickLisener=lisener;
    }


    public CustomDetectView(Context context) {
        this(context,null);
    }

    public CustomDetectView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomDetectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint=new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        linepaint=new Paint();
        linepaint.setColor(Color.GRAY);
        linepaint.setAntiAlias(true);
        linepaint.setStrokeWidth(1);
        linepaint.setStyle(Paint.Style.STROKE);
        myMatrix=new Matrix();

        map = SVGXmlParserUtils.parserXml(getResources().openRawResource(R.raw.taiwan));
        isFirst=true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        if (map!=null){
            map_scale=width/map.getMax_x();
            height=(int) (map.getMax_y()*map_scale);
        }
        setMeasuredDimension(width, height);
    }


    //设置最大缩放倍数
    public void setMaxScale(float a){
        if (a<=1){
            scale_max=1;
        }else{
            scale_max=a;
        }
    }
    //设置最小缩放倍数
    public void setMinScale(float a){
        if (a<=0){
            scale_min=0;
        }else{
            scale_min=a;
        }
    }
    public void setMap(MapArea map){
        this.map=map;
        isFirst=true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isFirst){
            Width=getWidth();
            Height=getHeight();
            //首先重置所有点的坐标，使得map适应屏幕大小
            if (map!=null){
                map_scale=Width/map.getMax_x();
            }
            scalePoints(canvas,map_scale);
            isFirst=false;
        }else{
            canvas.concat(myMatrix);
            canvas.translate(OnMoveX, OnMoveY);
            drawMap(canvas);
        }
        super.onDraw(canvas);
    }
    private void drawMap(Canvas canvas) {
        //linepaint.setStrokeWidth(1/getScale());
        if (map.getIrregularAreaslist().size()>0){
            int b=0;
            for (int i=0;i<map.getIrregularAreaslist().size();i++){
                if (map.getIrregularAreaslist().get(i).getLinecolor()==Color.BLACK){
                    b=i;
                }else{
                    paint.setColor(map.getIrregularAreaslist().get(i).getColor());
                    linepaint.setColor(map.getIrregularAreaslist().get(i).getLinecolor());
                    for (Path p:map.getIrregularAreaslist().get(i).getListpath()){
                        canvas.drawPath(p, paint);
                        canvas.drawPath(p, linepaint);
                    }
                }
            }
            paint.setColor(map.getIrregularAreaslist().get(b).getColor());
            linepaint.setColor(map.getIrregularAreaslist().get(b).getLinecolor());
            for (Path p:map.getIrregularAreaslist().get(b).getListpath()){
                canvas.drawPath(p, paint);
                canvas.drawPath(p, linepaint);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // 多点触摸
                onPointerDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                // 必须是单指移动
                if (mode==MOVE){
                    offX = (event.getX() - downX)/getScale();//X轴移动距离
                    offY = (event.getY() - downY)/getScale();//y轴移动距离
                    if (!criticalflag)  {
                        UpMoveX=UpMoveX+offX;
                        UpMoveY=UpMoveY+offY;}
                }
                mode = NONE;
                if (Math.abs(event.getX()-downX)<10&&Math.abs(event.getY()-downY)<10){
                    RectF rectF=getMatrixRectF();
                    //判断点的是哪个省，然后相应的省变颜色
                    changeIrregularAreaColor(event,rectF);
                }
                break;
            // 多点松开
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }
    //滑动
    private void onTouchMove(MotionEvent event) {
        //双指缩放操作
        if (mode == ZOOM) {
            afterLength = getDistance(event);// 获取两点的距离
            float gapLength = afterLength - beforeLength;// 变化的长度
            if (Math.abs(gapLength)>10&&beforeLength!=0){
                if (gapLength>0){
                    if (getScale()<scale_max){
                        scale_temp=afterLength/beforeLength;
                    }else{
                        scale_temp = scale_max / getScale();
                    }}else{
                    if (getScale()>scale_min){
                        scale_temp=afterLength/beforeLength;
                    }else{
                        scale_temp = scale_min / getScale();
                    }
                }
                //设置缩放比例和缩放中心
                myMatrix.postScale(scale_temp, scale_temp, downMidX, downMidY);
                invalidate();
                beforeLength = afterLength;
            }
        }
        //单指拖动操作
        else if(mode == MOVE){
            // 计算实际距离
            offX = (event.getX() - downX)/getScale();//X轴移动距离
            offY = (event.getY() - downY)/getScale();//y轴移动距离
            OnMoveX=UpMoveX+offX;
            OnMoveY=UpMoveY+offY;
            RectF rectF=getMatrixRectF();
            if (rectF.left+OnMoveX*getScale()>=Width/2){
                UpMoveX=(Width/2-rectF.left)/getScale();
                criticalflag=true;
            }else if (rectF.right+OnMoveX*getScale()<=Width/2){
                UpMoveX=(Width/2-rectF.right)/getScale();
                criticalflag=true;
            }
            else if (rectF.top+OnMoveY*getScale()>=Height/2){
                UpMoveY=(Height/2-rectF.top)/getScale();
                criticalflag=true;
            }
            else if (rectF.bottom+OnMoveY*getScale()<=Height/2){
                UpMoveY=(Height/2-rectF.bottom)/getScale();
                criticalflag=true;
            }else{
                criticalflag=false;
                invalidate();
            }
        }
    }
    //单触点操作
    private void onTouchDown(MotionEvent event) {
        //触电数为1，即单点操作
        if(event.getPointerCount()==1){
            mode = MOVE;
            downX = event.getX();
            downY = event.getY();
        }
    }
    //多触点操作
    private void onPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mode = ZOOM;
            beforeLength = getDistance(event);
            downMidX = getMiddleX(event);
            downMidY=getMiddleY(event);
        }
    }
    // 获取两点的距离
    private float getDistance(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
    private float getMiddleX(MotionEvent event){
        return (event.getX(1)+event.getX(0))/2;
    }
    private float getMiddleY(MotionEvent event){
        return (event.getY(1)+event.getY(0))/2;
    }
    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale() {
        myMatrix.getValues(matrixValues);
        if (matrixValues[Matrix.MSCALE_X]==0){
            return 1;
        }else{
            return matrixValues[Matrix.MSCALE_X];
        }
    }

    //第一次绘制，缩小map到View指定大小
    private void scalePoints(Canvas canvas,float scale) {
        if (map.getIrregularAreaslist().size()>0)
            //map的左右上下4个临界点
            map.setMax_x(map.getMax_x()*scale);
        map.setMin_x(map.getMin_x()*scale);
        map.setMax_y(map.getMax_y()*scale);
        map.setMin_y(map.getMin_y()*scale);
        for (IrregularArea area:map.getIrregularAreaslist()){
            paint.setColor(area.getColor());
            List<RegionLasso> listLasso=new ArrayList<>();
            List<Path> pathList=new ArrayList<>();
            for (Path p:area.getListpath()){
                //遍历Path中的所有点，重置点的坐标
                Path newpath=resetPath(p, scale, listLasso);
                pathList.add(newpath);
                canvas.drawPath(newpath,paint);
                canvas.drawPath(newpath,linepaint);
            }
            area.setListpath(pathList);
            //拿到path转换之后的Lasso对象，用来点击的是哪个省份,即判断点是否在path画出的区域内
            area.setPathLasso(listLasso);
        }
    }
    private Path resetPath(Path path,float scale,List<RegionLasso> listLasso) {
        List<PointF> list=new ArrayList<>();
        PathMeasure pathmesure=new PathMeasure(path,true);
        float[] s=new float[2];
        for (int i=0;i<pathmesure.getLength();i=i+2) {
            pathmesure.getPosTan(i, s, null);
            PointF p=new PointF(s[0]*scale,s[1]*scale);
            list.add(p);
        }
        RegionLasso lasso=new RegionLasso(list);
        listLasso.add(lasso);
        Path path1=new Path();
        for (int i=0;i<list.size();i++){
            if (i==0){
                path1.moveTo(list.get(i).x,list.get(i).y);
            }else{
                path1.lineTo(list.get(i).x, list.get(i).y);
            }
        }
        path1.close();
        return path1;
    }
    //判断点的是哪个省，然后相应的省变颜色
    private void changeIrregularAreaColor(MotionEvent event,RectF rectF) {
        for (IrregularArea IrregularArea:map.getIrregularAreaslist()){
            IrregularArea.setColor(Color.YELLOW);
            IrregularArea.setLinecolor(Color.GRAY);
        }
        for (IrregularArea p:map.getIrregularAreaslist()){
            for (RegionLasso lasso:p.getPathLasso()){
                PointF pf=new PointF(event.getX() / getScale() - rectF.left / getScale()-OnMoveX
                        ,event.getY() / getScale() - rectF.top/getScale()-OnMoveY);
                if (lasso.contains(pf.x,pf.y)){
                    IrregularAreaname=p.getName();
                    //p.setColor(Color.RED);
                    p.setLinecolor(Color.BLACK);
                    p.setColor(Color.RED);
                    invalidate();
                    //暴露到Activity中的接口，把省的名字传过去
                    if(onIrregularAreaClickLisener != null){
                        onIrregularAreaClickLisener.onChose(IrregularAreaname);
                    }
                    break;
                }
            }
        }
    }
    /**
     * 根据当前图片的Matrix获得图片的范围
     */
    private RectF getMatrixRectF() {
        Matrix matrix = myMatrix;
        RectF rect = new RectF();
        rect.set(0, 0, Width, Height);
        matrix.mapRect(rect);
        return rect;
    }
    public interface onIrregularAreaClickLisener{
        public void onChose(String IrregularAreaname);
    }
}
