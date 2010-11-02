package railo.runtime.db;

public class ProcMetaCollection {

	public ProcMeta[] metas;
	public long created=System.currentTimeMillis();
	
	public ProcMetaCollection(ProcMeta[] metas) {
		this.metas=metas;
	}
	
	
}
