package stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.zt.model.StockSubscriber;
import com.zt.util.StockUtil;

import lombok.val;

public class Test {
    private final static ConcurrentHashMap<String, Aa> cMap = new ConcurrentHashMap<String, Aa>();
    
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Action", "65501");
        map.put("UserCode", "26900609");
        map.put("Type", "LastMonth");
        //		map.put("MobileCode", "10.18.9.54 #4437e65a0f17#AA000000000000006926");
        //		map.put("Token", "oiqvVk980@F2E5-67C8317");
        //		
        //		map.put("IntactToServer", "@ClZvbHVtZUluZm8JAAAARjJFNS02N0M4");
        //		map.put("Reqno", "1542189440692");
        //		map.put("ReqlinkType", "1");
        //		map.put("newindex", "1");
        //		map.put("Action", "150");
        //		map.put("stockcode", "600030");
        //		map.put("WTACCOUNTTYPE", "SHACCOUNT");
        //		map.put("price", "17.06");
        //		map.put("PriceType", "0");
        //		map.put("direction", "B");
        //		map.put("accountIndex", "2");
        //		map.put("Accountlist", "1");
        //		
        //		map.put("updatesign", "1");
        //		map.put("COMMBATCHENTRUSTINFO", "1");
        //		StockSubscriber subscriber = new StockSubscriber(map, "111", "222");
        //		StockUtil.sendData(subscriber);
        
        Thread thread = new Thread() {
            @Override
            public void run() {
                //                super.run();
                Aa a1 = new Aa();
                Aa one = cMap.putIfAbsent("1", a1);
                Aa a2 = new Aa();
                Aa one1 = cMap.putIfAbsent("1", a2);
                System.err.println(a1);
                System.err.println(a2);
                System.err.println(one);
                System.err.println(one1);
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                //                super.run();
                Aa a1 = new Aa();
                Aa one = cMap.putIfAbsent("1", a1);
                Aa a2 = new Aa();
                Aa one1 = cMap.putIfAbsent("1", a2);
                System.err.println(a1);
                System.err.println(a2);
                System.err.println(one);
                System.err.println(one1);
                
            }
        };
        
        thread.start();
        thread2.start();
        
        Aa aa = new Aa();
        Aa one = cMap.putIfAbsent("1", aa);
        Aa bb = new Aa();
        Aa two = cMap.putIfAbsent("2", bb);
        Aa cc = new Aa();
        Aa three = cMap.putIfAbsent("3", cc);
        Aa dd = new Aa();
        Aa oneCopy = cMap.putIfAbsent("1", dd);
        Aa ee = new Aa();
        Aa four = cMap.putIfAbsent("3", ee);
        //        System.err.println(cMap.size());
        ArrayList<Aa> lists = new ArrayList<>();
        ConcurrentHashMap<String, Aa> cMap2 = new ConcurrentHashMap<String, Aa>();
        int count = 1;
        for (Aa a : cMap2.values()) {
            if (a == null) {
                System.err.println("null");
            }
            lists.add(a);
            cMap2.remove("3");
            System.out.println(++count + "次数");
        }
    }
    
//    @test
    public void test() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Action", "65501");
        map.put("UserCode", "26900609");
        map.put("Type", "LastMonth");
        //		map.put("MobileCode", "10.18.9.54 #4437e65a0f17#AA000000000000006926");
        //		map.put("Token", "oiqvVk980@F2E5-67C8317");
        //		
        //		map.put("IntactToServer", "@ClZvbHVtZUluZm8JAAAARjJFNS02N0M4");
        //		map.put("Reqno", "1542189440692");
        //		map.put("ReqlinkType", "1");
        //		map.put("newindex", "1");
        //		map.put("Action", "150");
        //		map.put("stockcode", "600030");
        //		map.put("WTACCOUNTTYPE", "SHACCOUNT");
        //		map.put("price", "17.06");
        //		map.put("PriceType", "0");
        //		map.put("direction", "B");
        //		map.put("accountIndex", "2");
        //		map.put("Accountlist", "1");
        //		
        //		map.put("updatesign", "1");
        //		map.put("COMMBATCHENTRUSTINFO", "1");
        //		StockSubscriber subscriber = new StockSubscriber(map, "111", "222");
        //		StockUtil.sendData(subscriber);
        
        Thread thread = new Thread() {
            @Override
            public void run() {
                //                super.run();
                Aa a1 = new Aa();
                Aa one = cMap.putIfAbsent("1", a1);
                Aa a2 = new Aa();
                Aa one1 = cMap.putIfAbsent("1", a2);
                System.err.println(a1);
                System.err.println(a2);
                System.err.println(one);
                System.err.println(one1);
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                //                super.run();
                Aa a1 = new Aa();
                Aa one = cMap.putIfAbsent("1", a1);
                Aa a2 = new Aa();
                Aa one1 = cMap.putIfAbsent("1", a2);
                System.err.println(a1);
                System.err.println(a2);
                System.err.println(one);
                System.err.println(one1);
                
            }
        };
        
        thread.start();
        thread2.start();
        
        Aa aa = new Aa();
        Aa one = cMap.putIfAbsent("1", aa);
        Aa bb = new Aa();
        Aa two = cMap.putIfAbsent("2", bb);
        Aa cc = new Aa();
        Aa three = cMap.putIfAbsent("3", cc);
        Aa dd = new Aa();
        Aa oneCopy = cMap.putIfAbsent("1", dd);
        Aa ee = new Aa();
        Aa four = cMap.putIfAbsent("3", ee);
        //        System.err.println(cMap.size());
        ArrayList<Aa> lists = new ArrayList<>();
        ConcurrentHashMap<String, Aa> cMap2 = new ConcurrentHashMap<String, Aa>();
        int count = 1;
        for (Aa a : cMap2.values()) {
            if (a == null) {
                System.err.println("null");
            }
            lists.add(a);
            cMap2.remove("3");
            System.out.println(++count + "次数");
        }
    }
}

class Aa {
    
}