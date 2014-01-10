<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="setUp"></cffunction>
	<cffunction name="test">
		
		<cfsavecontent variable="content"><cfset createObject("component","jira0236._argColl").ino(a:1,b:2)></cfsavecontent>
		<cfset assertEquals("A:1;B:2;A:1;B:37;",trim(content))>
		
		<cfsavecontent variable="content"><cfset createObject("component","jira0236._argColl").ino(1,2)></cfsavecontent>
		<cfset assertEquals("A:1;B:2;2:2;A:1;B:37;",trim(content))>
		
	</cffunction>
</cfcomponent>