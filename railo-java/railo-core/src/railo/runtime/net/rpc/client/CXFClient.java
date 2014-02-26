package railo.runtime.net.rpc.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;

import railo.print;
import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.loader.util.Util;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.rpc.RPCException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyAsStringIterator;
import railo.runtime.type.it.ObjectsEntryIterator;
import railo.runtime.type.it.ObjectsIterator;
import railo.runtime.type.util.ArrayUtil;


final class CXFClient extends WSClient {
	
	private static final long serialVersionUID = 7329491172855348435L;
	
	private Client client;
	
	private URL wsdlUrl;

	public CXFClient(String strWsdlUrl, String username, String password, ProxyData proxyData) throws PageException {
		DynamicClientFactory dcf=DynamicClientFactory.newInstance();
		print.e(dcf.isSimpleBindingEnabled());
		//JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		try {
			this.wsdlUrl=HTTPUtil.toURL(strWsdlUrl, true);
		}
		catch (MalformedURLException e) {
			throw Caster.toPageException(e);
		}
		client = dcf.createClient(this.wsdlUrl);
		
		if(!Util.isEmpty(username)) {
			if(password==null)password="";
			// TODO set username/password
			
	        
		}
		// TODO proxyData
	}
	

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable box = new DumpTable("webservice","#99cc99","#ccffcc","#000000");
        box.setTitle("Web Service (CXF)");
        box.setComment(wsdlUrl.toExternalForm());
        DumpTable functions = box;
        
        
		
		// TODO Auto-generated method stub
		Collection<BindingOperationInfo> ops = client.getEndpoint().getBinding().getBindingInfo().getOperations();
		Iterator<BindingOperationInfo> it = ops.iterator();
		OperationInfo oi;
		String funcName,doc;
		MessageInfo in,out;
		while(it.hasNext()){
			oi=it.next().getOperationInfo(); 
			funcName=oi.getName().getLocalPart();
			doc=oi.getDocumentation();
			if(oi.isUnwrapped() || (oi.isUnwrappedCapable() && (oi=oi.getUnwrappedOperation())!=null)) {
				in = oi.getInput();
				out = oi.getOutput();

				// attributes/input
				DumpTable table = new DumpTable("#ccccff","#ccff66","#000000");
		    	DumpTable attributes = new DumpTable("#ccccff","#ccff66","#000000");
				DumpTable rtn = new DumpTable("#ccccff","#ccff66","#000000");
		        List<MessagePartInfo> parts = in.getMessageParts();
				Iterator<MessagePartInfo> itt = parts.iterator();
				while(itt.hasNext()){
					MessagePartInfo mpi = itt.next();
					attributes.appendRow(0,new SimpleDumpData(mpi.getName().getLocalPart()),new SimpleDumpData(mpi.getTypeQName().getLocalPart()));
				}
				
				// return value/output
				parts = out.getMessageParts();
				itt = parts.iterator();
				while(itt.hasNext()){
					MessagePartInfo mpi = itt.next();
					rtn.appendRow(0,new SimpleDumpData(mpi.getName().getLocalPart()),new SimpleDumpData(mpi.getTypeQName().getLocalPart()));
				}
				
				table.appendRow(1,new SimpleDumpData("arguments"),attributes);
		        table.appendRow(1,new SimpleDumpData("return type"),rtn);
		        if(!StringUtil.isEmpty(doc))table.appendRow(1,new SimpleDumpData("hint"),new SimpleDumpData(doc));
		        
				
				
				functions.appendRow(1,new SimpleDumpData(funcName),table);
			}
			else functions.appendRow(1,new SimpleDumpData(funcName));
		
		}
		
		//box.appendRow(1,new SimpleDumpData(""),functions);
	    return box;
	}

	@Override
	public Iterator<String> keysAsStringIterator() {
		return new KeyAsStringIterator(keyIterator());
	}
	
	@Override
	public Iterator<Key> keyIterator() {
		Collection<BindingOperationInfo> ops = client.getEndpoint().getBinding().getBindingInfo().getOperations();
		Iterator<BindingOperationInfo> it = ops.iterator();
		List<Key> keys=new ArrayList<Key>();
		while(it.hasNext()){
			keys.add(KeyImpl.init(it.next().getOperationInfo().getName().getLocalPart()));
		}
		return keys.iterator();
	}

	@Override
	public Iterator<Object> valueIterator() {
		return new ObjectsIterator(keyIterator(),this);
	}

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new ObjectsEntryIterator(keyIterator(), this);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
        return call(pc,KeyImpl.init("get"+key.getString()), ArrayUtil.OBJECT_EMPTY);
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		try {
            return call(pc,KeyImpl.init("get"+StringUtil.ucFirst(key.getString())), ArrayUtil.OBJECT_EMPTY);
        } catch (PageException e) {
            return defaultValue;
        }
	}
	
	@Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
        return call(pc,KeyImpl.init("set"+propertyName.getString()), new Object[]{value});
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		try {
            return call(pc,KeyImpl.init("set"+propertyName.getString()), new Object[]{value});
        } catch (PageException e) {
            return null;
        }
	}

	@Override
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		Collection<BindingOperationInfo> ops = client.getEndpoint().getBinding().getBindingInfo().getOperations();
		Iterator<BindingOperationInfo> it = ops.iterator();
		String name=null;
		BindingOperationInfo boi;
		OperationInfo oi;
		while(it.hasNext()){
			boi = it.next();
			oi = boi.getOperationInfo();
			name=oi.getName().getLocalPart();
			if(name.equalsIgnoreCase(methodName.getString())) {
				break;
			}
			name=null;
		}
		if(name == null)
			throw new RPCException("Cannot locate operation " + methodName + " in webservice " + wsdlUrl);
		
		try {
			return client.invoke(name,new Object[]{});// TOD set arguments
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws ApplicationException {
		// TODO impl
		throw new ApplicationException("not implemented yet");
	} 
	@Override
	public Object callWithNamedValues(Config config, Key methodName, Struct arguments) throws PageException {
		// TODO Auto-generated method stub
		throw new ApplicationException("not implemented yet");
	}
	
	

	@Override
    public String castToString() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a string");
    }

	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a boolean");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a number");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

    @Override
    public DateTime castToDateTime() throws RPCException {
        throw new RPCException("can't cast Webservice to a Date Object");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Webservice Object with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a String");
	}


	@Override
	public void addHeader(SOAPHeaderElement header) throws ApplicationException {
		throw new ApplicationException("not supported with CXF Client, use instead the Axis Client");
	}


	@Override
	public Call getLastCall() throws ApplicationException {
		throw new ApplicationException("not supported with CXF Client, use instead the Axis Client");
	}
}