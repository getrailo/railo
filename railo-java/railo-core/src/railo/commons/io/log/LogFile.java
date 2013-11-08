package railo.commons.io.log;

import java.io.File;
import java.io.IOException;

import railo.commons.io.res.util.ResourceUtil;

/**
 * Simple Logger to log data to a file
 */
 final class LogFile extends LogResource {
    
    /**
     * Constructor of the Logger 
     * @param file file to log to
     * @param logLevel 
     * @throws IOException 
     */
    public LogFile(File file, int logLevel, String charset) throws IOException {
    	super(ResourceUtil.toResource(file),MAX_FILE_SIZE,MAX_FILES,logLevel,charset);
    }
    
    /**
     * Constructor of the Logger 
     * @param file file to log to
     * @param maxFileSize max file size if file is greater creates a backup file of the actuell file and creates a new one.
     * @param logLevel 
     * @throws IOException 
     */
    public LogFile(File file, long maxFileSize, int logLevel, String charset) throws IOException {
        super(ResourceUtil.toResource(file),maxFileSize,MAX_FILES,logLevel,charset);
    }
    
    /**
     * Constructor of the Logger 
     * @param file file to log to
     * @param maxFileSize max file size if file is greater creates a backup file of the actuell file and creates a new one.
     * @param maxFiles max count of files
     * @param logLevel 
     * @throws IOException 
     */
    public LogFile(File file, long maxFileSize, int maxFiles, int logLevel, String charset) throws IOException {
    	super(ResourceUtil.toResource(file),maxFileSize,maxFiles,logLevel,charset);
    }
}