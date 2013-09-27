package railo.runtime.component;

import java.lang.ref.SoftReference;

public class MetaDataSoftReference<T> extends SoftReference<T> {

	public final long creationTime;

	public MetaDataSoftReference(T referent) {
		super(referent);
		this.creationTime=System.currentTimeMillis();
	}

}
