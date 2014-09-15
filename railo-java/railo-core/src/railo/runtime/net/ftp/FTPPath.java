package railo.runtime.net.ftp;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.Dumpable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.PageException;
import railo.runtime.functions.arrays.ArrayMerge;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

/**
 * represent a ftp path
 */
public final class FTPPath implements Dumpable{
    
    private String path;
    private String name;
    //private Array arrPath;

    /**
     * @param current
     * @param relpath
     * @throws PageException
     */
    public FTPPath(String current, String relpath) throws PageException {
        relpath=relpath.replace('\\','/');
        //if(relpath.startsWith("./")) relpath=relpath.substring(2);
        //if(relpath.startsWith(".")) relpath=relpath.substring(1);
        Array relpathArr=ListUtil.listToArrayTrim(relpath,'/');

        // relpath is absolute
        if(relpath.startsWith("/")) {
            init(relpathArr);
            return;
        }
        if(current==null)current="";
        else current=current.replace('\\','/');
        Array parentArr=ListUtil.listToArrayTrim(current,'/');
        
        // Single Dot .
        if(relpathArr.size()>0&&relpathArr.get(1,"").equals(".")) {
            relpathArr.removeEL(1);
        }
        
        // Double Dot ..
        while(relpathArr.size()>0&&relpathArr.get(1,"").equals("..")) {
            relpathArr.removeEL(1);
            if(parentArr.size()>0) {
                parentArr.removeEL(parentArr.size());
            }
            else {
                parentArr.prepend("..");
            }
		}
        ArrayMerge.append(parentArr,relpathArr);
        init(parentArr);
    }
    
    private void init(Array arr) throws PageException {
        if(arr.size()>0) {
	        this.name=(String)arr.get(arr.size(),"");
	        arr.removeEL(arr.size());
	        this.path='/'+ListUtil.arrayToList(arr,"/")+'/';
        }
        else {
            this.path="/";
            this.name="";
        }
        //this.arrPath=arr;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path;
    }
    
    @Override
    public String toString() {
        return path+name;//+" - "+"path("+getPath()+");"+"name("+getName()+");"+"parent("+getParentPath()+");";
    }
    
    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = new DumpTable("string","#ff6600","#ffcc99","#000000");
		table.appendRow(1,new SimpleDumpData("FTPPath"),new SimpleDumpData(toString()));
		return table;
    }
}