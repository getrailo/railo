package railo.runtime.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import railo.commons.cli.Command;
import railo.commons.lang.StringUtil;
import railo.runtime.functions.string.ParseNumber;
import railo.runtime.op.Caster;
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
	public static String executeQuery(String[] cmd) throws IOException, InterruptedException {
		return Command.execute(cmd);
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
	    String[] cmd = new String[]{"reg","query",cleanBrunch(branch),"/v",entry};
		RegistryEntry[] rst = filter(executeQuery(cmd),branch,type);
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
		String[] cmd = new String[]{"reg","query",branch};
		return filter(executeQuery(cmd),cleanBrunch(branch),type);
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
	    
	    if(type==RegistryEntry.TYPE_KEY) {
	        String fullKey=List.trim(branch,"\\")+"\\"+List.trim(entry,"\\");
	        //String[] cmd = new String[]{"reg","add",cleanBrunch(fullKey),"/ve","/f"};
	        String[] cmd = new String[]{"reg","add",cleanBrunch(fullKey),"/f"};
	        executeQuery(cmd);
		}
	    else {
	    	if(type==RegistryEntry.TYPE_DWORD)value=Caster.toString(Caster.toIntValue(value,0));
	    	String[] cmd = new String[]{"reg","add",cleanBrunch(branch),"/v",entry,"/t",RegistryEntry.toStringType(type),"/d",value,"/f"};
	    	executeQuery(cmd);
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
	    	String[] cmd = new String[]{"reg","delete",cleanBrunch(branch),"/f"};
	    	executeQuery(cmd);
	        //executeQuery("reg delete \""+List.trim(branch,"\\")+"\"  /f");
	    }
	    else {
	    	String[] cmd = new String[]{"reg","delete",cleanBrunch(branch),"/v",entry,"/f"};
	    	executeQuery(cmd);
	        //executeQuery("reg delete \""+List.trim(branch,"\\")+"\" /v "+entry+" /f");
	    }
	}
	
	private static String cleanBrunch(String branch) {
		branch=branch.replace('/', '\\');
		branch=List.trim(branch,"\\");
		if(branch.length()==0) return "\\";
		return branch;
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
				 
				String _key=line.substring(0,index).trim();
				String _value=StringUtil.substringEL(line,index+len+1,"").trim();
				
				if(_key.equals(NO_NAME)) _key="";
				    if(_type==RegistryEntry.TYPE_DWORD)_value=String.valueOf(ParseNumber.invoke(_value.substring(2),"hex",0));
				    RegistryEntry re = new RegistryEntry(_type,_key,_value);
				    if(type==RegistryEntry.TYPE_ANY || type==re.getType()) array.add(re);
				//}
			}
			else if(line.indexOf(branch)==0 && (type==RegistryEntry.TYPE_ANY || type==RegistryEntry.TYPE_KEY)) {
			    line=List.trim(line,"\\");
			    if(branch.length()<line.length()) {
			        array.add(new RegistryEntry(RegistryEntry.TYPE_KEY,List.last(line,"\\",true),""));
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


 