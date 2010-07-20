package railo.runtime.spooler.remote;

import railo.runtime.config.RemoteClient;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTaskWS;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class RemoteClientTask extends SpoolerTaskWS {
	
	public static final Collection.Key ACTION = KeyImpl.getInstance("action");
	public static final Collection.Key TYPE = KeyImpl.getInstance("type");
	public static final Collection.Key PASSWORD = KeyImpl.getInstance("password");
	public static final Collection.Key ATTRIBUTE_COLLECTION = KeyImpl.getInstance("attributeCollection");
	public static final Collection.Key CALLER_ID = KeyImpl.getInstance("callerId");
	private StructImpl args;
	private String action;
	private String type;
    
	public RemoteClientTask(ExecutionPlan[] plans,RemoteClient client, Struct attrColl,String callerId, String type) {
		super(plans,client);
		this.type=type;
		action=(String) attrColl.get(ACTION,null);
		args = new StructImpl();
		args.setEL(TYPE, client.getType());
		args.setEL(PASSWORD, client.getAdminPasswordEncrypted());
		args.setEL(ATTRIBUTE_COLLECTION, attrColl);
		args.setEL(CALLER_ID, callerId);
	}

	/**
	 * @see railo.runtime.spooler.SpoolerTask#getType()
	 */
	public String getType() {
		return type;
	}

	/**
	 * @see railo.runtime.spooler.SpoolerTask#subject()
	 */
	public String subject() {
		return action+" ("+super.subject()+")";
	}
	/**
	 * @see railo.runtime.spooler.SpoolerTask#detail()
	 */
	public Struct detail() {
		Struct sct=super.detail();
		sct.setEL("action", action);
		return sct;
	}
	

	/**
	 * @see railo.runtime.spooler.SpoolerTaskWS#getMethodName()
	 */
	protected String getMethodName() {
		return "invoke";
	}
	
	/**
	 * @see railo.runtime.spooler.SpoolerTaskWS#getArguments()
	 */
	protected Struct getArguments() {
		return args;
	}
}
