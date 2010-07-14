package railo.runtime.type.comparator;

import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;

import java.util.Comparator;

public class ArrayOfStructComparator implements Comparator<Struct> {


	private Key key;
	
	/**
	 * Constructor of the class
	 * @param key key used from struct
	 */
	public ArrayOfStructComparator(Collection.Key key){
		this.key=key;
	}

	public int compare(Struct s1, Struct s2) {
		return compareObjects(s1.get(key,""), s2.get(key,"")) ;
	}
	

	private int compareObjects(Object oLeft, Object oRight) {
		return Caster.toString(oLeft,"").compareToIgnoreCase(Caster.toString(oRight,""));
		//return Caster.toString(oLeft).compareTo(Caster.toString(oRight));
	}



}
