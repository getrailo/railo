<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="test">
		<cfstoredproc procedure="spweb_ZipCodes_LookupLatLon3074" datasource="mySQL" cachedwithin="#CreateTimeSpan(0,0,10,0)#">
		    <cfprocparam cfsqltype="cf_sql_varchar" value="57103">
		    <cfprocresult name="rsLatLon" resultset="1">
		</cfstoredproc>

		<cfset assertEquals("","")>
	</cffunction>
</cfcomponent>