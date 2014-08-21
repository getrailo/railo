package railo.commons.lang;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import railo.runtime.type.comparator.NumberComparator;
import railo.runtime.type.comparator.TextComparator;

public class ComparatorUtil {

	public static final int SORT_TYPE_TEXT=1;
	public static final int SORT_TYPE_TEXT_NO_CASE=2;
	public static final int SORT_TYPE_NUMBER=3;
	
	public static Comparator toComparator(int sortType, boolean orderAsc, Locale l,Comparator defaultValue) {
		// text
		if(sortType==SORT_TYPE_TEXT) {
			if(l!=null)return toCollator(l,Collator.IDENTICAL);
			return new TextComparator(orderAsc,false);
		}
		// text no case
		else if(sortType==SORT_TYPE_TEXT_NO_CASE) {
			if(l!=null)return toCollator(l,Collator.TERTIARY);
			return new TextComparator(orderAsc,true);
		}
		// numeric
		else if(sortType==SORT_TYPE_NUMBER) {
			return new NumberComparator(orderAsc);
		}
		else {
			return defaultValue;
		}	
	}
	

	private static Comparator toCollator(Locale l, int strength) {
		Collator c=Collator.getInstance(l);
		c.setStrength(strength);
		c.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
		return c;
	}

}
