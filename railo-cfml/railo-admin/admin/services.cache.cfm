<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">


<cfset stText.Settings.cache.noDriver="There is no Cache Driver Available">
<cfset stText.Settings.cache.title="Cache">
<cfset stText.Settings.cache.description="Verwalten der Cache">
<cfset stText.Settings.cache.name="Name">
<cfset stText.Settings.cache.class="Class">
<cfset stText.Settings.cache.titleCreate="Create a new cache connection">
<cfset stText.Settings.cache.titleExisting="List of existing cache connection">

<cfset stText.Settings.cache.descExisting="List of all existing connection for ths enviroment">
<cfset stText.Settings.cache.nameMissing="Cancel">


<cfset stText.Settings.cache.type="Type">

<cfset stText.Settings.cache.defaulttypeObject="Object">
<cfset stText.Settings.cache.defaulttypeObjectDesc="This cache connection is used for all cache operations (cacheGet,cachePut ...)">
<cfset stText.Settings.cache.defaulttypeTemplate="Template">
<cfset stText.Settings.cache.defaulttypeTemplateDesc="This cache connection is used for the tag cfcache">
<cfset stText.Settings.cache.defaulttypeQuery="Query">
<cfset stText.Settings.cache.defaulttypeQueryDesc="This cache connection is used for the caching of the tag cfquery">
<cfset stText.Settings.cache.defaulttypeResource="Resource">
<cfset stText.Settings.cache.defaulttypeResourceDesc="This cache connection is used for the Ram Resource (ram://...)">

<cfset stText.Settings.cache.defaultDesc="Define the default cache connection for tempates (cfcache) and object (cacheGet, cachePut ...), this connection is used when no cache name is explicit defined">



<cfset stText.Settings.cache.Default="Default">
<cfset stText.Settings.cache.DefaultTitle="Default cache connection">
<cfset stText.Settings.cache.noDefault="no default cache">
<cfset stText.Settings.cache.noAccess="no access to create cache connections">
<cfset stText.Settings.cache.defaultDesc2="Define if this connection will be the default cache connection, the default cache connection is used when no cache name is explicit defined">
<cfset stText.Settings.cache.titleReadOnly             = "Readonly cache connections">
<cfset stText.Settings.cache.descReadOnly  = "Readonly cache connections are generated within the ""server administrator"" for all web instances and can not be modified by the ""web administrator"".">
<cfset stText.Settings.cache.Buttons.default="set as default">


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
<span class="CheckError">
The Cache Implementation is currently in Beta State. Its functionality can change before it's final release.
For addional Cache Implementation (EHCache,MemCache,JBossCache) check the Extension/Application page.<br />
If you have any problems while using the Cache Implementation, please post the bugs and errors in our
<a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
<br /><br />
</span>
<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="services.cache.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="services.cache.create.cfm"/></cfcase>

</cfswitch>