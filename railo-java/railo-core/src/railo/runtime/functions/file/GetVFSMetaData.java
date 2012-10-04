package railo.runtime.functions.file;

import railo.commons.io.res.ResourceProvider;
import railo.runtime.PageContext;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class GetVFSMetaData {
	public static Struct call(PageContext pc, String scheme)  {
		ResourceProvider[] providers = pc.getConfig().getResourceProviders();
		ResourceProvider provider;
		scheme=scheme.trim();
		Struct sct=new StructImpl();
		for(int i=0;i<providers.length;i++){
			provider=providers[i];
			if(provider.getScheme().equalsIgnoreCase(scheme)){
				//MUST sct=provider.getMetaData();
				sct.setEL("Scheme", provider.getScheme());
				sct.setEL("Attributes", provider.isAttributesSupported());
				sct.setEL("CaseSensitive", provider.isCaseSensitive());
				sct.setEL("Mode", provider.isModeSupported());
				
				sct.setEL("Enabled", Boolean.TRUE);
				return sct;
			}	
		}
		sct.setEL("Enabled", Boolean.FALSE);
		return sct;
	}

}
