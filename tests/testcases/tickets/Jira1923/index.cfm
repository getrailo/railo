<cfsetting showdebugoutput="no">
<cfoutput>
	<cfif isDefined("form.test")>form:#serialize(form.test)#;</cfif><cfif isDefined("url.test")>url:#serialize(url.test)#;</cfif>
</cfoutput>