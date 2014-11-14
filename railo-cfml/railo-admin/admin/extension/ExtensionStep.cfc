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
 ---><cfcomponent>

	<cfset this.groups=array()>
	
    
	<cffunction name="init" output="no">
    	<cfargument name="label" type="string">
    	<cfargument name="description" type="string">
        <cfset this.label=arguments.label>
        <cfset this.description=arguments.description>
    	<cfreturn this>
    </cffunction>
    
    
	<cffunction name="createGroup" output="no"
    	hint="create a new group cfc">
    	<cfargument name="label" type="string" default="">
    	<cfargument name="description" type="string" default="">
        
        <cfset var group=createObject('component','ExtensionGroup').init(label,description)>
        <cfset ArrayAppend(this.groups,group)>
    	<cfreturn group>
    </cffunction>
    
	<cffunction name="getGroups" output="no" returntype="array"
    	hint="return all created groups">
    	<cfreturn this.groups>
    </cffunction>
    
    
    
	<cffunction name="getLabel" output="no" returntype="String">
    	<cfreturn this.label>
    </cffunction>
    
	<cffunction name="getDescription" output="no" returntype="String">
    	<cfreturn this.description>
    </cffunction>
    
	<cffunction name="setLabel" output="no" returntype="void">
    	<cfargument name="label" type="string" required="yes">
    	<cfset this.label=arguments.label>
    </cffunction>
    
	<cffunction name="setDescription" output="no" returntype="void">
    	<cfargument name="description" type="string" required="yes">
    	<cfset this.description=arguments.description>
    </cffunction>
    
</cfcomponent>