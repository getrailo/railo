<cfcomponent>
	<cfset this.name = hash( getCurrentTemplatePath() )& gettickcount()>
    <cfsetting showdebugoutput="no">
</cfcomponent>