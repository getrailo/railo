<cfcomponent>

	<!---Init--->
	<cffunction name="init" output="false" returntype="void">
		<cfreturn this />
	</cffunction>
	
	<!---get--->
	<cffunction name="get" output="false" returntype="string" returnformat="plain" access="remote">
		<cfargument name="lib" type="string" required="false" default="" />
		<!--- give not access to files from other directories --->
		<cfset local.sLib = replace(arguments.lib, "../", "", "ALL")>
		<cfset sLib = replace(sLib, "..\", "", "ALL")>
		
		<cfset var filePath = expandPath('js/#sLib#.js')/>
		<cfset var local = {result=""} /><cfcontent type="text/javascript">
			<cfsavecontent variable="local.result">
				<cfif fileExists(filePath)>                
					<cfinclude template="js/#sLib#.js"/>
				</cfif>			
			</cfsavecontent>		
		<cfreturn local.result />	
	</cffunction>

	
</cfcomponent>