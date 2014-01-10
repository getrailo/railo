<cfsetting showdebugoutput="no">
<cfset dt=createDateTime(2000,1,2,3,4,5,0,'GMT')>
<cfoutput>
#getTimeZone()#-#GetApplicationSettings().timezone#-#dt#
</cfoutput>