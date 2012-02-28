package railo.runtime.functions.other;


import java.util.HashSet;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.converter.LazyConverter;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Operator;
import railo.runtime.orm.hibernate.HBMCreator;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Collection.Key;

public class ObjectEquals {
	public static boolean call(PageContext pc , Object left, Object right) {
		return _equals(new HashSet<Object>(), left, right);
	}
	
	private static boolean _equals(HashSet<Object> done,Object left, Object right) {
		// null
		if(left==null) {
			return right==null;
		}
		if(left==right) return true;
		
		// components
		if(left instanceof Component){
			if(!(right instanceof Component)) return false;
			return _equals(done,(Component)left, (Component)right);
		}

		// collection
		if(left instanceof Collection){
			if(!(right instanceof Collection)) return false;
			return _equals(done,(Collection)left, (Collection)right);
		}
		
		// other
		return left.equals(right);
	}
	
	private static boolean _equals(HashSet<Object> done,Collection left, Collection right) {
		Object rawLeft = LazyConverter.toRaw(left);
		Object rawRight = LazyConverter.toRaw(right);
		
		if(done.contains(rawLeft)) return done.contains(rawRight);
		done.add(rawLeft);
		done.add(rawRight);
		try{
			if(left.size()!=right.size()) return false;
			Key[] keys = left.keys();
			Object l,r;
			for(int i=0;i<keys.length;i++){
				l=left.get(keys[i],null);
				r=right.get(keys[i],null);
				if(r==null || !_equals(done,l, r)) return false;
			}
			return true;
		}
		finally {
			done.remove(rawLeft);
			done.remove(rawRight);
		}
	}
	
	private static boolean _equals(HashSet<Object> done,Component left, Component right) {
		Object rawLeft = LazyConverter.toRaw(left);
		Object rawRight = LazyConverter.toRaw(right);
		
		if(done.contains(rawLeft)) return done.contains(rawRight);
		done.add(rawLeft);
		done.add(rawRight);
		
		
		
		try{
			if(left==null || right==null) return false;
			if(!left.getPageSource().equals(right.getPageSource())) return false;
			Property[] props = left.getProperties(true);
			Object l,r;
			props=HBMCreator.getIds(null,null,props,null,true);
			for(int i=0;i<props.length;i++){
				l=left.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
				r=right.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
				if(!_equals(done,l, r)) return false;
			}
			return true;
		}
		finally {
			done.remove(rawLeft);
			done.remove(rawRight);
		}
	}
}
