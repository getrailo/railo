<cfsetting showdebugoutput="no">
<!--- Cache the request --->
<cfoutput>#getTickcount()#</cfoutput>,
<cfcache action="cache">
	<cfoutput>#getTickcount()#</cfoutput>
</cfcache>
