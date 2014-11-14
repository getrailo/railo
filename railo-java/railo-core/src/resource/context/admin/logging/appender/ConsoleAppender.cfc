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
 ---><cfcomponent extends="Appender">
	
    <cfset fields=array(
		field("Stream type","streamtype","output,error",true,"define if the appender logs to the error or output stream","select")
		
		)>
    
	<cffunction name="getClass" returntype="string" output="false">
    	<cfreturn "railo.commons.io.log.log4j.appender.ConsoleAppender">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="false">
    	<cfreturn "Console">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Logs events to to the error or output stream">
    </cffunction>
    
</cfcomponent>