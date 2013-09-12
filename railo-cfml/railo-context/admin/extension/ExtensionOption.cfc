<cfcomponent>

	
	<cffunction name="init" output="no">
    	<cfargument name="value" type="string" required="yes">
    	<cfargument name="selected" type="boolean" default="#false#">
    	<cfargument name="label" type="string" required="yes">
    	<cfargument name="description" type="string" default="">
        
        <cfset this.value=arguments.value>
        <cfset this.selected=arguments.selected>
        <cfset this.label=arguments.label>
        <cfset this.description=arguments.description>
        
    	<cfreturn this>
    </cffunction>
    
    
	<cffunction name="getLabel" output="no" returntype="String">
    	<cfreturn this.label>
    </cffunction>
    
	<cffunction name="getValue" output="no" returntype="String">
    	<cfreturn this.value>
    </cffunction>
	<cffunction name="getSelected" output="no" returntype="boolean">
    	<cfreturn this.selected>
    </cffunction>
    
	<cffunction name="getDescription" output="no" returntype="string">
    	<cfreturn this.description>
    </cffunction>
    
    
    
        
	<cffunction name="setLabel" output="no" returntype="void">
    	<cfargument name="label" type="string" required="yes">
    	<cfset this.label=arguments.label>
    </cffunction>
	<cffunction name="setValue" output="no" returntype="void">
    	<cfargument name="value" type="string" required="yes">
    	<cfset this.value=arguments.value>
    </cffunction>
	<cffunction name="setSelected" output="no" returntype="void">
    	<cfargument name="description" type="boolean" required="yes">
    	<cfset this.selected=arguments.selected>
    </cffunction>
	<cffunction name="setDescription" output="no" returntype="void">
    	<cfargument name="description" type="string" required="yes">
    	<cfset this.description=arguments.description>
    </cffunction>
    

</cfcomponent>