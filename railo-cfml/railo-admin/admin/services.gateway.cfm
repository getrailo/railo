<cfif request.admintype EQ "server"><cflocation url="#request.self#" addtoken="no"></cfif>

<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

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

<!---<span class="CheckError">
The Gateway Implementation is currently in Beta State. Its functionality can change before it's final release.
If you have any problems while using the Gateway Implementation, please post the bugs and errors in our <a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
</span><br /><br />--->

<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="services.gateway.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="services.gateway.create.cfm"/></cfcase>

</cfswitch>