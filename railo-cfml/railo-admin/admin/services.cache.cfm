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

<cfset driverNames=ComponentListPackage("cdriver")>
<cfloop array="#driverNames#" item="n">
	
	<cfif n NEQ "Cache" and n NEQ "Field" and n NEQ "Group">
		<cfset tmp = createObject("component","cdriver."&n)>
		<cfset drivers[tmp.getClass()]=tmp>
	</cfif>
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