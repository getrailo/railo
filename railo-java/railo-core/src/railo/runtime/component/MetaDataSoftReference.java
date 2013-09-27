package railo.runtime.component;

import java.lang.ref.SoftReference;

public class MetaDataSoftReference<T> extends SoftReference<T> {

	public final long creationTime;

	public MetaDataSoftReference(T referent, long creationTime) {
		super(referent);
		this.creationTime=creationTime;
	}

}
