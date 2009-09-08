package railo.commons.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.lang.StringUtil;

public class Command {
    
    public static String execute(String cmdline) throws IOException, InterruptedException {
        
    	
    	
    	return _execute(Runtime.getRuntime().exec(toArray(cmdline)));
    }

	public static String execute(String[] cmdline) throws IOException, InterruptedException {
        return _execute(Runtime.getRuntime().exec(cmdline));
    }
    public static String execute(String cmd,String[] args) throws IOException, InterruptedException {
    	return execute(StringUtil.merge(cmd,args));
    }

    private static String _execute(Process p) throws IOException, InterruptedException {
    	InputStream is=null;
    	InputStream es=null;
    	try {
    		
    		String err = IOUtil.toString(es=p.getErrorStream(),SystemUtil.getCharset());
    		String in = IOUtil.toString(is=p.getInputStream(),SystemUtil.getCharset());
	        if(p.waitFor()!=0 && !StringUtil.isEmpty(err))throw new CommandException(err);
        	return in;
    	}
    	finally {
    		IOUtil.closeEL(is);
    		IOUtil.closeEL(es);
    	}
    }
    
    


    private static String[] toArray(String str) {
    	
		if(StringUtil.isEmpty(str)) return new String[]{""};
    	str=str.trim();
    	StringBuffer sb=new StringBuffer();
    	ArrayList list=new ArrayList();
		char[] carr = str.toCharArray();
		char c,last=0;
		char inside=0;
		for(int i=0;i<carr.length;i++){
			c=carr[i];
			if(i>0)last=carr[i-1];
			switch(c){
		// DELIMETER
			case '\\':	
				if(i+1<carr.length){
					sb.append(carr[++i]);
				}
				else sb.append(c);
				break;
		// QUOTE
			case '\'':
			case '"':
				if(inside==0){
					if(str.lastIndexOf(c)>i)
						inside=c;
					else
						sb.append(c);
				}
				else if(inside==c) {
					inside=0;
				}
				else sb.append(c);
			break;
		// WHITE SPACE
			case ' ':
			case '\b':
			case '\t':
			case '\n':
			case '\r':
			case '\f':
				//if(last=='\\')sb.setCharAt(sb.length()-1,c);
				if(inside==0) {
					populateList(sb,list);
				}
				else sb.append(c);
				break;
		// OTHERS
			default:
					sb.append(c);
			}
		}
		populateList(sb, list);
		
		
    	return (String[]) list.toArray(new String[list.size()]);
	}
    
    private static void populateList(StringBuffer sb, ArrayList list) {

		String tmp = sb.toString();
		tmp=tmp.trim();
		if(tmp.length()>0)list.add(tmp);
		sb.delete(0, sb.length());
	}

}