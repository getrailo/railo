/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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