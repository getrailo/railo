<cfcomponent>

	<!---Init--->
	<cffunction name="init" output="false" returntype="void">
		<cfreturn this />
	</cffunction>
	
	<!---get--->
	<cffunction name="get" output="false" returntype="string" returnformat="plain" access="remote">
		<cfargument name="lib" type="string" required="false" default="" />
		<cfset var filePath = expandPath('js/#arguments.lib#.js')/>
		<cfset var local = {result=""} /><cfcontent type="text/javascript">
			<cfsavecontent variable="local.result">
				<cfif fileExists(filePath)>                
					<cfinclude template="js/#arguments.lib#.js"/>
				</cfif>			
			</cfsavecontent>		
		<cfreturn local.result />	
	</cffunction>

	
</cfcomponent>