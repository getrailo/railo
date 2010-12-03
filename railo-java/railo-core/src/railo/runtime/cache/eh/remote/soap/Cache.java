/* cache - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package railo.runtime.cache.eh.remote.soap;

public class Cache
{
    private CacheConfiguration cacheConfiguration;
    private String description;
    private String name;
    private Object statistics;
    private String uri;
    
    public final CacheConfiguration getCacheConfiguration() {
	return cacheConfiguration;
    }
    
    public final void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
	this.cacheConfiguration = cacheConfiguration;
    }
    
    public final String getDescription() {
	return description;
    }
    
    public final void setDescription(String description) {
	this.description = description;
    }
    
    public final String getName() {
	return name;
    }
    
    public final void setName(String name) {
	this.name = name;
    }
    
    public final Object getStatistics() {
	return statistics;
    }
    
    public final void setStatistics(Object statistics) {
	this.statistics = statistics;
    }
    
    public final String getUri() {
	return uri;
    }
    
    public final void setUri(String uri) {
	this.uri = uri;
    }
}
