<cfsetting showdebugoutput="No" enablecfoutputonly="Yes">

<cftry>

    <cfset virtualPath = "/railo-context-compiled">

	<cfadmin action="updateMapping" type="web" password="#url.password#"
		virtual="#virtualPath#"
		physical="#url.admin_source#"
		primary="physical"
		trusted="false"
		archive="">

	<cfadmin action="createArchive" type="web" password="#url.password#"
		virtual="#virtualPath#"
		file="#url.admin_source#/railo-context.ra"
		addCFMLFiles="false"
		addNonCFMLFiles="false"
		append="false">

	<cfcatch type="Any">
		<cfoutput>Mapping not created. Error occured. (#cfcatch.message#)</cfoutput>
		<cfabort>
	</cfcatch>
</cftry>
<cfoutput>Railo Admin compiled...</cfoutput>
