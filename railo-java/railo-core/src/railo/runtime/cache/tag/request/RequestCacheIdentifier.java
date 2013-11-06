package railo.runtime.cache.tag.request;

import railo.runtime.cache.tag.CacheIdentifier;

public class RequestCacheIdentifier implements CacheIdentifier {
	private String id;
	
	public RequestCacheIdentifier(String id){
		this.id=id;
	}
	
	@Override
	public String id() {
		return id;
	}

}
