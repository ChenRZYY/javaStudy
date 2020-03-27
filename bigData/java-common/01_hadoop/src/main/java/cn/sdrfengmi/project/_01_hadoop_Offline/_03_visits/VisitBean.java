package cn.sdrfengmi.project._01_hadoop_Offline._03_visits;

import lombok.Data;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Data
public class VisitBean implements Writable {

    private String session;
    private String remote_addr;
    private String inTime;
    private String outTime;
    private String inPage;
    private String outPage;
    private String referal;
    private int pageVisits;

    public void set(String session, String remote_addr, String inTime, String outTime, String inPage, String outPage, String referal, int pageVisits) {
        this.session = session;
        this.remote_addr = remote_addr;
        this.inTime = inTime;
        this.outTime = outTime;
        this.inPage = inPage;
        this.outPage = outPage;
        this.referal = referal;
        this.pageVisits = pageVisits;
    }

    @Override
    public String toString() {
        return session + "\001" + remote_addr + "\001" + inTime + "\001" + outTime + "\001" + inPage + "\001" + outPage + "\001" + referal + "\001" + pageVisits;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.session = in.readUTF();
        this.remote_addr = in.readUTF();
        this.inTime = in.readUTF();
        this.outTime = in.readUTF();
        this.inPage = in.readUTF();
        this.outPage = in.readUTF();
        this.referal = in.readUTF();
        this.pageVisits = in.readInt();

    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(session);
        out.writeUTF(remote_addr);
        out.writeUTF(inTime);
        out.writeUTF(outTime);
        out.writeUTF(inPage);
        out.writeUTF(outPage);
        out.writeUTF(referal);
        out.writeInt(pageVisits);

    }
}
