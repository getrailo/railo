<cfcomponent extends="Cache">


	
    <cfset fields=array(
		field("Eternal","eternal","false",true,"Sets whether elements are eternal. If eternal, timeouts are ignored and the element is never expired","checkbox","true"),
		field("Maximal elements in memory","maxelementsinmemory","10000",true,"Sets the maximum objects to be held in memory","text"),
		field("Memory Store Eviction Policy","memoryevictionpolicy","LRU,LFU,FIFO",true,"The algorithm to used to evict old entries when maximum limit is reached, such as LRU (least recently used), LFU (least frequently used) or FIFO (first in first out).","select"),
		field("Time to idle in seconds","timeToIdleSeconds","86400",true,"Sets the time to idle for an element before it expires. Is only used if the element is not eternal","time"),
		field("Time to live in seconds","timeToLiveSeconds","86400",true,"Sets the timeout to live for an element before it expires. Is only used if the element is not eternal","time"),
		
		//group("Disk","Hard disk specific settings"),
		field("Disk persistent","diskpersistent","true",true,"for caches that overflow to disk, whether the disk store persists between restarts of the Engine.","checkbox","true"),
		field("Overflow to disk","overflowtodisk","true",true,"for caches that overflow to disk, the disk cache persist between CacheManager instances","checkbox","true"),
		field("Maximal elements on disk","maxelementsondisk","10000000",true,"Sets the maximum number elements on Disk. 0 means unlimited","text")
		
		
		
		
		
		
	)>

	<cffunction name="getClass" returntype="string">
    	<cfreturn "railo.runtime.cache.eh.EHCacheLite">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="no">
    	<cfreturn "EHCache Lite">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfset var c="">
    	<cfsavecontent variable="c">
Ehcache is a widely used java cache for general purpose caching, Java EE and light-weight containers.
<br><br>
It features memory and disk stores, replicate by copy and invalidate, listeners, cache loaders, cache extensions, cache exception handlers, a gzip caching servlet filter, RESTful & SOAP APIs, an implementation of JSR107 and much more...
<br><br>
Ehcache is available under an Apache open source license and is actively developed, maintained and supported.

This version does not support replication, for replication check the Extension/Application page for "EHCache".
        </cfsavecontent>
    
    
    	<cfreturn c>
    </cffunction>
</cfcomponent>