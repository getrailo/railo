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
package com.allaire.cfx;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;

/**
 * Implementation of the Debug Request
 */
public class DebugRequest implements Request {

    private Struct attributes;
    private Query query;
    private Struct settings;

    /**
     * constructor of the class
     * @param attributes
     */
    public DebugRequest(Hashtable attributes) {
        this(attributes,null,null);
    }


    /**
     * constructor of the class
     * @param attributes
     * @param query
     */
    public DebugRequest(Hashtable attributes, Query query) {
        this(attributes,query,null);
    }
    
    /**
     * constructor of the class
     * @param attributes
     * @param query
     * @param settings
     */
    public DebugRequest(Hashtable attributes, Query query, Hashtable settings) {
        this.attributes=toStruct(attributes);
        this.query=query;
        this.settings=toStruct(settings);
        
    }
    
    /**
     * @see com.allaire.cfx.Request#attributeExists(java.lang.String)
     */
    public boolean attributeExists(String key) {
        return attributes.containsKey(key);
    }

    /**
     * @see com.allaire.cfx.Request#debug()
     */
    public boolean debug() {
        Object o=attributes.get("debug",Boolean.FALSE);
        return CFMLEngineFactory.getInstance().getCastUtil().toBooleanValue(o,false);
    }

    /**
     * @see com.allaire.cfx.Request#getAttribute(java.lang.String, java.lang.String)
     */
    public String getAttribute(String key, String defaultValue) {
        return CFMLEngineFactory.getInstance().getCastUtil().toString(attributes.get(key,defaultValue),defaultValue);
    }

    /**
     * @see com.allaire.cfx.Request#getAttribute(java.lang.String)
     */
    public String getAttribute(String key) {
        return getAttribute(key, "");
    }

    /**
     * @see com.allaire.cfx.Request#getAttributeList()
     */
    public String[] getAttributeList() {
    	Iterator<Key> it = attributes.keyIterator();
    	List<String> arr=new ArrayList<String>();
    	while(it.hasNext()){
			arr.add(it.next().getString());
		}
        return arr.toArray(new String[arr.size()]);
    }

    /**
     * @see com.allaire.cfx.Request#getIntAttribute(java.lang.String, int)
     */
    public int getIntAttribute(String key, int defaultValue) {
        Object o=attributes.get(key,null);
        if(o==null) return defaultValue;
        return (int)CFMLEngineFactory.getInstance().getCastUtil().toDoubleValue(o,defaultValue);
    }

    /**
     * @see com.allaire.cfx.Request#getIntAttribute(java.lang.String)
     */
    public int getIntAttribute(String key) throws NumberFormatException {
        return getIntAttribute(key, -1);
    }

    /**
     * @see com.allaire.cfx.Request#getQuery()
     */
    public Query getQuery() {
        return query;
    }

    /**
     * @see com.allaire.cfx.Request#getSetting(java.lang.String)
     */
    public String getSetting(String key) {
        return settings==null?"":CFMLEngineFactory.getInstance().getCastUtil().toString(settings.get(key,""),"");
    }
    
    /**
     * @param hashTable a Hashtable to a Struct
     * @return casted struct
     */
    private static Struct toStruct(Hashtable hashTable) {
        if(hashTable==null) return null;
        
        Enumeration e = hashTable.keys();
        Struct sct=CFMLEngineFactory.getInstance().getCreationUtil().createStruct();
        while(e.hasMoreElements()) {
            Object key=e.nextElement();
            sct.setEL(key.toString(),hashTable.get(key));
        }
        return sct;
    }
}
