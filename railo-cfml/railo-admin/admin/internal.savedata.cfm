<cftry>
	<cfif listFind("addfavorite,removefavorite", url.action2) and structKeyExists(url, "favorite")>
		<cfset application.adminfunctions[url.action2](url.favorite) />
		<cflocation url="?action=#url.favorite#" addtoken="no" />
	</cfif>
	<cfcatch></cfcatch>
</cftry>
<cflocation url="#cgi.SCRIPT_NAME#" addtoken="no" />