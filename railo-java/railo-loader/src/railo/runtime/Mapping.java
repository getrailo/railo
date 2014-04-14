package railo.runtime;

import java.io.InputStream;

import railo.commons.io.res.Resource;
import railo.runtime.config.Config;
import railo.runtime.dump.Dumpable;


/**
 * interface of the mapping definition
 */
public interface Mapping  extends Dumpable{
	
	public Class<?> getArchiveClass(String className) throws ClassNotFoundException;
	public Class<?> getArchiveClass(String className, Class<?> defaultValue);

	public InputStream getArchiveResourceAsStream(String string);
	
	public Class<?> getPhysicalClass(String className) throws ClassNotFoundException;
	public Class<?> getPhysicalClass(String className, byte[] code);
			

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
     * pagesoucre matching given realpath
     * @param realPath
     * @return matching pagesource
     */
    public abstract PageSource getPageSource(String realPath);
    
    
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
     * @deprecated use instead <code>public short getInspectTemplate();</code>
     */
    public abstract boolean isTrusted();
    
    public short getInspectTemplate();

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