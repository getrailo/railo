package railo.runtime.orm.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import railo.runtime.orm.ORMException;


public class HibernateUtil {

	public static Type getPropertyType(ClassMetadata metaData, String name) throws HibernateException {
		try{
			return  metaData.getPropertyType(name);
		}
		catch(HibernateException he){
			if(name.equalsIgnoreCase(metaData.getIdentifierPropertyName())) 
				return metaData.getIdentifierType();
			
			String[] names = metaData.getPropertyNames();
			for(int i=0;i<names.length;i++){
				if(names[i].equalsIgnoreCase(name))
					return metaData.getPropertyType(names[i]);
			}
			throw he;
		}
	}
	public static Type getPropertyType(ClassMetadata metaData, String name, Type defaultValue) {
		try{
			return  metaData.getPropertyType(name);
		}
		catch(HibernateException he){
			if(name.equalsIgnoreCase(metaData.getIdentifierPropertyName())) 
				return metaData.getIdentifierType();
			
			String[] names = metaData.getPropertyNames();
			for(int i=0;i<names.length;i++){
				if(names[i].equalsIgnoreCase(name))
					return metaData.getPropertyType(names[i]);
			}
			return defaultValue;
		}
	}
	
	public static String validateColumnName(ClassMetadata metaData, String name) throws ORMException {
		String res = validateColumnName(metaData, name,null);
		if(res!=null) return res;
		throw new ORMException("invalid name, there is no property with name ["+name+"] in the entity ["+metaData.getEntityName()+"]",
				"valid properties names are ["+railo.runtime.type.List.arrayToList(metaData.getPropertyNames(), ", ")+"]");
		
	}
	

	public static String validateColumnName(ClassMetadata metaData, String name, String defaultValue) {
		if(name.equalsIgnoreCase(metaData.getIdentifierPropertyName())) 
			return metaData.getIdentifierPropertyName();
		
		String[] names = metaData.getPropertyNames();
		for(int i=0;i<names.length;i++){
			if(names[i].equalsIgnoreCase(name))
				return names[i];
		}
		return defaultValue;
	}
	
	
}
