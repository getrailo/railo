<cfcomponent extends="B3" hint="this is component c" displayname="Cer">
	<cfset cfc_C="">
   <cfset a="C_a"> 
   <cfset var_In_C="">
   
   <cffunction access="public" name="getVariableList" output="false" returntype="string" displayname="getVariableLister" hint="some function">
   		<!--- <cfdump var="#super.getVariableListX#" label="super.getVariableListX">		
   		<cfdump var="#super.super#" label="super.super">	
   		<cfdump var="#super#" label="super">	 --->
		
   		<cfdump var="#(this)#" label="this">		
   		<cfreturn structkeyList(variables)>
   </cffunction>
</cfcomponent> 