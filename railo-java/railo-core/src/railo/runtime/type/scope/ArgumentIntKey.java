package railo.runtime.type.scope;

import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;

public final class ArgumentIntKey extends KeyImpl {
	
	private static final ArgumentIntKey[] KEYS = new ArgumentIntKey[]{
		new ArgumentIntKey(0),
		new ArgumentIntKey(1),
		new ArgumentIntKey(2),
		new ArgumentIntKey(3),
		new ArgumentIntKey(4),
		new ArgumentIntKey(5),
		new ArgumentIntKey(6),
		new ArgumentIntKey(7),
		new ArgumentIntKey(8),
		new ArgumentIntKey(9),
		new ArgumentIntKey(10),
		new ArgumentIntKey(11),
		new ArgumentIntKey(12),
		new ArgumentIntKey(13),
		new ArgumentIntKey(14),
		new ArgumentIntKey(15),
		new ArgumentIntKey(16),
		new ArgumentIntKey(17),
	};
	
	private int intKey;

	public ArgumentIntKey(int key) {
		super(Caster.toString(key));
		
		this.intKey=key;
	}

	public int getIntKey() {
		return intKey;
	}

	public static ArgumentIntKey init(int i) {
		if(i>=0 && i<KEYS.length) return KEYS[i];
		return new ArgumentIntKey(i);
	}
}
