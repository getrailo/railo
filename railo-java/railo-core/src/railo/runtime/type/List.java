package railo.runtime.type;

import railo.runtime.type.util.ListUtil;

/**
 * @deprecated BACKCOMP this class only exists for backward compatibility to code genrated for .ra files, DO NOT USE
 */
public class List {

	public static Array listToArrayRemoveEmpty(String list, String delimiter){
		return ListUtil.listToArrayRemoveEmpty(list, delimiter);
	}

	public static Array listToArrayRemoveEmpty(String list, char delimiter){
		return ListUtil.listToArrayRemoveEmpty(list, delimiter);
	}

	public static int listFindForSwitch(String list, String value, String delimiter){
		return ListUtil.listFindForSwitch(list, value, delimiter);
	}
	

}
