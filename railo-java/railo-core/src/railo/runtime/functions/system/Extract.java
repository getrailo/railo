

package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.CompressUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.List;

/**
 * Implements the Cold Fusion Function compress
 */
public final class Extract implements Function {
        
    public static boolean call(PageContext pc , String strFormat, String strSource, String srcTarget) throws PageException {
        
        strFormat=strFormat.trim().toLowerCase();
        int format=CompressUtil.FORMAT_ZIP;
        if(strFormat.equals("bzip")) format=CompressUtil.FORMAT_BZIP;
        else if(strFormat.equals("bzip2")) format=CompressUtil.FORMAT_BZIP2;
        else if(strFormat.equals("gzip")) format=CompressUtil.FORMAT_GZIP;
        else if(strFormat.equals("tar")) format=CompressUtil.FORMAT_TAR;
        else if(strFormat.equals("tbz")) format=CompressUtil.FORMAT_TBZ;
        else if(strFormat.startsWith("tar.bz")) format=CompressUtil.FORMAT_TBZ;
        else if(strFormat.equals("tbz2")) format=CompressUtil.FORMAT_TBZ2;
        else if(strFormat.startsWith("tar.gz")) format=CompressUtil.FORMAT_TGZ;
        else if(strFormat.equals("tgz")) format=CompressUtil.FORMAT_TGZ;
        else if(strFormat.equals("zip")) format=CompressUtil.FORMAT_ZIP;
        else throw new FunctionException(pc,"compress",1,"format","invalid format definition ["+strFormat+"]," +
                " valid formats are [bzip,gzip,tar,tbz (tar bzip),tgz (tar gzip) and zip]");
        
        
        String[] arrSources=List.toStringArrayEL(List.listToArrayRemoveEmpty(strSource,","));
        
        Resource[] sources=new Resource[arrSources.length];
        for(int i=0;i<sources.length;i++) {
            sources[i]=ResourceUtil.toResourceExisting(pc, arrSources[i]);
            	//FileUtil.toFileExisting(pc,arrSources[i]);
            pc.getConfig().getSecurityManager().checkFileLocation(sources[i]);
        }

        
        Resource target=ResourceUtil.toResourceExisting(pc,srcTarget);
        pc.getConfig().getSecurityManager().checkFileLocation(target);
        
        try {
            CompressUtil.extract(format,sources,target);
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
        return true;
    }
    
}