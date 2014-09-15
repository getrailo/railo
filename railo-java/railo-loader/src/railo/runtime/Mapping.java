package railo.runtime;

import railo.commons.io.res.Resource;
import railo.runtime.config.Config;
import railo.runtime.dump.Dumpable;


/**
 * interface of the mapping definition
 */
public interface Mapping  extends Dumpable{

    /**
     * @return returns the archiveClassLoader
     */
    public abstract ClassLoader getClassLoaderForArchive();

    /**
     * @return Returns the physical.
     */
    public abstract Resource getPhysical();

    /**
     * @return Returns the virtual lower case.
     */
    public abstract String getVirtualLowerCase();

    /**
     * @return Returns the virtual lower case with slash at the end.
     */
    public abstract String getVirtualLowerCaseWithSlash();

    /**
     * @return return the archive file
     */
    public abstract Resource getArchive();

    /**
     * @return returns if mapping has a archive
     */
    public abstract boolean hasArchive();

    /**
     * @return return if mapping has a physical path
     */
    public abstract boolean hasPhysical();

    /**
     * @return class root directory
     */
    public abstract Resource getClassRootDirectory();

    /**
     * pagesoucre matching given relpath
     * @param relPath
     * @return matching pagesource
     */
    public abstract PageSource getPageSource(String relPath);
    
    
    /**
     * @param path
     * @param isOut
     * @return matching pagesoucre
     */
    public abstract PageSource getPageSource(String path, boolean isOut);

    /**
     * checks the mapping
     */
    public abstract void check();

    /**
     * @return Returns the hidden.
     */
    public abstract boolean isHidden();

    /**
     * @return Returns the physicalFirst.
     */
    public abstract boolean isPhysicalFirst();

    /**
     * @return Returns the readonly.
     */
    public abstract boolean isReadonly();

    /**
     * @return Returns the strArchive.
     */
    public abstract String getStrArchive();

    /**
     * @return Returns the strPhysical.
     */
    public abstract String getStrPhysical();

    /**
     * @return Returns the trusted.
     */
    public abstract boolean isTrusted(); // FUTURE mark as deprecated; use instead <code>public short getInspectTemplate();</code>
    // FUTURE public short getInspectTemplate();

    public abstract boolean isTopLevel();

    /**
     * @return Returns the virtual.
     */
    public abstract String getVirtual();
    

    /**
     * returns config of the mapping
     * @return config
     */
    public Config getConfig();
}