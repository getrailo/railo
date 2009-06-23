<cfset StructDelete(session,"password"&request.adminType)>
<cflocation url="#cgi.SCRIPT_NAME#">