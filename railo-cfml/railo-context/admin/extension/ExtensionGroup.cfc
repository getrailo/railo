<cfcomponent>

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