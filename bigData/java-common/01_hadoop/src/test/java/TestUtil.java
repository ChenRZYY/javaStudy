import org.junit.Test;

public class TestUtil {


    @Test
    public void str() {
        String QUERY_POS = "chen";
        System.out.println(QUERY_POS.length());
        QUERY_POS = QUERY_POS + "\0";
        System.out.println(QUERY_POS);
        System.out.println(QUERY_POS.length());

        String a = "陈";
        System.out.println(a+a.length());
        a = a + "\0";
        System.out.println(a+a.length());

    }
}

