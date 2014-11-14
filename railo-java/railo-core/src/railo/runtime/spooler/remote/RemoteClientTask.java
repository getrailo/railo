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
package railo.runtime.spooler.remote;

import railo.runtime.config.RemoteClient;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTaskWS;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public class RemoteClientTask extends SpoolerTaskWS {
	
	public static final Collection.Key PASSWORD = KeyImpl.intern("password");
	public static final Collection.Key ATTRIBUTE_COLLECTION = KeyImpl.intern("attributeCollection");
	public static final Collection.Key CALLER_ID = KeyImpl.intern("callerId");
	private StructImpl args;
	private String action;
	private String type;
    
	public RemoteClientTask(ExecutionPlan[] plans,RemoteClient client, Struct attrColl,String callerId, String type) {
		super(plans,client);
		this.type=type;
		action=(String) attrColl.get(KeyConstants._action,null);
		args = new StructImpl();
		args.setEL(KeyConstants._type, client.getType());
		args.setEL(PASSWORD, client.getAdminPasswordEncrypted());
		args.setEL(ATTRIBUTE_COLLECTION, attrColl);
		args.setEL(CALLER_ID, callerId);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String subject() {
		return action+" ("+super.subject()+")";
	}
	@Override
	public Struct detail() {
		Struct sct=super.detail();
		sct.setEL("action", action);
		return sct;
	}
	

	@Override
	protected String getMethodName() {
		return "invoke";
	}
	
	@Override
	protected Struct getArguments() {
		return args;
	}
}
