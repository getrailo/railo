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
		)>
    
	<cffunction name="getClass" returntype="string">
    	<cfreturn "railo.commons.io.log.log4j.layout.ClassicLayout">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="false">
    	<cfreturn "Classic">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Same logging layout as with Railo 1 - 4.1">
    </cffunction>
    
</cfcomponent>