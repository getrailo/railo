<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cfadmin 
	action="getCacheConnections"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="connections">
    
<!--- load available drivers --->
<cfset drivers=struct()>
<cfdirectory directory="./cdriver" action="list" name="dir" recurse="no" filter="*.cfc">
<cfloop query="dir">
	<cfif dir.name EQ "Cache.cfc" or dir.name EQ "Field.cfc" or dir.name EQ "Group.cfc">
		<cfcontinue>
	</cfif>
	<cfset tmp=createObject('component','cdriver.#ReplaceNoCase(dir.name,'.cfc','')#')>
	<cfset drivers[tmp.getClass()]=tmp>
</cfloop>

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="access"
	secType="cache">

<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="services.cache.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="services.cache.create.cfm"/></cfcase>

</cfswitch>