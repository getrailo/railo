package railo;

import java.io.IOException;

import railo.loader.TP;
import railo.loader.util.Util;

/**
 * returns th current built in version
 */
public class Version {

    private static int version=-1;
    private static long created=-1;
    
    
    /**
     * @return returns the current version 
     */
    public static int getIntVersion() {
        init();
        return version;
    }
    
    /**
     * return creattion time of this version
     * @return creattion time
     */
    public static long getCreateTime() {
        init();
        return created;
    }


    private static void init() {
        if(version!=-1) return;
        String content="9000000:"+System.currentTimeMillis();
        try {
            content= Util.getContentAsString(
                    new TP().getClass().getClassLoader().getResourceAsStream("railo/version"),
                    "UTF-8");
            
            
        } 
        catch (IOException e) {} 
        
        int index=content.indexOf(':');
        version=Integer.parseInt(content.substring(0,index));
        created=Long.parseLong(content.substring(index+1));
        
    }
}
