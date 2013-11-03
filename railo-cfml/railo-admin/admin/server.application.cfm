<cfsilent>
<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">


<cfadmin 
	action="getLocales"
	locale="#stText.locale#"
	returnVariable="locales">
	
<cfadmin 
	action="getTimeZones"
	locale="#stText.locale#"
	returnVariable="timezones">
	
<cfadmin 
	action="getRegional"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="regional">

<cfadmin 
	action="getScope"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="scope">
	
<cfadmin 
	action="getInfo"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="info">

<cfadmin 
	action="getApplicationSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="appSettings">
<cfset requestTimeout=
		appSettings.requestTimeout_second +
		(appSettings.requestTimeout_minute*60) +
		(appSettings.requestTimeout_hour*3600) +
		(appSettings.requestTimeout_day*3600*24)>
		

<cfadmin 
	action="getOutputSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="outputSetting">

<cfset stText.application.title="Application.cfc">
<cfset stText.application.desc="All settings possible in Application.cfc based on your settings">
</cfsilent>
<cfoutput>
	

	<div class="pageintro">#stText.application.title#</div>
	

	<cfsavecontent variable="codeSample">
component {

	this.name = "#info.label#"; // name of the application context

	// regional settings
	this.locale = "#regional.locale#"; // default locale used for formating dates, numbers ...
	this.timezone = "#regional.timezone#"; // default timezone used

	// scope handling
	this.applicationTimeout = createTimeSpan( #scope.applicationTimeout_day#, #scope.applicationTimeout_hour#, #scope.applicationTimeout_minute#, #scope.applicationTimeout_second# ); // lifespan of a untouched application scope

	this.sessionManagement = #scope.sessionManagement#; // session handling enabled or not
	this.sessionType = "#scope.sessionType#"; // cfml or jee based sessions
	this.sessionTimeout = createTimeSpan( #scope.sessionTimeout_day#, #scope.sessionTimeout_hour#, #scope.sessionTimeout_minute#, #scope.sessionTimeout_second# ); // untouched session lifespan

	this.clientManagement = #scope.clientManagement#; // client scope enabled or not
	this.setDomainCookies = #scope.domainCookies#; // using domain cookies or not
	this.setClientCookies = #scope.clientCookies#;

	this.localMode = "#scope.LocalMode#"; // prefer the local scope at unscoped write

	this.bufferOutput = "#outputSetting.bufferOutput#"; // buffer the output of a tag/function body to output in case of a exception

	// request
	setting requestTimeout = "#requestTimeout#"; // max lifespan of a running request
}
</cfsavecontent>
<cf_admin_coding_tip codeSample="#codeSample#" text="#stText.application.desc#" expand="true">
	

</cfoutput>