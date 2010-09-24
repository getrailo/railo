<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">


<cfset stText.Settings.gateway.noDriver="There is no Gateway Type Available">
<cfset stText.Settings.gateway.title="Gateway">
<cfset stText.Settings.gateway.description="Verwalten der Gateways">
<cfset stText.Settings.gateway.id="ID">
<cfset stText.Settings.gateway.class="Class">
<cfset stText.Settings.gateway.titleCreate="Create a new Gateway instance">
<cfset stText.Settings.gateway.titleExisting="List of existing gateway instances">

<cfset stText.Settings.gateway.descExisting="List of all existing gateways for ths enviroment">
<cfset stText.Settings.gateway.nameMissing="Cancel">


<cfset stText.Settings.gateway.type="Type">
<cfset stText.Settings.gateway.state="State">



<cfset stText.Settings.gateway.Default="Default">
<cfset stText.Settings.gateway.DefaultTitle="Default cache connection">
<cfset stText.Settings.gateway.noDefault="no default cache">
<cfset stText.Settings.gateway.noAccess="no access to create gateway instances">
<cfset stText.Settings.gateway.defaultDesc2="Define if this connection will be the default cache connection, the default cache connection is used when no cache name is explicit defined">
<cfset stText.Settings.gateway.titleReadOnly             = "Readonly cache connections">
<cfset stText.Settings.gateway.descReadOnly  = "Readonly cache connections are generated within the ""server administrator"" for all web instances and can not be modified by the ""web administrator"".">
<cfset stText.Settings.gateway.Buttons.default="set as default">


<cfadmin 
	action="getGatewayEntries"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="entries">
    
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="access"
	secType="gateway">
    
<!--- load available drivers --->
	<cfset drivers=struct()>
    <cfdirectory directory="./gdriver" action="list" name="dir" recurse="no" filter="*.cfc">
    <cfloop query="dir">
    	<cfif dir.name EQ "Gateway.cfc" or dir.name EQ "Field.cfc" or dir.name EQ "Group.cfc">
        	<cfcontinue>
        </cfif>
    	<cfset tmp=createObject('component','gdriver/#ReplaceNoCase(dir.name,'.cfc','')#')>
        <cfset drivers[dir.name]=tmp>
    </cfloop>
<!--- add driver to query --->
<cfset QueryAddColumn(entries,"driver",array())>
<cfloop query="entries">
    <cfloop collection="#drivers#" item="key">
    	<cfset d=drivers[key]>
        
        <cfif 
			(StructKeyExists(d,'getCFCPath')?d.getCFCPath() EQ entries.cfcPath:"" EQ entries.cfcPath)
			and 
			(StructKeyExists(d,'getClass')?d.getClass() EQ entries.class:"" EQ entries.class)
			>
			<cfset QuerySetCell(entries,"driver",d,entries.currentrow)>
            
		</cfif>
    </cfloop>
    
</cfloop>

<span class="CheckError">
The Gateway Implementation is currently in Beta State. Its functionality can change before it's final release.
If you have any problems while using the Gateway Implementation, please post the bugs and errors in our <a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
</span>

<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="services.gateway.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="services.gateway.create.cfm"/></cfcase>

</cfswitch>