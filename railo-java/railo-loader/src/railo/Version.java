package railo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import railo.loader.TP;

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
            content= getContentAsString(
                    new TP().getClass().getClassLoader().getResourceAsStream("railo/version"),
                    "UTF-8");
            
            
        } 
        catch (IOException e) {} 
        
        int index=content.indexOf(':');
        version=Integer.parseInt(content.substring(0,index));
        created=Long.parseLong(content.substring(index+1));
        
    }
    
    private static String getContentAsString(InputStream is, String charset) throws IOException {
        
        BufferedReader br = (charset==null)?
                new BufferedReader(new InputStreamReader(is)):
                new BufferedReader(new InputStreamReader(is,charset)); 
        StringBuffer content=new StringBuffer();
        
        String line=br.readLine();
        if(line!=null) {
            content.append(line);
            while((line=br.readLine())!=null)   {
                content.append("\n"+line);
            }
        }
        br.close();
        return content.toString();
     }
}
