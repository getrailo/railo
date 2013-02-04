package java.util;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;

public interface NullSensitive {
	public Object gib(Key key) throws PageException;
	public Object gib(Key key, Object defaultValue);
	public Object haeb(Key key, Object value);
}
