package railo.runtime.converter;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import railo.aprint;
import railo.commons.lang.ParserString;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;

public class ClientScopeConverter {

	public static Struct unserialize(String str) {
		Struct sct=new StructImpl();
		ParserString ps=new ParserString(str);
		
		StringBuffer sb=new StringBuffer();
		String key=null;
		while(!ps.isAfterLast()) {
			if(ps.isCurrent('#')) {
				if(ps.isNext('=')){
					ps.next();
					sb.append('=');
				}
				else if(ps.isNext('#')){
					ps.next();
					sb.append('#');
				}
				else {
					sct.setEL(key, sb.toString());
					sb=new StringBuffer();
				}
			}
			else if(ps.isCurrent('=')) {
				key=sb.toString();
				sb=new StringBuffer();
			}
			else sb.append(ps.getCurrent());
			ps.next();
		}
		
		
		if(!StringUtil.isEmpty(key) && !StringUtil.isEmpty(sb)) {
			sct.setEL(key, sb.toString());
		}
		return sct;
		
		/*
		int index=0,last=0;
		while((index=str.indexOf('#',last))!=-1) {
			outer:while(str.length()+1>index) {
				c=str.charAt(index+1);
				if(c=='#' || c=='=') {
					last=index+1;
					continue;
				}
			}
			_unserialize(str.substring(last,index));
			last=index+1;
		}
		_unserialize(str.substring(last));
		*/
		
	}



	public static String serialize(Struct sct) throws ConverterException {
		// TODO Auto-generated method stub
		return serialize(sct,null);
	}
	
	public static String serialize(Struct sct, Set ignoreSet) throws ConverterException {
		StringBuffer sb=new StringBuffer();
		Iterator it=sct.keyIterator();
        boolean doIt=false;
        Object oKey;
        while(it.hasNext()) {
        	oKey=it.next();
        	if(ignoreSet!=null && ignoreSet.contains(oKey)) continue;
            String key=Caster.toString(oKey,"");
            if(doIt)sb.append('#');
            doIt=true;
            sb.append(escape(key));
            sb.append('=');
            sb.append(_serialize(sct.get(key,"")));
        }
        return sb.toString();
	}

	private static String escape(String str) {
		int len=str.length();
		StringBuffer sb=new StringBuffer();
		char c;
		for(int i=0;i<len;i++) {
			c=str.charAt(i);
			if(c=='=') 		sb.append("#=");
			else if(c=='#')	sb.append("##");
			else 			sb.append(c);
		}
		return sb.toString();
	}

	private static String _serialize(Object object) throws ConverterException {
		
		if(object==null) return "";
		
		// String
		else if(object instanceof String) return escape(object.toString());
		
		// Number
		else if(object instanceof Number) return Caster.toString(((Number)object));
		
		// Boolean
		else if(object instanceof Boolean) return Caster.toString(((Boolean)object).booleanValue());
		
		// DateTime
		else if(object instanceof DateTime) return Caster.toString(object,null);
		
		// Date
		else if(object instanceof Date) return Caster.toString(object,null);
		
		throw new ConverterException("can't convert complex value "+Caster.toTypeName(object)+" to a simple value");
	}

	public static void main(String[] args) throws ConverterException {
		Struct sct=new StructImpl();
		sct.setEL("a", "b");
		sct.setEL("pe#=ter", "ab##c");
		sct.setEL("susi", Boolean.TRUE);
		sct.setEL("peter", "abc");
		sct.setEL("x", "");
		
		/*sct.setEL("abc=def", "abc");
		sct.setEL("abc#def", "ab#=c");
		*/
		String str;
		aprint.out(sct);
		aprint.out(str=ClientScopeConverter.serialize(sct));
		aprint.out(ClientScopeConverter.unserialize(str));
		//aprint.out(new ScriptConverter().serialize(sct));
		
		
		
	}

}
