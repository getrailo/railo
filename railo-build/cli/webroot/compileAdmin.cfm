<cfsetting showdebugoutput="No" enablecfoutputonly="Yes">
<cfparam name="url.dest" default="">
<cftry>

	<cfadmin 
		action="updateMapping"
		type="web"
		archive=""
		primary="physical"
		trusted="false"
		virtual="/railo-context-compiled"
		physical="#url.admin_source#"
		remoteClients="">
	
	<cfadmin 
		action="createArchive"
		type="web"
		file="#url.admin_source#/railo-context.ra"
		virtual="/railo-context-compiled"
		secure="true"
		append="false"
		remoteClients="">
		
		
	<cfcatch type="Any">
		<cfoutput>Mapping not created. Error occured. (#cfcatch.message#)</cfoutput>
		<cfsavecontent variable="errorFull">
		<cfdump var="#cfcatch#">
		</cfsavecontent>
		<cffile action="write" file="error.html" output="#errorFull#">
		<cfabort>
	</cfcatch>
</cftry>
<cfoutput>Railo Admin compiled to #url.admin_source#/railo-context.ra #url.dest#</cfoutput>

