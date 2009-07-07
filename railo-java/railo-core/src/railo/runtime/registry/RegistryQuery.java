package railo.runtime.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import railo.commons.io.IOUtil;
import railo.runtime.type.List;


/**
 * 
 */
public final class RegistryQuery {

  private static final char DQ='"'; 
  private static final int lenDWORD=RegistryEntry.REGDWORD_TOKEN.length();
  private static final int lenSTRING=RegistryEntry.REGSTR_TOKEN.length();
  private static final String NO_NAME="<NO NAME>";
  
	  /**
	   * execute a String query on command line
	 * @param query String to execute
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String executeQuery(String query) throws IOException, InterruptedException {
		StreamReader reader=null;
		try {
		    Process process = Runtime.getRuntime().exec(query);
		    reader = new StreamReader(process.getInputStream());
		
		    reader.start();
		    process.waitFor();
		    reader.join();
		
		    return reader.getResult();
		}
		finally {
			IOUtil.closeEL(reader);
		}
		
	}  
  
	  /**
	   * gets a single value form the registry
	 * @param branch brach to get value from
	 * @param entry entry to get
	 * @param type type of the registry entry to get
	 * @return registry entry or null of not exist
	 * @throws RegistryException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static RegistryEntry getValue(String branch, String entry, short type) throws RegistryException, IOException, InterruptedException {
	    RegistryEntry[] rst = filter(executeQuery("reg query "+DQ+branch+DQ+" /v "+entry),branch,type);
	    if(rst.length==1) {
	        return rst[0];
	        //if(type==RegistryEntry.TYPE_ANY || type==r.getType()) return r;
	    }
	    return null;
	}

	/**
	 * gets all entries of one branch
	 * @param branch
	 * @param type
	 * @return
	 * @throws RegistryException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static RegistryEntry[] getValues(String branch, short type) throws RegistryException, IOException, InterruptedException {
	    return filter(executeQuery("reg query "+DQ+branch+DQ),branch,type);
	}

	/**
	 * writes a value to registry
	 * @param branch
	 * @param entry
	 * @param type
	 * @param value
	 * @throws RegistryException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void setValue(String branch, String entry, short type, String value) throws RegistryException, IOException, InterruptedException {
	    //String fullKey=List.trim(branch,"\\")+"\\"+List.trim(entry,"\\")+"\\";
	    //String query="reg add "+DQ+fullKey+DQ+" /v "+value+" /t "+RegistryEntry.toStringType(type)+" /f";
	    //executeQuery(query);
	    
	    if(type==RegistryEntry.TYPE_KEY) {
	        String fullKey=List.trim(branch,"\\")+"\\"+List.trim(entry,"\\");
	        executeQuery("reg add \""+fullKey+"\" /ve /d \""+value+"\" /f");
	    }
	    else {
	        executeQuery("reg add \""+List.trim(branch,"\\")+"\" /v "+entry+" /t "+RegistryEntry.toStringType(type)+" /d \""+value+"\" /f");
	    }
	}
	
	/**
	 * deletes a value or a key
	 * @param branch
	 * @param entry
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void deleteValue(String branch, String entry) throws IOException, InterruptedException {
	    if(entry==null) {
	        executeQuery("reg delete \""+List.trim(branch,"\\")+"\"  /f");
	    }
	    else {
	        executeQuery("reg delete \""+List.trim(branch,"\\")+"\" /v "+entry+" /f");
	    }
	}
	
	/**
	 * filter registry entries from the raw result
	 * @param string plain result to filter regisry entries
	 * @param branch
	 * @param type
	 * @return filtered entries
	 * @throws RegistryException
	 */
	private static RegistryEntry[] filter(String string,String branch, short type) throws RegistryException {
		//print.ln(">>>>>>>>>>>>>>"+string+"<<<<<<<<<<");
	    branch=List.trim(branch,"\\");
	    StringBuffer result=new StringBuffer();
		ArrayList array=new ArrayList();
	  	String[] arr=string.split("\n");
	  	
		for(int i=0;i<arr.length;i++) {
			String line=arr[i].trim();
			int indexDWORD=line.indexOf(RegistryEntry.REGDWORD_TOKEN);
			int indexSTRING=line.indexOf(RegistryEntry.REGSTR_TOKEN);
			
			if((indexDWORD!=-1) || (indexSTRING!=-1) ) {
			    int index=(indexDWORD==-1)?indexSTRING:indexDWORD;
			    int len=(indexDWORD==-1)?lenSTRING:lenDWORD;
			    short _type=(indexDWORD==-1)?RegistryEntry.TYPE_STRING:RegistryEntry.TYPE_DWORD;
			    
				if(result.length()>0)result.append("\n");
				//String[] la = line.split("\\s");
				//print.ln(":"+line);
				//if(la.length==3) {
				    
				    String _key=line.substring(0,index).trim();
				    String _value=line.substring(index+len+1).trim();
				    if(_key.equals(NO_NAME)) _key="";
				    if(_type==RegistryEntry.TYPE_DWORD)_value=String.valueOf(Integer.parseInt(_value.substring(2),16));
				    RegistryEntry re = new RegistryEntry(_type,_key,_value);
				    if(type==RegistryEntry.TYPE_ANY || type==re.getType()) array.add(re);
				//}
			}
			else if(line.indexOf(branch)==0 && (type==RegistryEntry.TYPE_ANY || type==RegistryEntry.TYPE_KEY)) {
			    line=List.trim(line,"\\");
			    if(branch.length()<line.length()) {
			        array.add(new RegistryEntry(RegistryEntry.TYPE_KEY,List.last(line,"\\"),""));
			    }
			}
		}
		return (RegistryEntry[])array.toArray(new RegistryEntry[array.size()]);
	}











static class StreamReader extends Thread {
    private InputStream is;
    private StringWriter sw;

    StreamReader(InputStream is) {
      this.is = is;
      sw = new StringWriter();
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
      try {
        int c;
        while ((c = is.read()) != -1)
          sw.write(c);
        }
        catch (IOException e) {  }
      }

    String getResult() {
      return sw.toString();
    }
  }

  
}


 