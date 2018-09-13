package com.sky.regiondetectlibrary.svgparse;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Xml;

import com.sky.regiondetectlibrary.Bean.IrregularArea;
import com.sky.regiondetectlibrary.Bean.MapArea;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjd on 2017/6/1.
 * 解析svg xml
 */

public class SVGXmlParserUtils {



    public static MapArea parserXml(final InputStream in){
        MapArea mapArea = new MapArea();
        parserXml(in,mapArea);
        return mapArea;
    }

    private static float Max_X,Min_x,Max_y,Min_y;

    private static void parserXml(InputStream in, MapArea mapArea){
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(in, "UTF-8");
            int eventType = parser.getEventType();
            String name = null;
            List<IrregularArea> list = new ArrayList<>();
            IrregularArea  irregularArea = null;
            SvgPathParser svg=new SvgPathParser();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                        break;
                    case XmlPullParser.START_TAG:// 开始元素事件
                        name = parser.getName();
                        if ("path".equals(name)) {
                            irregularArea = new IrregularArea();
                            irregularArea.setName(parser.getAttributeValue(null, "title"));
                            String PathPoints = parser.getAttributeValue(null, "d");
                            List<Path> listpath=new ArrayList<>();
                            //拿到每个省的path集合
                            String s[]=PathPoints.split("z");
                            for(String ss:s){
                                ss+="z";
                                listpath.add(svg.parsePath(ss));
                            }
                            //拿到name和path
                            irregularArea.setName(name);
                            irregularArea.setListpath(listpath);
                            irregularArea.setColor(Color.YELLOW);
                            irregularArea.setLinecolor(Color.GRAY);

                            if (svg.getMax_X()>=Max_X){
                                Max_X=svg.getMax_X();
                            }
                            if (svg.getMax_Y()>=Max_y){
                                Max_y=svg.getMax_Y();
                            }
                            if (svg.getMin_X()<=Min_x){
                                Min_x=svg.getMin_X();
                            }
                            if (svg.getMin_Y()<=Min_y){
                                Min_y=svg.getMin_Y();
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:// 结束元素事件
                        name = parser.getName();
                        if ("path".equals(name)) {

                            list.add(irregularArea);
                        }
                        break;
                }

                mapArea.setIrregularAreaslist(list);
                mapArea.setMax_x(Max_X);
                mapArea.setMax_y(Max_y);
                mapArea.setMin_x(Min_x);
                mapArea.setMin_y(Min_y);
                eventType = parser.next();
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
