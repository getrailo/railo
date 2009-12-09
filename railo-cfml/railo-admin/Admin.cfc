<cfcomponent>
	<cffunction access="remote" name="invoke" output="false">
		<cfargument name="type" required="yes" type="string">
		<cfargument name="password" required="yes" type="string">
		<cfargument name="attributeCollection" required="yes" type="struct">
		<cfargument name="callerId" required="no" type="string" default="undefined">
		
		<cfset var result="">
		<cfset var id=getRailoId()[arguments.type].id>
		<cfset var sec=getRailoId()[arguments.type].securityKey>
		<cfif not listFind(arguments.callerId,id)>
			<cfadmin 
				type="#arguments.type#"
				password="#Decrypt(arguments.password,sec)#"
				attributeCollection="#arguments.attributeCollection#"
				providedCallerIds="#arguments.callerId#"
				returnVariable="result">
			<cfif isDefined('result')><cfreturn result></cfif>
		</cfif>
		
		
	</cffunction>

</cfcomponent>