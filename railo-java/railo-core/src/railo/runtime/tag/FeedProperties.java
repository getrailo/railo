package railo.runtime.tag;

import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.StructUtil;

public class FeedProperties {
	private static final Collection.Key ITEM = KeyImpl.intern("ITEM");
	private static final Collection.Key ITEMS = KeyImpl.intern("ITEMS");
	private static final Collection.Key ENTRY = KeyImpl.intern("ENTRY");
	private static final Collection.Key RDF = KeyImpl.intern("RDF");
	private static final Collection.Key RSS = KeyImpl.intern("RSS");
	private static final Collection.Key CHANNEL = KeyImpl.intern("channel");
	
	
	public static Struct toProperties(Struct data) {
		data=(Struct) Duplicator.duplicate(data,true);
		
		

		Struct rdf = Caster.toStruct(data.removeEL(RDF),null,false);
		if(rdf==null)rdf = Caster.toStruct(data.removeEL(RSS),null,false);
		if(rdf!=null){
			rdf.removeEL(ITEM);
			Struct channel = Caster.toStruct(rdf.get(CHANNEL,null),null,false);
			if(channel!=null){
				channel.removeEL(ITEMS);
				StructUtil.copy(channel, data, true);
				
			}
		}
		

		data.removeEL(ITEM);
		data.removeEL(ENTRY);
		
		return data;
	}
}
