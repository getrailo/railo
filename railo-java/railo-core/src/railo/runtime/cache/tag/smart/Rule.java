package railo.runtime.cache.tag.smart;

import railo.runtime.type.dt.TimeSpan;

public class Rule {
	public final TimeSpan timespan;
	public int used=0;
	public final long created;
	
	public Rule(TimeSpan timespan){
		this.timespan=timespan;
		this.created=System.currentTimeMillis();
	}
	
}
