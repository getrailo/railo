<cfcomponent displayname="Hello" hint="hint for Hello">
	<cfset this.prop="0">
   
   <cffunction name="set">
   		<cfargument name="arg1">
		<cfset this.prop = arguments.arg1>
   </cffunction>
   <cffunction name="get">
   		<cfreturn this.prop>
   </cffunction>
   
</cfcomponent>