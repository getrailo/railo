package railo.runtime.component;

import java.util.HashMap;
import java.util.Map;

import railo.runtime.InterfaceImpl;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;
import railo.runtime.type.util.ComponentUtil;

public class InterfaceCollection {

	private InterfaceImpl[] interfaces;
	private Map udfs= new HashMap();
	private long lastUpdate=0;


	public InterfaceCollection(PageContextImpl pc, PageSource child,String implement) throws PageException {
		interfaces = InterfaceImpl.loadImplements(pc,child, implement,udfs);
	}

	/**
	 * @return the interfaces
	 */
	public InterfaceImpl[] getInterfaces() {
		return interfaces;
	}

	/**
	 * @return the udfs
	 */
	public Map getUdfs() {
		return udfs;
	}
	
	public long lastUpdate() {
		if(lastUpdate==0){
			long temp;
			for(int i=0;i<interfaces.length;i++){
				temp=ComponentUtil.getCompileTime(null,interfaces[i].getPageSource(),0);
				if(temp>lastUpdate)
					lastUpdate=temp;
			}
		}
		return lastUpdate;
	}

}
