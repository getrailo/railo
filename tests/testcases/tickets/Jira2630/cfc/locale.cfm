<cfsetting showdebugoutput="no">

<cfoutput>#getLocale()#-#GetApplicationSettings().locale#-#lsDateFormat(date:createDate(2000,1,2),locale:url.locale)#</cfoutput>