package railo.commons.io;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.res.Resource;

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
        
        
        // creaste new file
        else if(res.length()>maxFileSize) {
            Resource parent = res.getParentResource();
            String name = res.getName();
                        
            for(int i=maxFiles;i>0;i--) {
            	
                Resource to=parent.getRealResource(name+"."+i+".bak");
                Resource from=parent.getRealResource(name+"."+(i-1)+".bak");
                if(from.exists()) {
                    if(to.exists())to.delete();
                    from.renameTo(to);
                }
            }
            res.renameTo(parent.getRealResource(name+".1.bak"));
            res=parent.getRealResource(name);//new File(parent,name);
            res.createNewFile();
            writeHeader=true;
        }
        else {
        	byte[] buffer = new byte[header.length];
            int len;
            InputStream in = res.getInputStream();
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
        
        
        if(writeHeader) {
            IOUtil.write(res, header);
        }   
    }
}
