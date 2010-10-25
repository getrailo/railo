package railo.runtime;

import java.io.IOException;

import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;

/**
 * extends the source file with class features 
 */
public interface PageSource extends SourceFile {

    /**
     * loads the Page from this PageSource
     * @param config
     * @return page Loaded
     * @throws PageException
     */
    public abstract Page loadPage(ConfigWeb config) throws PageException;
    
    // FUTURE public abstract Page loadPage(PageContext pc) throws PageException;
    
    /**
     * loads the Page from this PageSource
     * @param config
     * @param defaultValue
     * @return Page loaded
     * @throws PageException 
     */
    public abstract Page loadPage(ConfigWeb config, Page defaultValue) throws PageException;
 // FUTURE public abstract Page loadPage(PageContext pc, Page defaultValue) throws PageException;
    
    /**
     * returns the ralpath without the mapping
     * @return Returns the realpath.
     */
    public abstract String getRealpath();

    /**
     * Returns the full name (mapping/realpath).
     * @return mapping/realpath
     */
    public abstract String getFullRealpath();

    /**
     * @return returns the full class name (Example: railo.web.test_cfm)
     */
    public abstract String getClazz();

    /**
     * @return return the file name of the source file (test.cfm)
     */
    public abstract String getFileName();

    /**
     * @return returns the a classname matching to filename (Example: /railo/web/test_cfm)
     */
    public abstract String getJavaName();

    /**
     * @return returns the a package matching to file (Example: railo.web)
     */
    public abstract String getComponentName();

    /**
     * @return returns mapping where PageSource based on
     */
    public abstract Mapping getMapping();

    /**
     * @return returns if page source exists or not
     */
    public abstract boolean exists();

    /**
     * @return returns if the physical part of the source file exists
     */
    public abstract boolean physcalExists();

    /**
     * @return return the sozrce of the file as String array
     * @throws IOException
     */
    public abstract String[] getSource() throws IOException;

    /**
     * get an new Pagesoure from ralpath
     * @param realPath
     * @return new Pagesource
     */
    public abstract PageSource getRealPage(String realPath);

    /**
     * sets time last accessed page
     * @param lastAccess time ast accessed
     */
    public abstract void setLastAccessTime(long lastAccess);

    /**
     * 
     * @return returns time last accessed page
     */
    public abstract long getLastAccessTime();

    /**
     * set time last accessed (now)
     */
    public abstract void setLastAccessTime();

    /**
     * @return returns how many this page is accessed since server is in use.
     */
    public abstract int getAccessCount();

}