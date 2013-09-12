<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="orm"
	secValue="yes">
	
<cfadmin 
	action="getORMSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="settings">
<cfadmin 
	action="getORMEngine"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="engine">
	
<!---
<div class="CheckError" style="width:740px">
The ORM Implementation is currently in Beta State. Its functionality can change before it's final release.
If you have any problems while using the ORM Implementation, please post the bugs and errors in our <a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
<br /><br />
</div>--->

<cfinclude template="services.orm.list.cfm"/>