package railo.runtime.functions.system;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;

public class GetPrinterList extends BIF {

	private static final long serialVersionUID = -3863471828670823815L;


	public static String call(PageContext pc,String delimiter) { 
		if(delimiter==null) delimiter=",";
		StringBuilder sb=new StringBuilder();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (int i=0;i<services.length;i++) {
        	if(i>0)sb.append(delimiter);
        	sb.append(services[i].getName());
        }
        return sb.toString();
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,",");
	}
}
