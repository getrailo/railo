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
package railo.runtime.cache.legacy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.Md5;
import railo.runtime.PageContext;
import railo.runtime.type.dt.TimeSpan;

 
class CacheItemFS extends CacheItem {
	
	private final Resource res,directory;
	private String name;
	
	public CacheItemFS(PageContext pc, HttpServletRequest req, String id, String key, boolean useId, Resource dir) throws IOException {
		super(pc, req, id, key, useId);
		// directory
		directory= dir!=null?dir:getDirectory(pc);
        
		// name
        name=Md5.getDigestAsString(fileName)+".cache";
        
        // res
        res=directory.getRealResource(name);
		
	}
	
	private static Resource getDirectory(PageContext pc) throws IOException{
		Resource dir= pc.getConfig().getCacheDir();
		if(!dir.exists())dir.createDirectory(true);
		return dir;
	}
	
	
	
	public boolean isValid() {
		return res!=null;
	}
	
	public boolean isValid(TimeSpan timespan) {
		return res!=null && res.exists() && (res.lastModified()+timespan.getMillis()>=System.currentTimeMillis());
	}
	
	public void writeTo(OutputStream os, String charset) throws IOException {
		IOUtil.copy(res.getInputStream(),os,true,false);
	}
	public String getValue() throws IOException {
		return IOUtil.toString(res,"UTF-8");
	}
	public void store(String result) throws IOException {
		IOUtil.write(res, result,"UTF-8", false); 
		MetaData.getInstance(directory).add(name, fileName);
	}
	
	public void store(byte[] barr,boolean append) throws IOException {
    	IOUtil.copy(new ByteArrayInputStream(barr), res.getOutputStream(append),true,true);
    	MetaData.getInstance(directory).add(name, fileName);
	}

	protected static void _flushAll(PageContext pc, Resource dir) throws IOException {
		if(dir==null)dir=getDirectory(pc);
		ResourceUtil.removeChildrenEL(dir);
	}

	protected static void _flush(PageContext pc, Resource dir, String expireurl) throws IOException, MalformedPatternException {
		if(dir==null)dir=getDirectory(pc);
		List<String> names;
		names = MetaData.getInstance(dir).get(expireurl);
		Iterator<String> it = names.iterator();
		String name;
		while(it.hasNext()){
			name=it.next();
			if(dir.getRealResource(name).delete());
				
		}
	}
}