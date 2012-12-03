package railo.commons.io;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;

public class FileRotation {
	public static void checkFile(Resource res, long maxFileSize, int maxFiles, byte[] header) throws IOException {
        boolean writeHeader=false;
        // create file
        if(!res.exists()) {
            res.createFile(true);
            writeHeader=true;
        }
        else if(res.length()==0) {
            writeHeader=true;
        }
        
        
        // create new file
        else if(res.length()>maxFileSize) {
            Resource parent = res.getParentResource();
            String name = res.getName();
            int lenMaxFileSize=(""+maxFiles).length();       
            for(int i=maxFiles;i>0;i--) {
            	
                Resource to=parent.getRealResource(name+"."+StringUtil.addZeros(i, lenMaxFileSize)+".bak");
                Resource from=parent.getRealResource(name+"."+StringUtil.addZeros(i-1,lenMaxFileSize)+".bak");
                if(from.exists()) {
                    if(to.exists())to.delete();
                    from.renameTo(to);
                }
            }
            res.renameTo(parent.getRealResource(name+"."+StringUtil.addZeros(1,lenMaxFileSize)+".bak"));
            res=parent.getRealResource(name);//new File(parent,name);
            res.createNewFile();
            writeHeader=true;
        }
        else if(header!=null && header.length>0) {
        	byte[] buffer = new byte[header.length];
            int len;
            InputStream in = null;
            try{
	            in = res.getInputStream();
	            boolean headerOK = true;
	            len = in.read(buffer);
	        	if(len==header.length){
	        		for(int i=0;i<header.length;i++) {
	        			if(header[i]!=buffer[i]){
	        				headerOK=false;
	        				break;
	        			}
	        		}
	        	}
	        	else headerOK=false;
	            if(!headerOK)writeHeader=true;
            }
            finally {
            	IOUtil.closeEL(in);
            }
        }
        
        
        if(writeHeader) {
            if(header==null)header=new byte[0];
            IOUtil.write(res, header,false);
           
        }   
    }
}
