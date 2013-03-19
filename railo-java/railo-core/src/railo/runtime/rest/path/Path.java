package railo.runtime.rest.path;

import java.util.Iterator;

import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;

public abstract class Path {
	/**
	 * check if given path part match this path Path part defintion
	 * @param variables fill all key value pairs extracte from path to this Map
	 * @param path path to check
	 * @return true if the given path match, false otherwise 
	 */
	public abstract boolean match(Struct variables,String path);

	public static Path[] init(String path) {
		Array arr = ListUtil.listToArrayRemoveEmpty(path,'/');
		Path[] rtn=new Path[arr.size()];
		Iterator it = arr.valueIterator();
		int index=-1;
		String str;
		while(it.hasNext()){
			index++;
			str=Caster.toString(it.next(),null);
			//print.e("str:"+str);
			if(str.indexOf('{')!=-1) rtn[index]=ExpressionPath.getInstance(str);
			else rtn[index]=new LiteralPath(str);
		}
		return rtn;
	}
}
