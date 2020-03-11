package cn.zt;

//import com.ibm.broker.javacompute.MbJavaComputeNode;
//import com.ibm.broker.plugin.*;

//import java.io.UnsupportedEncodingException;
//import java.text.SimpleDateFormat;
//import java.util.ResourceBundle;
//
//import com.macli.COMM_TYPE;
//import com.macli.FID;
//import com.macli.MACLI_HEAD_FID;
//import com.macli.MACLI_OPTION;
//import com.macli.MACLI_SIZE;
//import com.macli.PROTOCAL;
//import com.macli.ST_MACLI_CONNECT_OPTION;
//import com.macli.ST_MACLI_SYNCCALL;
//import com.macli.ST_MACLI_USERINFO;
//import com.macli.maCliApi;
//
//class fix_fields {
//    public byte szOpUser[] = new byte[32];
//    public byte btOpRole = '1';
//    public byte szOpSite[] = new byte[257];
//    public byte btOpChannel = '0';
//    public byte szOpOrg[] = new byte[8];
//    public byte szSession[] = new byte[129];
//    public byte szFunction[] = new byte[9];
//    public byte szRuntime[] = new byte[27];
//    public byte btAcctType = 0;
//}
//
//class acct_info {
//    public byte szStkbd[] = new byte[3];
//    public byte szTrdacct[] = new byte[11];
//    public byte szStkpbu[] = new byte[9];
//}
//
//class pscb_data {
//    public byte szAcceptSn[];
//    public byte szPubData[];
//    public int nDataLen;
//}
//
//class arcb_data {
//    public byte szMsgId[];
//    public byte szAnsData[];
//    public int nDataLen;
//}
//
//public class KDRzrqCustAsset {
//
//    private static final String CUACCT_CODE = null;
//    protected static maCliApi cliapi = new maCliApi();
//    public static long handle = 0;
//    protected static MSG_MODE eAnsMode = MSG_MODE.SYNC;
//    protected MSG_MODE ePubMode = MSG_MODE.SYNC;
//    protected static acct_info[] arrAcctInfo;
//    protected static fix_fields stFixFields;
//    protected static byte szPktVer[] = {'0', '1'};
//    protected byte szChannel[];
//    public static String strIp = ResourceBundle.getBundle("kd").getString("kd.SvrAddress");
//    public static int nPort = Integer.parseInt(ResourceBundle.getBundle("kd").getString("kd.SvrPort"));
//    public static String strAcct = "10997002\0";
//    public String strKey = "123444\0";
//    public int nStat = 1;
//    public int nNum = 1;
//
//    public static int cli_init() {
//        int ret = 0;
//        long[] handles = new long[1];
//        ret = cliapi.maCli_Init(handles); //TODO 初始化
//        if (ret == 0) {
//            handle = handles[0];
//        } else {
//            System.out.println("cli_init fail");
//        }
//        return ret;
//    }
//
//    public static int cli_open() {
//        int ret = -1;
//        if (eAnsMode == MSG_MODE.NONE && eAnsMode == MSG_MODE.NONE) {
//            return ret;
//        }
//        ST_MACLI_CONNECT_OPTION ConnOpt = new ST_MACLI_CONNECT_OPTION();
//        ConnOpt.szServerName = ("SERVER\0").getBytes();
//        ConnOpt.nCommType = COMM_TYPE.SOCKET;
//        ConnOpt.nProtocal = PROTOCAL.TCP;
//        ConnOpt.szSvrAddress = strIp.getBytes();
//        ConnOpt.nSvrPort = nPort;
//        cliapi.maCli_SetOptions(handle, MACLI_OPTION.CONNECT_PARAM, cliapi.ConnectOptionToBytes(ConnOpt), 1552);
//        cliapi.maCli_SetOptions(handle, MACLI_OPTION.SYNCCALL_TIMEOUT, cliapi.IntToBytes(5), 4);
//        cliapi.maCli_SetOptions(handle, MACLI_OPTION.ASYNCALL_TIMEOUT, cliapi.IntToBytes(5), 4);
//
//        ST_MACLI_USERINFO UserInfo = new ST_MACLI_USERINFO();
//
//        String ServerName = ResourceBundle.getBundle("kd").getString("kd.ServerName");
//        UserInfo.szServerName = ServerName.getBytes();
//        String OpId = ResourceBundle.getBundle("kd").getString("kd.OpId");
//        UserInfo.szUserId = OpId.getBytes();
//        String OpPassWord = ResourceBundle.getBundle("kd").getString("kd.OpPassWord");
//        UserInfo.szPassword = OpPassWord.getBytes();
//        String AppId = ResourceBundle.getBundle("kd").getString("kd.AppId");
//        UserInfo.szAppId = AppId.getBytes();
//        String AuthCode = ResourceBundle.getBundle("kd").getString("kd.AuthCode");
//        UserInfo.szAuthCode = AuthCode.getBytes();
//        ret = cliapi.maCli_Open(handle, UserInfo);  //TODO 连接
//        if (ret != 0) {
//            System.out.println("cli_open fail, " + lastmsg());
//            return ret;
//        }
//        if (stFixFields == null) {
//            stFixFields = new fix_fields();
//        }
//        return ret;
//    }
//
//    public static int cli_close() {
//        int ret = -1;
//        if (eAnsMode == MSG_MODE.NONE && eAnsMode == MSG_MODE.NONE) {
//            return ret;
//        }
//        System.out.println("cli_close recalled");
//        if (handle > 0) {
//            cliapi.maCli_SetArCallback(handle, null);
//            cliapi.maCli_SetPsCallback(handle, null);
//            ret = cliapi.maCli_Close(handle); //TODO 关闭连接
//            if (ret != 0) {
//                System.out.println("cli_close fail, " + lastmsg());
//            }
//        }
//        return ret;
//    }
//
//    public static int cli_fini() {
//        int ret = 0;
//        System.out.println("cli_fini recalled");
//        if (handle > 0) {
//            ret = cliapi.maCli_Exit(handle);  //TODO ???
//            if (ret != 0) {
//                System.out.println(" cli_fini fail, " + lastmsg());
//            }
//            System.out.println(" end");
//            handle = 0;
//        }
//        return ret;
//    }
//
//    public static String lastmsg() { //TODO 获取异常信息
//        int[] errcode = new int[1];
//        cliapi.maCli_GetLastErrorCode(handle, errcode);
//        byte[] errmsg = new byte[1024 + 1];
//        cliapi.maCli_GetLastErrorMsg(handle, errmsg, 1024);
//        return String.format("errcode:%d,errmsg:%s", errcode[0], getString(errmsg));
//    }
//
//    public static String getString(byte szBytes[]) {
//        String str = new String();
//        try {
//            str = new String(szBytes, "GBK");
//            str = str.trim();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return str;
//    }
//
//    public static int pkthead(byte szFuncId[], long lReqId, byte szMsgId[], byte btPkgType, long lTimeStamp) {
//        cliapi.maCli_SetHdrValueC(handle, (byte) 'R', MACLI_HEAD_FID.MSG_TYPE);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
//        cliapi.maCli_SetHdrValue(handle, sdf.format(lTimeStamp).getBytes(), 17, MACLI_HEAD_FID.TIMESTAMP);
//        cliapi.maCli_SetHdrValue(handle, szFuncId, 8, MACLI_HEAD_FID.FUNC_ID);
//        cliapi.maCli_SetHdrValueC(handle, (byte) 'R', MACLI_HEAD_FID.MSG_TYPE);
//        if (btPkgType == 'S') {
//            cliapi.maCli_SetHdrValue(handle, ("01\0").getBytes(), 2, MACLI_HEAD_FID.PKT_VER);
//        } else {
//            cliapi.maCli_SetHdrValue(handle, szPktVer, 2, MACLI_HEAD_FID.PKT_VER);
//        }
//        cliapi.maCli_SetHdrValueC(handle, btPkgType, MACLI_HEAD_FID.PKT_TYPE);
//        System.out.println("---" + String.format("%08d%016x%08x", 1, lTimeStamp, lReqId));
//        szMsgId = String.format("%08d%016x%08x", 1, lTimeStamp, lReqId).getBytes();
//        cliapi.maCli_SetHdrValue(handle, szMsgId, MACLI_SIZE.MSG_ID_SIZE, MACLI_HEAD_FID.MSG_ID);
//        return 0;
//    }
//
//    public static int pktfix(byte szFuncId[], long lTimeStamp) {
//        if (stFixFields.szOpUser[0] != 0) {
//            cliapi.maCli_SetValueS(handle, stFixFields.szOpUser, FID.OP_USER_CODE);
//        } else {
//            cliapi.maCli_SetValueC(handle, (byte) '0', FID.OP_USER_CODE);
//        }
//
//        cliapi.maCli_SetValueC(handle, stFixFields.btOpRole, FID.OP_USERROLE);
//        if (stFixFields.szOpSite[0] == 0) {
//            byte bVal[] = new byte[257];
//            if (cliapi.maCli_GetOptions(handle, MACLI_OPTION.CPU_ID, bVal, 256) == 0) {
//                stFixFields.szOpSite = getString(bVal).getBytes();
//            }
//        }
//        cliapi.maCli_SetValueS(handle, stFixFields.szOpSite, FID.OP_SITE);
//        if (stFixFields.szSession[0] == 0) {
//            cliapi.maCli_GetVersion(handle, stFixFields.szSession, 128);
//        }
//        cliapi.maCli_SetValueS(handle, stFixFields.szSession, FID.SESSION);
//        cliapi.maCli_SetValueC(handle, stFixFields.btOpChannel, FID.OP_CHANNEL);
//        cliapi.maCli_SetValueS(handle, szFuncId, FID.FUNCID);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S000\0");
//        cliapi.maCli_SetValue(handle, sdf.format(lTimeStamp).getBytes(), 26, FID.RUN_TIME);
//        if (stFixFields.szOpOrg[0] != 0) {
//            cliapi.maCli_SetValueS(handle, stFixFields.szOpOrg, FID.OP_ORG);
//        } else {
//            cliapi.maCli_SetValueC(handle, (byte) '0', FID.OP_ORG);
//        }
//        if (stFixFields.btAcctType != 0) {
//            cliapi.maCli_SetValueC(handle, stFixFields.btAcctType, FID.CUACCT_TYPE);
//        }
//        return 0;
//    }
//
//    public enum MSG_MODE {
//        NONE, CALLBACK, GET, SYNC,
//    }
//
//
//    public static void main(String argc[]) {
//
//        String CUACCT_CODE = "10997002";
//
//        if (KDRzrqCustAsset.cli_init() != 0) {
//            return;
//        } else if (KDRzrqCustAsset.cli_open() == 0) {
//
//            int iret = 0;
//            byte szFuncId[] = new byte[8 + 1];
//            szFuncId = ("10323006\0").getBytes();
//            long lReqId = 0;
//            long lTimeStamp = System.currentTimeMillis();
//            byte[] szMsgId = new byte[MACLI_SIZE.MSG_ID_SIZE + 1];
//            cliapi.maCli_BeginWrite(handle);
//            pkthead(szFuncId, lReqId, szMsgId, (byte) 'B', lTimeStamp);
//            pktfix(szFuncId, lTimeStamp);
//            cliapi.maCli_SetValueS(handle, CUACCT_CODE.getBytes(), FID.CUACCT_CODE);
//            cliapi.maCli_SetValueS(handle, ("0\0").getBytes(), FID.CURRENCY);
//
//            cliapi.maCli_EndWrite(handle);
//            ST_MACLI_SYNCCALL SyncCall = new ST_MACLI_SYNCCALL();
//            SyncCall.nTimeout = 0;
//            iret = cliapi.maCli_SyncCall(handle, SyncCall);
//
//            String MSG_CODE;
//            String MSG_TEXT;
//            //
//            if (iret == 0) {
//                int iTableCount[] = new int[1];
//                iret = cliapi.maCli_GetTableCount(handle, iTableCount);
//                System.out.println("===" + iTableCount[0]);
//                if (iTableCount[0] > 1) {
//                    cliapi.maCli_OpenTable(handle, 1);
//                    cliapi.maCli_ReadRow(handle, 1);
//                    int[] szMsgCode = new int[1];
//                    cliapi.maCli_GetValueN(handle, szMsgCode, FID.MSG_CODE);
//                    byte szMsgText[] = new byte[257];
//                    cliapi.maCli_GetValueS(handle, szMsgText, 256, FID.MSG_TEXT);
//                    MSG_CODE = szMsgCode[0] + "";
//                    MSG_TEXT = getString(szMsgText);
//
//
//                    cliapi.maCli_OpenTable(handle, 2);
//                    int iRowCount[] = new int[1];
//                    cliapi.maCli_GetRowCount(handle, iRowCount);
//                    if (iRowCount[0] > 0) {
//                        for (int nRow = 0; nRow < iRowCount[0]; ++nRow) {
//                            cliapi.maCli_ReadRow(handle, nRow + 1);
//                            double[] szTotalAssert = new double[1];
//                            cliapi.maCli_GetValueD(handle, szTotalAssert, FID.TOTAL_ASSERT);
//                            String TOTAL_ASSERT = szTotalAssert[0] + "";
//
//                            double[] szTotalDebts = new double[1];
//                            cliapi.maCli_GetValueD(handle, szTotalDebts, FID.TOTAL_DEBTS);
//                            String TOTAL_DEBTS = szTotalDebts[0] + "";
//
//                            long[] szCustCode = new long[1];
//                            cliapi.maCli_GetValueL(handle, szCustCode, FID.CUST_CODE);
//                            String CUST_CODE = szCustCode[0] + "";
//
//                            System.out.println("TOTAL_ASSERT:" + TOTAL_ASSERT);
//                            System.out.println("TOTAL_DEBTS:" + TOTAL_DEBTS);
//                            System.out.println("CUST_CODE:" + CUST_CODE);
//                        }
//                    }
//                }
//            } else {
//                int[] errcode = new int[1];
//                cliapi.maCli_GetLastErrorCode(handle, errcode);
//                byte[] errmsg = new byte[1024 + 1];
//                cliapi.maCli_GetLastErrorMsg(handle, errmsg, 1024);
//                MSG_CODE = errcode[0] + "";
//                MSG_TEXT = getString(errmsg);
//            }
//
//        }
//        cli_close();
//        cli_fini();
//    }
//
//}
