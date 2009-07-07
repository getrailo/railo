<cfsetting showdebugoutput="No" enablecfoutputonly="Yes">

<cftry>

	<cfadmin 
		action="updateMapping"
		type="web"
		password="#url.password#"
		archive=""
		primary="physical"
		trusted="false"
		virtual="/railo-context-compiled"
		physical="#url.admin_source#"
		remoteClients="">
	
	<cfadmin 
		action="createArchive"
		type="web"
		password="#url.password#"
		file="#url.admin_source#/railo-context.ra"
		virtual="/railo-context-compiled"
		secure="true"
		append="false"
		remoteClients="">
	<cfcatch type="Any">
		<cfoutput>Mapping not created. Error occured. (#cfcatch.message#)</cfoutput>
		<cfabort>
	</cfcatch>
</cftry>
<cfoutput>Railo Admin compiled...</cfoutput>

