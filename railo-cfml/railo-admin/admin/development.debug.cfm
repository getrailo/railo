
<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">


<cfset stText.debug.label="Label">
<cfset stText.debug.type="Type">
<cfset stText.debug.labelMissing="you need to define the label for the debug template">
<cfset stText.debug.noDriver="there is no debug template defined">
<cfset stText.debug.noAccess="you have no access to manipulate the debug settings">
<cfset stText.debug.ipRange="IP Range">
<cfset stText.debug.ipRangeDesc="the ip range where this template is used, use ""*"" as wilddard. Example:127.0.0,1,138.*.*.*">
<cfset stText.debug.ipRangeMIssing="Missing IP Range defintion">

<cfset stText.debug.list.serverTitle="Readonly Debug Templates">
<cfset stText.debug.list.webTitle="Debug Templates">
<cfset stText.debug.list.serverTitleDesc="Readonly debug templates are generated within the ""server administrator"" for all web instances and can not be modified by the ""web administrator"".">
<cfset stText.debug.list.webTitleDesc="List of all existing debug templates.">
<cfset stText.debug.titleCreate="Create a Template for a specific IP Range">

<cfadmin 
	action="getDebugEntry"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="debug">
    

<cfadmin 
	action="getDebug"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="_debug">
    
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="access"
	secType="debugging">
<cfset hasAccess=access>

    
<!--- load available drivers --->
	<cfset drivers=struct()>
    <cfdirectory directory="./debug" action="list" name="dir" recurse="no" filter="*.cfc">
    <cfloop query="dir">
    	<cfif dir.name EQ "Debug.cfc" or dir.name EQ "Field.cfc" or dir.name EQ "Group.cfc">
        	<cfcontinue>
        </cfif>
    	<cfset tmp=createObject('component','debug/#ReplaceNoCase(dir.name,'.cfc','')#')>
        <cfset drivers[trim(tmp.getId())]=tmp>
    </cfloop>

<!--- 
<span class="CheckError">
The Gateway Implementation is currently in Beta State. Its functionality can change before it's final release.
If you have any problems while using the Gateway Implementation, please post the bugs and errors in our <a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
</span><br /><br />
--->


<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="development.debug.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="development.debug.create.cfm"/></cfcase>

</cfswitch>