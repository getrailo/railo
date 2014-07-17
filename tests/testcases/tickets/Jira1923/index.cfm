<cfsetting showdebugoutput="no">
<cfoutput>
	<cfif isDefined("form.test")>form:#serialize(form.test)#->#serialize(getApplicationSettings().sameformfieldsasarray)#;</cfif><cfif isDefined("url.test")>url:#serialize(url.test)#->#serialize(getApplicationSettings().sameurlfieldsasarray)#;</cfif><!---

--->
</cfoutput>