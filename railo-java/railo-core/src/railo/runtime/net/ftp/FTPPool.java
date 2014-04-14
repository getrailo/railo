package railo.runtime.net.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

import railo.runtime.exp.PageException;

/**
 * FTP Pool
 */
public interface FTPPool {

    /**
     * returns a FTPClient from the pool, if no matching exist, create a new one
     * @param conn
     * @return Matching FTP Client
     * @throws IOException
     * @throws PageException
     */
    public abstract FTPClient get(FTPConnection conn) throws IOException, PageException;

    /**
     * removes a FTPConnection from pool andreturn it (disconnected)
     * @param conn 
     * @return disconnetd Client
     */
    public abstract FTPClient remove(FTPConnection conn);

    /**
     * removes a FTPConnection from pool andreturn it (disconnected)
     * @param name Name of the connection to remove
     * @return disconnetd Client
     */
    public abstract FTPClient remove(String name);

    /**
     * clears all connection from pool
     */
    public abstract void clear();

}