<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ---><cfcomponent extends="Cache">
	
    <cfset fields=array(
		field("Time to idle in seconds","timeToIdleSeconds","0",true,"Sets the time to idle for an element before it expires. If all fields are set to 0 the element live as long the server live.","time"),
		field("Time to live in seconds","timeToLiveSeconds","0",true,"Sets the timeout to live for an element before it expires. If all fields are set to 0 the element live as long the server live.","time")
	)>
    
	<cffunction name="getClass" returntype="string">
    	<cfreturn "railo.runtime.cache.ram.RamCache">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string">
    	<cfreturn "RamCache">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Create a Ram Cache (in Memory Cache)">
    </cffunction>
    
</cfcomponent>