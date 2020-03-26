package cn.sdrfengmi._01_mapReduce._03_sortBean;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SortBean implements WritableComparable<SortBean> {  //TODO 序列化&排序

    private String word;
    private int num;

    public SortBean() {
    }

    public SortBean(String word, int num) {
        this.word = word;
        this.num = num;
    }

    @Override
    public int compareTo(SortBean sortBean) {

        int i = sortBean.getWord().compareTo(word);
        if (i == 0) {
            return num - sortBean.getNum();
        }
        return i;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(word);
        dataOutput.writeInt(num);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        word = dataInput.readUTF();
        num = dataInput.readInt();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

//    @Override
//    public String toString() {
//        return   word + "\t"+ num ;
//    }

}
