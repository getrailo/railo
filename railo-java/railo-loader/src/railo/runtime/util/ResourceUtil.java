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
package railo.runtime.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface ResourceUtil {
	
		
		/**
	     * Field <code>FILE_SEPERATOR</code>
	     */
	    public static final char FILE_SEPERATOR=File.separatorChar; 
	    /**
	     * Field <code>FILE_ANTI_SEPERATOR</code>
	     */
	    public static final char FILE_ANTI_SEPERATOR=(FILE_SEPERATOR=='/')?'\\':'/';
	    
	    /**
	     * Field <code>TYPE_DIR</code>
	     */
	    public static final short TYPE_DIR=0;
	    
	    /**
	     * Field <code>TYPE_FILE</code>
	     */
	    public static final short TYPE_FILE=1;

	    /**
	     * Field <code>LEVEL_FILE</code>
	     */
	    public static final short LEVEL_FILE=0;
	    /**
	     * Field <code>LEVEL_PARENT_FILE</code>
	     */
	    public static final short LEVEL_PARENT_FILE=1;
	    /**
	     * Field <code>LEVEL_GRAND_PARENT_FILE</code>
	     */
	    public static final short LEVEL_GRAND_PARENT_FILE=2;
		
	    /**
	     * cast a String (argument destination) to a File Object, 
	     * if destination is not a absolute, file object will be relative to current position (get from PageContext)
	     * file must exist otherwise throw exception
	     * @param pc Page Context to et actuell position in filesystem
	     * @param path relative or absolute path for file object
	     * @return file object from destination
	     * @throws ExpressionException
	     */
	    public Resource toResourceExisting(PageContext pc ,String path) throws PageException;
	    

	    /**
	     * cast a String (argument destination) to a File Object, 
	     * if destination is not a absolute, file object will be relative to current position (get from PageContext)
	     * at least parent must exist
	     * @param pc Page Context to et actuell position in filesystem
	     * @param destination relative or absolute path for file object
	     * @return file object from destination
	     * @throws ExpressionException
	     */
	    public Resource toResourceExistingParent(PageContext pc ,String destination) throws PageException;
	    
	    /**
	     * cast a String (argument destination) to a File Object, 
	     * if destination is not a absolute, file object will be relative to current position (get from PageContext)
	     * existing file is prefered but dont must exist
	     * @param pc Page Context to et actuell position in filesystem
	     * @param destination relative or absolute path for file object
	     * @return file object from destination
	     */
	    public Resource toResourceNotExisting(PageContext pc ,String destination);
			
	    /**
	     * create a file if possible, return file if ok, otherwise return null 
	     * @param res file to touch 
	     * @param level touch also parent and grand parent
	     * @param type is file or directory
	     * @return file if exists, otherwise null
	     */
	    public Resource createResource(Resource res, short level, short type);
	    
		/**
		 * sets a attribute to the resource
		 * @param res
		 * @param attributes
		 * @throws IOException
		 */
		public void setAttribute(Resource res,String attributes) throws IOException;
		
	    /**
	     * return the mime type of a file, does not check the extension of the file, it checks the header
	     * @param res
	     * @param defaultValue 
	     * @return mime type of the file
	     */
	    public String getMimeType(Resource res, String defaultValue);

	    /**
	     * return the mime type of a byte array
	     * @param barr
	     * @param defaultValue 
	     * @return mime type of the file
	     */
	    public String getMimeType(byte[] barr, String defaultValue);
	    
		/**
		 * check if file is a child of given directory
		 * @param file file to search
		 * @param dir directory to search
		 * @return is inside or not
		 */
		public boolean isChildOf(Resource file, Resource dir);
		
		/**
		 * return diffrents of one file to a other if first is child of second otherwise return null
		 * @param file file to search
		 * @param dir directory to search
		 */
		public String getPathToChild(Resource file, Resource dir);
		
	    /**
	     * get the Extension of a file resource
	     * @param res
	     * @return extension of file
	     * @deprecated use instead <code>getExtension(Resource res, String defaultValue);</code>
	     */
	    public String getExtension(Resource res);

	    /**
	     * get the Extension of a file resource
	     * @param res
	     * @return extension of file
	     */
	    public String getExtension(Resource res, String defaultValue);

	    /**
	     * get the Extension of a file
	     * @param strFile
	     * @return extension of file
	     * @deprecated use instead <code>getExtension(String strFile, String defaultValue);</code>
	     */
	    public String getExtension(String strFile);

	    /**
	     * get the Extension of a file resource
	     * @param res
	     * @return extension of file
	     */
	    public String getExtension(String strFile, String defaultValue);
	    

	    /**
	     * copy a file or directory recursive (with his content)
	     * @param file file or directory to delete
	     * @throws IOException 
	     * @throws FileNotFoundException 
	     */
	    public void copyRecursive(Resource src,Resource trg) throws IOException;
	    
	    
	    /**
	     * copy a file or directory recursive (with his content)
	     * @param src
	     * @param trg
	     * @param filter
	     * @throws IOException 
	     * @throws FileNotFoundException 
	     */
	    public void copyRecursive(Resource src,Resource trg,ResourceFilter filter) throws IOException;
	    
	    public void removeChildren(Resource res) throws IOException;

		public void removeChildren(Resource res,ResourceNameFilter filter) throws IOException;
		
		public void removeChildren(Resource res,ResourceFilter filter) throws IOException;
		
		public void moveTo(Resource src, Resource dest) throws IOException;


		/**
		 * return if Resource is empty, means is directory and has no children or a empty file,
		 * if not exist return false.
		 * @param res
		 */
		public boolean isEmpty(Resource res);

		public boolean isEmptyDirectory(Resource res);
		
		public boolean isEmptyFile(Resource res);
		
		public String translatePath(String path, boolean slashAdBegin, boolean slashAddEnd);
		
		public String[] translatePathName(String path);
		
		public String merge(String parent, String child);
		
		public String removeScheme(String scheme, String path);
		
		/**
		 * check if directory creation is ok with the rules for the Resource interface, to not change this rules.
		 * @param resource
		 * @param createParentWhenNotExists
		 * @throws IOException
		 */
		public void checkCreateDirectoryOK(Resource resource, boolean createParentWhenNotExists) throws IOException;


		/**
		 * check if file creating is ok with the rules for the Resource interface, to not change this rules.
		 * @param resource
		 * @param createParentWhenNotExists
		 * @throws IOException
		 */
		public void checkCreateFileOK(Resource resource, boolean createParentWhenNotExists) throws IOException;

		/**
		 * check if copying a file is ok with the rules for the Resource interface, to not change this rules.
		 * @param source
		 * @param target
		 * @throws IOException
		 */
		public void checkCopyToOK(Resource source, Resource target) throws IOException;

		/**
		 * check if moveing a file is ok with the rules for the Resource interface, to not change this rules.
		 * @param source
		 * @param target
		 * @throws IOException
		 */
		public void checkMoveToOK(Resource source, Resource target) throws IOException;

		/**
		 * check if getting a inputstream of the file is ok with the rules for the Resource interface, to not change this rules.
		 * @param resource
		 * @throws IOException
		 */
		public void checkGetInputStreamOK(Resource resource) throws IOException;

		/**
		 * check if getting a outputstream of the file is ok with the rules for the Resource interface, to not change this rules.
		 * @param resource
		 * @throws IOException
		 */
		public void checkGetOutputStreamOK(Resource resource) throws IOException;

		/**
		 * check if removing the file is ok with the rules for the Resource interface, to not change this rules.
		 * @param resource
		 * @throws IOException
		 */
		public void checkRemoveOK(Resource resource) throws IOException;


		public String toString(Resource r, String charset) throws IOException;


		public String contractPath(PageContext pc, String path);

}
