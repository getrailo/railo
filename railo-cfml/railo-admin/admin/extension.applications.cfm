<cfsetting enablecfoutputonly="yes">



<cfif StructKeyExists(form,'action2')>
	<cfset url.action2="install3">
</cfif>
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfif not isDefined('session.extFilter2')>
	<cfset session.extFilter.category="">
	<cfset session.extFilter.name="">
	<cfset session.extFilter.provider="">
	<cfset session.extFilter2.category="">
	<cfset session.extFilter2.name="">
	<cfset session.extFilter2.provider="">
</cfif>

<cfadmin 
	action="getExtensionProviders"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="providers">
<cfset request.providers=providers>
    

<cfadmin 
    action="getExtensions"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    returnVariable="extensions">
 
<cfparam name="err" default="#struct(message:"",detail:"")#">
<cfset apps=array()>
<cfset infos=array()>
<cfset urls=array()>
<cfloop query="providers">
	<cftry>
		<cfset provider=loadCFC(providers.url)>
        <cfset ArrayAppend( apps,provider.listApplications())>
        <cfset ArrayAppend( infos,provider.getInfo())>
        <cfset ArrayAppend( urls,providers.url)>
    	<cfcatch>
        	<cfif len(err.message)>
        		<cfset err.message&="<br>can't load provider [#providers.url#]">
            <cfelse>
        		<cfset err.message="can't load provider [#providers.url#]">
            </cfif>
        </cfcatch>
    </cftry>
</cfloop>

<cffunction name="isInstalled">
	<cfreturn 1>
	<cfreturn RandRange(1,0)>
</cffunction>

<cffunction name="updateAvailable">
    <cfargument name="extensions" required="yes" type="query">
	<cftry>
        <cfset var detail=getDetail(hash(extensions.provider),extensions.id)>
        <cfif not StructKeyExists(detail,'app')><cfreturn false></cfif>
        <cfreturn extensions.version LT detail.app.version>
    	<cfcatch>
        	<cfreturn false>
        </cfcatch>
    </cftry>
</cffunction>





        
<cffunction name="doFilter" returntype="string" output="false">
	<cfargument name="filter" required="yes" type="string">
	<cfargument name="value" required="yes" type="string">
	<cfargument name="exact" required="no" type="boolean" default="false">
	
	<cfset arguments.filter=replace(arguments.filter,'*','',"all")>
	<cfif not len(filter)>
		<cfreturn true>
	</cfif>
	<cfif exact>
		<cfreturn filter EQ value>
	<cfelse>
		<cfreturn FindNoCase(filter,value)>
	</cfif>
</cffunction>


<cffunction name="loadCFC" returntype="struct" output="yes">
	<cfargument name="provider" required="yes" type="string">
	<cfreturn createObject('component',"ExtensionProviderProxy").init(arguments.provider)>
</cffunction>
<cfset request.loadCFC=loadCFC>

<cffunction name="getDetail" returntype="struct" output="yes">
	<cfargument name="hashProvider" required="yes" type="string">
	<cfargument name="appId" required="yes" type="string">
	<cfset var detail=struct()>
    <cfset providers=request.providers>
	<cfloop query="providers">
		<cfif hash(providers.url) EQ arguments.hashProvider>
            <cfset detail.provider=loadCFC(providers.url)>
            <cfset var apps=detail.provider.listApplications()>
            <cfset detail.info=detail.provider.getInfo()>
            <cfset detail.url=providers.url>
            <cfset detail.info.cfc=providers.url>
            <cfloop query="apps">
                <cfif apps.id EQ arguments.appId>
                	<cfset detail.app=querySlice(apps,apps.currentrow,1)>
                    <cfbreak>
                </cfif>
    		</cfloop>
        </cfif>
	</cfloop>
    <!--- installed --->
    <cfloop query="extensions">
    	<cfif  hash(extensions.provider) EQ arguments.hashProvider and extensions.id EQ arguments.appId>
        	<cfset detail.installed=querySlice(extensions,extensions.currentrow,1)>
            <cfbreak>
        </cfif>
    </cfloop>
    <cfreturn detail>
</cffunction>

<cffunction name="getDownloadDetails" returntype="struct" output="yes">
	<cfargument name="hashProvider" required="yes" type="string">
    <cfargument name="type" required="yes" type="string">
    <cfargument name="serverId" required="yes" type="string">
    <cfargument name="webId" required="yes" type="string">
    <cfargument name="appId" required="yes" type="string">
    <cfargument name="serialNumber" required="no" type="string">
    
   <cfset providers=request.providers>
	<cfloop query="providers">
		<cfif hash(providers.url) EQ arguments.hashProvider>
            <cfset detail.provider= request.loadCFC(providers.url)>
            <cfreturn detail.provider.getDownloadDetails(type,serverId,webId,appId,serialNumber)>
        </cfif>
	</cfloop>
    <cfreturn struct()>
</cffunction>
<cfset request.getDownloadDetails=getDownloadDetails>



<cffunction name="getDetailFromExtension" returntype="struct" output="yes">
	<cfargument name="hashProvider" required="yes" type="string">
	<cfargument name="appId" required="yes" type="string">
	<cfset var detail=struct()>
    <cfset detail.installed=false>
	<cfloop query="extensions">
		<cfif hash(extensions.provider) EQ arguments.hashProvider and  extensions.id EQ arguments.appId>
            <cfset detail.info.title="">
            <cfset detail.url=extensions.provider>
            <cfset detail.info.cfc=extensions.provider>
            <cfset detail.app=querySlice(extensions,extensions.currentrow,1)>
            <cfset detail.installed=true>
            <cfbreak>
        </cfif>
	</cfloop>
    
    <!--- installed --->
    <cfloop query="extensions">
    	<cfif  hash(extensions.provider) EQ arguments.hashProvider and extensions.id EQ arguments.appId>
        	<cfset detail.installed=querySlice(extensions,extensions.currentrow,1)>
            <cfbreak>
        </cfif>
    </cfloop>
    
    
    <cfreturn detail>
</cffunction>








<cffunction name="getProviderData" returntype="struct" output="yes">
	<cfargument name="provider" required="yes" type="string">
	<cfargument name="isHash" required="no" type="boolean" default="false">
    <cfif not isHash>
    	<cfset arguments.provider=hash(arguments.provider)>
    </cfif>
    
	<cfset var detail=struct()>
	<cfloop query="providers">
		<cfif hash(providers.url) EQ arguments.provider>
            <cfset detail.provider=loadCFC(providers.url)>
            <cfset detail.app=detail.provider.listApplications()>
            <cfset detail.info=detail.provider.getInfo()>
            <cfset detail.url=providers.url>
            <cfset detail.info.cfc=providers.url>
        </cfif>
	</cfloop>
    <cfreturn detail>
</cffunction>    

    

<!--- Action --->
<cfparam name="error" default="#struct(message:"",detail:"")#">
<cftry>

	<cfswitch expression="#form.mainAction#">
	<!--- Filter --->
		<cfcase value="#stText.Buttons.filter#">
        	<cfif StructKeyExists(form,"categoryFilter")>
				<cfset session.extFilter.category=trim(form.categoryFilter)>
                <cfset session.extFilter.name=trim(form.nameFilter)>
                <cfset session.extFilter.provider=trim(form.providerFilter)>
            <cfelse>
				<cfset session.extFilter2.category=trim(form.categoryFilter2)>
                <cfset session.extFilter2.name=trim(form.nameFilter2)>
                <cfset session.extFilter2.provider=trim(form.providerFilter2)>
            </cfif>
		</cfcase>
        <cfcase value="#stText.Buttons.install#">
        	<cfset data.hashProviders=toArrayFromForm("hashProvider")>
        	<cfset data.ids=toArrayFromForm("id")>
            <cfif StructKeyExists(form,"row") and StructKeyExists(data,"ids") and ArrayIndexExists(data.ids,row)>
            	<cflocation url="#request.self#?action=#url.action#&action2=install1&provider=#data.hashProviders[row]#&app=#data.ids[row]#" addtoken="no">
            </cfif>
		</cfcase>
        <cfcase value="#stText.Buttons.uninstall#">
        	<cfset data.hashProviders=toArrayFromForm("hashProvider")>
        	<cfset data.ids=toArrayFromForm("id")>
            <cfif StructKeyExists(form,"row") and StructKeyExists(data,"ids") and ArrayIndexExists(data.ids,row)>
            	<cflocation url="#request.self#?action=#url.action#&action2=uninstall&provider=#data.hashProviders[row]#&app=#data.ids[row]#" addtoken="no">
            </cfif>
		</cfcase>
        <cfcase value="#stText.Buttons.update#">
        	<cfset data.hashProviders=toArrayFromForm("hashProvider")>
        	<cfset data.ids=toArrayFromForm("id")>
            <cfif StructKeyExists(form,"row") and StructKeyExists(data,"ids") and ArrayIndexExists(data.ids,row)>
            	<cflocation url="#request.self#?action=#url.action#&action2=install1&provider=#data.hashProviders[row]#&app=#data.ids[row]#" addtoken="no">
            </cfif>
		</cfcase>
	</cfswitch>
<cfinclude template="#url.action#.#url.action2#.cfm"/>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>




<cfsetting enablecfoutputonly="no">
<!--- 
Error Output --->
<cfset printError(err)>
<cfset printError(error)>
