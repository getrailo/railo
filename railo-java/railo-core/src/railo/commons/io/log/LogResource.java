package railo.commons.io.log;


import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

/**
 * Simple Logger to log data to a file
 */
public class LogResource implements Log {
    
    private Resource res;


	/**
     * maximum number of files (history of files) 
     */
    public static final int MAX_FILES=10;

    private long maxFileSize;
    private int maxFiles;
    private int count=0;
    private int logLevel;
	private String charset;
    
    
    /**
     * Constructor of the Logger 
     * @param res resource to log to
     * @param logLevel 
     * @throws IOException 
     */
    public LogResource(Resource res, int logLevel, String charset) throws IOException {
        this(res,MAX_FILE_SIZE,MAX_FILES,logLevel,charset);
    }
    
    /**
     * Constructor of the Logger 
     * @param res resource to log to
     * @param maxFileSize max file size if file is greater creates a backup file of the actuell file and creates a new one.
     * @param logLevel 
     * @throws IOException 
     */
    public LogResource(Resource res, long maxFileSize, int logLevel, String charset) throws IOException {
        this(res,maxFileSize,MAX_FILES,logLevel,charset);
    }
    
    /**
     * Constructor of the Logger 
     * @param res resource to log to
     * @param maxFileSize max file size if file is greater creates a backup file of the actuell file and creates a new one.
     * @param maxFiles max count of files
     * @param logLevel 
     * @throws IOException 
     */
    public LogResource(Resource res, long maxFileSize, int maxFiles, int logLevel, String charset) throws IOException {
        this.res=res;
        this.maxFileSize=maxFileSize;
        this.maxFiles=maxFiles;
        this.logLevel=logLevel;
        this.charset=charset;
        checkFile();
        
    }
    
    
    /**
     * check files and creates a new one if to great or not exist
     * @throws IOException
     */
    private void checkFile() throws IOException {
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
        if(writeHeader) {
            writeHeader=false;
            write(LogUtil.getHeader());
        }   
    }

    @Override
    public void log(int level, String application, String message) {
        if(level<logLevel) return;
        count++;
        if(count++>100) {
            try {
                checkFile();
            } catch (IOException e) {}
            count=0;
        }
        String line=LogUtil.getLine(level,application,message);
        write(line);
        
    }
    private void write(String str) {
        try {
            IOUtil.write(res,str,charset,true);
        } catch (IOException e) {}
    }
    @Override
    public void info(String application, String message) {
        log(LEVEL_INFO,application,message);
    }
    @Override
    public void debug(String application, String message) {
        log(LEVEL_DEBUG,application,message);    
    }
    @Override
    public void warn(String application, String message) {
        log(LEVEL_WARN,application,message);
    }
    @Override
    public void error(String application, String message) {
        log(LEVEL_ERROR,application,message);
    }
    @Override
    public void fatal(String application, String message) {
        log(LEVEL_FATAL,application,message);
    }
    /**
     * @return Returns the resource.
     */
    public Resource getResource() {
        return res;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(int level) {
        this.logLevel=level;
    }
    

    @Override
    public String toString(){
    	return res.getAbsolutePath();
    }
    
    /**
     * maximum file size of for a log file
     */
    public static final long MAX_FILE_SIZE=1024*1024;
    /**
	 * @return the maxFileSize
	 */
	public long getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * @return the maxFiles
	 */
	public int getMaxFiles() {
		return maxFiles;
	}
}