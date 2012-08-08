package railo.runtime.util;

import java.util.ArrayList;
import java.util.List;

/**
 * class to operate some String List Operations and some list Operations
 */
public final class ListUtil {
	
	/**
	 * creates a List from a String "List"
	 * @param str String to creates List from
	 * @param delimiter delimiter to split string
	 * @return List 
	 */
	public static List stringToList(String str,char delimiter) {
		ArrayList list=new ArrayList();
		int len=str.length();
		if(len==0) return list;
		StringBuffer el=new StringBuffer();
		
		for(int i=0;i<len;i++) {
			char c=str.charAt(i);
			if(c==delimiter) {
				list.add(el.toString());
				if(el.length()>0) el=new StringBuffer();
			}
			else {
				el.append(c);
			}
		}
		list.add(el.toString());
		return list;
	}	
	
	/**
	 * creates a List from a String "List", trims empty values at start and end
	 * @param str String to creates List from
	 * @param delimiter delimiter to split string
	 * @return List 
	 */
	public static List stringToListTrim(String str,char delimiter) {
		ArrayList list=new ArrayList();
		int len=str.length();
		if(len==0) return list;
		StringBuffer el=new StringBuffer();
		boolean hasStart=false;
		
		for(int i=0;i<len;i++) {
			char c=str.charAt(i);
			if(c==delimiter) {
				if(!hasStart) {
					if(el.length()>0) {
						list.add(el.toString());
						hasStart=true;
					}
				}
				else {
					list.add(el.toString());
				}
				if(el.length()>0) el=new StringBuffer();
			}
			else {
				el.append(c);
			}
		}
		if(el.length()>0)list.add(el.toString());
		
		// remove empty items on the end
		for(int i=list.size()-1;i>=0;i--){
			if(list.get(i).toString().length()==0) {
				list.remove(i);
			}
			else break;
		}
		
		
		return list;
	}
	
	/**
	 * creates a List from a String "List", remove all empty values
	 * @param str String to creates List from
	 * @param delimiter delimiter to split string
	 * @return List 
	 */
	public static List stringToListRemoveEmpty(String str,char delimiter) {
		ArrayList list=new ArrayList();
		int len=str.length();
		if(len==0) return list;
		StringBuffer el=new StringBuffer();
		
		for(int i=0;i<len;i++) {
			char c=str.charAt(i);
			if(c==delimiter) {
				if(el.length()>0) {
					list.add(el.toString());
					el=new StringBuffer();
				}
			}
			else {
				el.append(c);
			}
		}
		if(el.length()>0)list.add(el.toString());
		return list;
	}
	
	/**
	 * cast all Elememts of a list to a String array, make simple cast of the object by toString method
	 * @param list List to cast to a String Array
	 * @return String array from List
	 */
	public static String[] toStringArray(List list) {
		int i=list.size();
		String[] arr=new String[i];
		for(i--;i>=0;i--) {
			arr[i]=list.get(i).toString();
		}
		return arr;
	}
	

}