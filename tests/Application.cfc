<cfcomponent>
	<cfset this.name = hash( getCurrentTemplatePath() )& gettickcount()>
    <cfset request.webadminpassword="server">
	
</cfcomponent>