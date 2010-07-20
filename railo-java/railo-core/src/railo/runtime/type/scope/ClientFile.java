package railo.runtime.type.scope;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * client scope that store it's data in a resource
 */
public final class ClientFile extends ClientSupport {

	private static ScriptConverter serializer=new ScriptConverter();
	private static CFMLExpressionInterpreter evaluator=new CFMLExpressionInterpreter();
	
	private Resource res;
	

	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 */
	private ClientFile(PageContext pc,Resource res) {
		super(
				new StructImpl(),
				new DateTimeImpl(pc.getConfig()),
				null,
				-1,1);
		//String folder=(StringUtil.isEmpty(name))?"__empty__":name;
		//res = pc.getConfig().getClientScopeDir().getRealResource(folder+"/"+pc.getCFID()+".script");
		this.res=res;
	}
	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 */
	private ClientFile(PageContext pc,Resource res,Struct sct) {
		super(
				sct,
				doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1,
				Caster.toIntValue(sct.get(HITCOUNT,"1"),1));
		
		this.res =res;// pc.getConfig().getClientScopeDir().getRealResource(name+"-"+pc.getCFID()+".script");
		
	}
	
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientFile(ClientFile other,boolean deepCopy) {
		super(other,deepCopy);
		this.res=other.res;
	}
	
	
	/**
	 * load new instance of the class
	 * @param name
	 * @param pc
	 * @return
	 */
	public static Client getInstance(String name, PageContext pc) {

		
		Resource res = pc.getConfig().getClientScopeDir().getRealResource(getFolderName(name,pc.getCFID(),true));
		if(res.exists()) {
			try {
				return new ClientFile(pc,res,(Struct) evaluator.interpret(pc,IOUtil.toString(res,"UTF-8")));
				//return new ClientImpl(pc,name,(Struct) serializer.deserialize(IOUtil.toString(res,"UTF-8"), true));
			} 
			catch (Exception e) {
				
			}
		}
		return new ClientFile(pc,res);
	}
	
	/**
	 * return a folder name that match given input
	 * @param name
	 * @param cfid
	 * @param addExtension
	 * @return
	 */
	public static String getFolderName(String name, String cfid,boolean addExtension) {
		if(addExtension) return getFolderName(name, cfid, false)+".script";
		if(!StringUtil.isEmpty(name))
			name=StringUtil.toVariableName(StringUtil.toLowerCase(name));
		else 
			name="__empty__";
		return name+"/"+cfid.substring(0,2)+"/"+cfid.substring(2);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.ClientSupport#store()
	 */
	public void store() {
		if(!super.hasContent()) return;
		try {
			if(!res.exists())ResourceUtil.createFileEL(res, true);
			IOUtil.write(res, serializer.serializeStruct(sct, ignoreSet), "UTF-8", false);
		} 
		catch (Throwable t) {}
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = super.toDumpTable(pageContext, maxlevel,dp);
		table.setTitle("Scope Client (File)");
		return table;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
    	return new ClientFile(this,deepCopy);
	}
	

}
