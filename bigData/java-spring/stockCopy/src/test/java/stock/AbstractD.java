package stock;

import io.netty.channel.Channel;

abstract class AbstractD {
    
    private int a = 0;
    
    protected AbstractD(Channel parent) {
        
        a = 5;
    }
}
