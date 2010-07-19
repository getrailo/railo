package railo.runtime.search.lucene2.query;

public class OpUtil {

	/*public static List getTerms(Object left, Object right) {
		List list=null;
		Op op;
		
		if(left instanceof Op) {
			op=(Op) left;
			list=op.getSearchedTerms();
		}
		else {
			if(right instanceof Op) {
				op=(Op) right;
				list=op.getSearchedTerms();
				list.add(left.toString());
				return list;
			}
			list=new ArrayList();
			list.add(left.toString());
			list.add(right.toString());
			return list;
		}
		
		if(right instanceof Op) {
			op=(Op) right;
			list.addAll(op.getSearchedTerms());
		}
		else list.add(right);
		
		
		return list;
	}
	
	public static List getTerms(Object obj) {
		if(obj instanceof Op) {
			return ((Op)obj).getSearchedTerms();
		}
		List list=new ArrayList();
		list.add(obj.toString());
		
		return list;
	}*/
}
