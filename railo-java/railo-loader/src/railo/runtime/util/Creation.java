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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;

import railo.commons.io.res.Resource;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.config.RemoteClient;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.exp.PageException;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTask;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.Date;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.Time;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.ClusterEntry;

/**
 * Creation of different Objects
 */
public interface Creation {

    /**
     * creates and returns a array instance
     * @return array
     */
    public abstract Array createArray();
    

    /**
     * creates and returns a array based on a string list
     * @return array
     */
    public abstract Array createArray(String list, String delimiter,boolean removeEmptyItem,boolean trim);

    /**
     * creates and returns a DateTime instance
     * @param time 
     * @return DateTime
     */
    public abstract DateTime createDateTime(long time);
    
    /**
     * creates and returns a DateTime instance
     * @param year 
     * @param month 
     * @param day 
     * @param hour 
     * @param minute 
     * @param seond 
     * @param millis 
     * @return DateTime
     */
    public abstract DateTime createDateTime(int year,int month,int day,int hour,int minute,int seond,int millis) throws PageException;

    /**
     * creates and returns a Date instance
     * @param time 
     * @return DateTime
     */
    public abstract Date createDate(long time);
    
    /**
     * creates and returns a Date instance
     * @param year 
     * @param month 
     * @param day 
     * @return DateTime
     */
    public abstract Date createDate(int year,int month,int day) throws PageException;

    /**
     * creates and returns a Time instance
     * @param time 
     * @return DateTime
     */
    public abstract Time createTime(long time);

    /**
     * creates and returns a Time instance
     * @param hour 
     * @param minute 
     * @param second 
     * @param millis 
     * @return DateTime
     */
    public abstract Time createTime(int hour,int minute,int second,int millis);
    
    /**
     * creates and returns a TimeSpan instance
     * @param day 
     * @param hour 
     * @param minute 
     * @param second 
     * @return TimeSpan
     */
    public abstract TimeSpan createTimeSpan(int day,int hour,int minute,int second);
    
    /**
     * creates and returns a array instance
     * @param dimension 
     * @return array
     * @throws PageException 
     */
    public abstract Array createArray(int dimension) throws PageException;
    
    /**
     * creates and returns a struct instance
     * @return struct
     */
    public abstract Struct createStruct();

    public abstract Struct createStruct(int type);
    
    // FUTURE public abstract Struct createStruct(String type);

    /**
     * creates a query object with given data
     * @param columns
     * @param rows
     * @param name
     * @return created query Object
     * @deprecated usse instead <code>createQuery(Collection.Key[] columns, int rows, String name)</code>
     */
    public abstract Query createQuery(String[] columns, int rows, String name);
    
    /**
     * creates a query object with given data
     * @param columns
     * @param rows
     * @param name
     * @return created query Object
     */
    public abstract Query createQuery(Collection.Key[] columns, int rows, String name) throws PageException;

    /**
	 * creates a query object with a resultset from a sql query
	 * @param dc Connection to a database
	 * @param name 
	 * @param sql sql to execute
	 * @param maxrow maxrow for the resultset
     * @throws PageException
     * @deprecated replaced with <code>{@link #createQuery(DatasourceConnection, SQL, int, int, int, String)}</code>
	 */	
    public abstract Query createQuery(DatasourceConnection dc,SQL sql,int maxrow, String name) throws PageException;

    /**
     * @param dc Connection to a database
	 * @param sql sql to execute
	 * @param maxrow maxrow for the resultset
     * @param fetchsize
     * @param timeout
     * @param name
     * @return created Query
     * @throws PageException
     */
    public abstract Query createQuery(DatasourceConnection dc,SQL sql,int maxrow, int fetchsize, int timeout,String name) throws PageException;


    /**
     * creates and returns a xml Document instance
     * @return struct
     * @throws PageException 
     */
    public abstract Document createDocument() throws PageException;
    
    /**
     * creates and returns a xml Document instance
     * @param file 
     * @param isHtml 
     * @return struct
     * @throws PageException 
     */
    public abstract Document createDocument(Resource file, boolean isHtml) throws PageException;
    
    /**
     * creates and returns a xml Document instance
     * @param xml 
     * @param isHtml 
     * @return struct
     * @throws PageException 
     */
    public abstract Document createDocument(String xml, boolean isHtml) throws PageException;
    
    /**
     * creates and returns a xml Document instance
     * @param is 
     * @param isHtml 
     * @return struct
     * @throws PageException 
     */
    public abstract Document createDocument(InputStream is, boolean isHtml) throws PageException;

	/**
	 * creates a collecton Key out of a String
	 * @param key
	 */
	public abstract Collection.Key createKey(String key);
	
	public SpoolerTask createRemoteClientTask(ExecutionPlan[] plans,RemoteClient remoteClient,Struct attrColl,String callerId, String type);

	public ClusterEntry createClusterEntry(Key key,Serializable value, int offset);


	public Resource createResource(String path, boolean existing) throws PageException;


	public abstract HttpServletRequest createHttpServletRequest(File contextRoot,String serverName, String scriptName,String queryString, 
			Cookie[] cookies, Map<String,Object> headers, Map<String, String> parameters, Map<String,Object> attributes, HttpSession session);
	
	public abstract HttpServletResponse createHttpServletResponse(OutputStream io);


	public abstract PageContext createPageContext(HttpServletRequest req, HttpServletResponse rsp, OutputStream out);

	/**
	 * creates a component object from (Full)Name, for example railo.extensions.net.HTTPUtil
	 * @param pc Pagecontext for loading the CFC
	 * @param fullname fullanem of the cfc example:railo.extensions.net.HTTPUtil
	 * @return loaded cfc
	 * @throws PageException 
	 */
	public abstract Component createComponentFromName(PageContext pc, String fullName) throws PageException;
	
	/**
	 * creates a component object from a absolute local path, for example /Users/susi/Projects/Sorglos/wwwrooot/railo/extensions/net/HTTPUtil.cfc
	 * @param pc Pagecontext for loading the CFC
	 * @param path path of the cfc example:/Users/susi/Projects/Sorglos/wwwrooot/railo/extensions/net/HTTPUtil.cfc
	 * @return loaded cfc
	 * @throws PageException 
	 */
	public abstract Component createComponentFromPath(PageContext pc, String path) throws PageException; 
	

		

}
