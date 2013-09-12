<cfset StructDelete(application, "stText")>
<cfset StructDelete(session,"password"&request.adminType)>
<cfcookie expires="Now" name="railo_admin_pw_#request.adminType#" value="">
<cflocation url="#cgi.SCRIPT_NAME#" addtoken="No">