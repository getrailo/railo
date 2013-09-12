<cfcomponent>

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