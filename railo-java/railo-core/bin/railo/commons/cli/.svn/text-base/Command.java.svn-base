package railo.commons.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.lang.StringUtil;

public class Command {
    
    public static Process createProcess(String cmdline,boolean translate) throws IOException {
    	if(!translate)return Runtime.getRuntime().exec(cmdline);
    	return Runtime.getRuntime().exec(toArray(cmdline));
    }
	
    /**
     * @param cmdline command line
     * @param translate translate the command line or not
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String execute(String cmdline,boolean translate) throws IOException, InterruptedException {
    	if(!translate)return execute(Runtime.getRuntime().exec(cmdline));
    	return execute(Runtime.getRuntime().exec(toArray(cmdline)));
    }
    
	public static String execute(String[] cmdline) throws IOException, InterruptedException {
		return execute(Runtime.getRuntime().exec(cmdline));
    }
    public static String execute(String cmd,String[] args) throws IOException, InterruptedException {
    	return execute(StringUtil.merge(cmd,args));
    }

    public static String execute(Process p) throws IOException, InterruptedException {
    	InputStream is=null;
    	InputStream es=null;
    	IOException ioe;
	    try {
	    	StreamGobbler in=new StreamGobbler(is=p.getInputStream());
	    	StreamGobbler err=new StreamGobbler(es=p.getErrorStream());
	    	in.start();
	    	err.start();
    		if(p.waitFor()!=0){
    			err.join();
    			if((ioe=err.getException())!=null) throw ioe;
    			String str=err.getString();
    			if(!StringUtil.isEmpty(str))
    				throw new CommandException(str);
	        }
    		in.join();
    		if((ioe=in.getException())!=null) throw ioe;
			return in.getString();
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
			/*case '\\':	
				if(i+1<carr.length){
					sb.append(carr[++i]);
				}
				else sb.append(c);
				break;*/
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

class StreamGobbler extends Thread {
  

	InputStream is;
	private String str;
	private IOException ioe;
    
    StreamGobbler(InputStream is)	{
        this.is = is;
    }
    
    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
			str=IOUtil.toString(is,SystemUtil.getCharset());
		} catch (IOException ioe) {
			this.ioe=ioe;
		}  
    }
    
    /**
	 * @return the str
	 */
	public String getString() {
		return str;
	}

	/**
	 * @return the ioe
	 */
	public IOException getException() {
		return ioe;
	}
	
}
