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

	<cfset this.items=array()>

	<cffunction name="init" output="no">
    	<cfargument name="label" type="string">
    	<cfargument name="description" type="string">
        <cfset this.label=arguments.label>
        <cfset this.description=arguments.description>
    	<cfreturn this>
    </cffunction>
    
	<cffunction name="createItem" output="no">
    	<cfargument name="type" type="string" required="yes">
    	<cfargument name="name" type="string" required="yes">
    	<cfargument name="value" type="string" default="">
    	<cfargument name="selected" type="boolean" default="#false#">
    	<cfargument name="label" type="string" default="#arguments.name#">
    	<cfargument name="description" type="string" default="">
        
        <cfset var item=createObject('component','ExtensionItem').init(type:arguments.type,name:arguments.name,value:arguments.value,selected:arguments.selected,label:arguments.label,description:arguments.description)>
        <cfset ArrayAppend(this.items,item)>
    	
    	<cfreturn item>
    </cffunction>
    
	<cffunction name="getItems" output="no" returntype="array">
    	<cfreturn this.items>
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