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
package railo.runtime.tag;

import railo.runtime.cfx.CFXTagException;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.cfx.RequestImpl;
import railo.runtime.cfx.ResponseImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.AppendixTag;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

import com.allaire.cfx.CustomTag;
import com.allaire.cfx.Request;
import com.allaire.cfx.Response;

/**
* Creates a CFML CFX Tag
*
*
*
**/
public final class CFXTag extends TagImpl implements DynamicAttributes,AppendixTag {

	private Struct attributes=new StructImpl();
	private String appendix;
	
	@Override
	public void release() {
		attributes.clear();
		appendix=null;
	}

	@Override
	public void setAppendix(String appendix) {
    	//print.out(appendix);
        this.appendix = appendix;
    }
	
    @Override
	public void setDynamicAttribute(String domain, String key, Object value) {
		setDynamicAttribute(domain, KeyImpl.init(key), value);
	}
	
	@Override
	public void setDynamicAttribute(String domain, Collection.Key key, Object value) {
		attributes.setEL(key,value);
	}

	@Override
	public int doStartTag() throws PageException {
	    // RR SerialNumber sn = pageContext.getConfig().getSerialNumber();
	    // if(sn.getVersion()==SerialNumber.VERSION_COMMUNITY)
	    //     throw new SecurityException("no access to this functionality with the "+sn.getStringVersion()+" version of railo");
	    
		
		CFXTagPool pool=pageContext.getConfig().getCFXTagPool();
		CustomTag ct;
		try {
			ct = pool.getCustomTag(appendix);
		} 
        catch (CFXTagException e) {
			throw Caster.toPageException(e);
		}
		Request req=new RequestImpl(pageContext,attributes);
		Response rsp=new ResponseImpl(pageContext,req.debug());
		try {
			ct.processRequest(req,rsp);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
		pool.releaseCustomTag(ct);
		
		return SKIP_BODY;
	}

    public String getAppendix() {
        return appendix;
    }
}