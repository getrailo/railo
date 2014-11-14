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
 ---><cfcomponent extends="Layout">
	
    <cfset fields=array(
		field("Pattern","pattern","%d{dd.MM.yyyy HH:mm:ss,SSS} %-5p [%c] %m%n",true,"This is the string which controls formatting and consists of a mix of literal content and conversion specifiers. for more details see: http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html","text")
		
		)>
    
	<cffunction name="getClass" returntype="string" output="false">
    	<cfreturn "org.apache.log4j.PatternLayout">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="false">
    	<cfreturn "Pattern">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "A flexible layout configurable with pattern string">
    </cffunction>
    
</cfcomponent>