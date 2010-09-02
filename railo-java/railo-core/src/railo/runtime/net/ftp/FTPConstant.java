package railo.runtime.net.ftp;

import org.apache.commons.net.ftp.FTPFile;

import railo.runtime.op.Constants;


/**
 * 
 */
public final class FTPConstant {


    /**
     * Field <code>TRANSFER_MODE_AUTO</code>
     */
    public static final short TRANSFER_MODE_AUTO=0;
    /**
     * Field <code>TRANSFER_MODE_BINARY</code>
     */
    public static final short TRANSFER_MODE_BINARY=1;
    /**
     * Field <code>TRANSFER_MODE_ASCCI</code>
     */
    public static final short TRANSFER_MODE_ASCCI=2;

    /**
     * Field <code>PERMISSION_READ</code>
     */
    public static final short PERMISSION_READ=4;
    /**
     * Field <code>PERMISSION_WRITE</code>
     */
    public static final short PERMISSION_WRITE=2;
    
    /**
     * Field <code>PERMISSION_EXECUTE</code>
     */
    public static final short PERMISSION_EXECUTE=1;

    /**
     * Field <code>ACCESS_WORLD</code>
     */
    public static final short ACCESS_WORLD=1;
    /**
     * Field <code>ACCESS_GROUP</code>
     */
    public static final short ACCESS_GROUP=10;
    /**
     * Field <code>ACCESS_USER</code>
     */
    public static final short ACCESS_USER=100;

    
    
    
    /**
     * @param type
     * @return file type as String
     */
    public static String getTypeAsString(int type) {
        if(type==FTPFile.DIRECTORY_TYPE)return "directory";
        else if(type==FTPFile.SYMBOLIC_LINK_TYPE)return "link";
        else if(type==FTPFile.UNKNOWN_TYPE)return "unknown";
        else if(type==FTPFile.FILE_TYPE)return "file";
        
        return "unknown";
    }

    /**
     * @param file
     * @return permission as integer
     */
    public static Integer getPermissionASInteger(FTPFile file) {
        int rtn=0;
        // world
        if(file.hasPermission(FTPFile.WORLD_ACCESS,FTPFile.READ_PERMISSION))rtn+=ACCESS_WORLD*PERMISSION_READ;
        if(file.hasPermission(FTPFile.WORLD_ACCESS,FTPFile.WRITE_PERMISSION))rtn+=ACCESS_WORLD*PERMISSION_WRITE;
        if(file.hasPermission(FTPFile.WORLD_ACCESS,FTPFile.EXECUTE_PERMISSION))rtn+=ACCESS_WORLD*PERMISSION_EXECUTE;

        // group
        if(file.hasPermission(FTPFile.GROUP_ACCESS,FTPFile.READ_PERMISSION))rtn+=ACCESS_GROUP*PERMISSION_READ;
        if(file.hasPermission(FTPFile.GROUP_ACCESS,FTPFile.WRITE_PERMISSION))rtn+=ACCESS_GROUP*PERMISSION_WRITE;
        if(file.hasPermission(FTPFile.GROUP_ACCESS,FTPFile.EXECUTE_PERMISSION))rtn+=ACCESS_GROUP*PERMISSION_EXECUTE;

        // user
        if(file.hasPermission(FTPFile.USER_ACCESS,FTPFile.READ_PERMISSION))rtn+=ACCESS_USER*PERMISSION_READ;
        if(file.hasPermission(FTPFile.USER_ACCESS,FTPFile.WRITE_PERMISSION))rtn+=ACCESS_USER*PERMISSION_WRITE;
        if(file.hasPermission(FTPFile.USER_ACCESS,FTPFile.EXECUTE_PERMISSION))rtn+=ACCESS_USER*PERMISSION_EXECUTE;
        
        return Integer.valueOf(rtn);
    }
}