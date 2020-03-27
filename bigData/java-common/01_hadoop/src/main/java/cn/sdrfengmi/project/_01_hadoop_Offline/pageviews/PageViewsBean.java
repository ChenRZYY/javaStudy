package cn.sdrfengmi.project._01_hadoop_Offline.pageviews;

import lombok.Data;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Data
public class PageViewsBean implements Writable {

    private String session;
    private String remote_addr;
    private String timestr;
    private String request;
    private int step;    //几步
    private String staylong; //停留时间
    private String referal;
    private String useragent;
    private String bytes_send;
    private String status;

    public void set(String session, String remote_addr, String useragent, String timestr, String request, int step, String staylong, String referal, String bytes_send, String status) {
        this.session = session;
        this.remote_addr = remote_addr;
        this.useragent = useragent;
        this.timestr = timestr;
        this.request = request;
        this.step = step;
        this.staylong = staylong;
        this.referal = referal;
        this.bytes_send = bytes_send;
        this.status = status;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(session);
        out.writeUTF(remote_addr);
        out.writeUTF(timestr);
        out.writeUTF(request);
        out.writeInt(step);
        out.writeUTF(staylong);
        out.writeUTF(referal);
        out.writeUTF(useragent);
        out.writeUTF(bytes_send);
        out.writeUTF(status);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.session = in.readUTF();
        this.remote_addr = in.readUTF();
        this.timestr = in.readUTF();
        this.request = in.readUTF();
        this.step = in.readInt();
        this.staylong = in.readUTF();
        this.referal = in.readUTF();
        this.useragent = in.readUTF();
        this.bytes_send = in.readUTF();
        this.status = in.readUTF();
    }
}
