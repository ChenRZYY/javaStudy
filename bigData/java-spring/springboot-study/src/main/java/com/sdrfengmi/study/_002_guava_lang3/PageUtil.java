package com.sdrfengmi.study._002_guava_lang3;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 指定开始位置和结束位置获取结果，for懒加载
 */
@Component
public class PageUtil {


    //按请求中的startPos和maxCount返回数据段
    public <T> List<T> getResultByPosit(String startPos, String maxCount, List<T> rowDate){
        int[] rangArr = getRangAndNextPos(startPos, maxCount);
        return getResultRang(rangArr[0], rangArr[1], rowDate);
    }


    public <T> List<T> getResultRang(int startNo, int endNo, List<T> rowDate){
        if(startNo > rowDate.size()){
            return Lists.newArrayList(); //返回空list
        } else{
            endNo = ((endNo <= 0) || (endNo > rowDate.size()))? rowDate.size() : (endNo + 1);//为了与redis中的返回行数保持一致，增加1行返回
            //return Lists.newArrayList(rowDate.subList(startNo, endNo));//新new一个arrayist对象，避免使用者因返回的是sublist报错，会损失一些性能
            return rowDate.subList(startNo, endNo);
        }
    }


    //按中焯规则根据请求中的startPos和maxCount计算请求数据的范围:startNo、endNo和下次入参的startPos
    public int[] getRangAndNextPos(String reqStartPos, String reqMaxCount) {
        String startPos = StringUtils.trimToEmpty(reqStartPos);
        String maxCount = StringUtils.trimToEmpty(reqMaxCount);
        //startPos=0表示是表头，startPos=1表示从第1条开始
        int startNo = (StringUtils.isNumeric(startPos) && (Integer.valueOf(startPos)-1) > 0) ? (Integer.valueOf(startPos)-1) : 0;
        int endNo;
        int nextStartPos;
        if (maxCount.length() != 0) {
            if(StringUtils.isNumeric(maxCount) && Integer.parseInt(maxCount) > 0){//maxCount>0 时返回maxCount条，下次起始位置为maxCount
                endNo = startNo + Integer.parseInt(maxCount) - 1;
                if(StringUtils.isNumeric(startPos) && Integer.valueOf(startPos)> 0){
                    nextStartPos = Integer.parseInt(maxCount);//startPos>0 时，nextStartPos为maxCount
                }else{
                    nextStartPos = Integer.parseInt(maxCount)+ 1 ; //startPos<=0 或输入非数字时，nextStartPos为maxCount+1
                }
            }else{//maxCount<=0 或输入非数字时从返回全部，下次起始位置为空
                startNo = 0;
                endNo = -1;  //redis中-1表示取全部
                nextStartPos = 0;
            }
        } else {//maxCount为空时默认返回100条,下次起始位置为第100
            endNo = startNo + 100 - 1 ;
            if(StringUtils.isNumeric(startPos) && Integer.valueOf(startPos)> 0){
                nextStartPos = 100;//startPos>0 时，nextStartPos为100
            }else{
                nextStartPos = 100 + 1 ;//startPos<=0 或输入非数字时，nextStartPos为101
            }
        }
        return new int[]{startNo, endNo, nextStartPos};
    }


}
