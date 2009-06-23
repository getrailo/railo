<cfcomponent>

	<cfset this.steps=array()>
	
	<cffunction name="createStep" output="no"
    	hint="create a new step cfc">
    	<cfargument name="label" type="string" default="">
    	<cfargument name="description" type="string" default="">
        
        <cfset var step=createObject('component','ExtensionStep').init(label,description)>
        <cfset ArrayAppend(this.steps,step)>
    	<cfreturn step>
    </cffunction>
    
	<cffunction name="getSteps" output="no" returntype="array"
    	hint="return all created steps">
    	<cfreturn this.steps>
    </cffunction>
    
    
</cfcomponent>