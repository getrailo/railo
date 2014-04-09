<cfcomponent output="false" hint="A generic service.">
	
	<cffunction name="getMembers" access="remote" returntype="Members"
			output="false"
			hint="Returns a Members object containing an array of Member
				objects.">
		
		<cfset local.memberCount = 3 />

		<cfset local.membersObj = CreateObject( 'component', 'Members' ) />
		<cfset local.membersObj.members = [] />

		<cfloop from="1" to="#local.memberCount#" index="local.i">
			<cfset local.memberObj = CreateObject( 'component', 'Member' ) />
			<cfset local.memberObj.name = 'Member #local.i#' />
			<cfset local.memberObj.joinDate =CreateDate( 2012 - local.i, local.i, 1 ) />

			<cfset ArrayAppend( local.membersObj.members, local.memberObj ) />
		</cfloop>
		<xcfset systemOutput(membersObj)>
		<cfreturn local.membersObj />
	</cffunction>

</cfcomponent>