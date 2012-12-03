<cfsilent>
	<cffunction name="isInstalled">
		<cfreturn 1>
		<cfreturn RandRange(1,0)>
	</cffunction>
	
	<cffunction name="updateAvailable" output="no">
		<cfargument name="extensions" required="yes" type="query">
		<cfset var detail=getDetailByUid(createId(extensions.provider,extensions.id))>
		<cftry>
			<!--- app no longer exist --->
			<cfif arrayLen(detail.all) EQ 0><cfreturn false></cfif>
			<cfloop array="#detail.all#" index="local.item">
				<cfif extensions.version LT item.version>
					<cfreturn true>
				</cfif>
			</cfloop>
			<cfcatch></cfcatch>
		</cftry>
		<cfreturn false>
	</cffunction>
	
	
	<cffunction name="createId" output="no">
		<cfargument name="provider" required="yes" type="string">
		<cfargument name="extensionId" required="yes" type="string">
		<cfif isValid("uuid",arguments.extensionId)>
			<cfreturn hash(arguments.extensionId)>
		</cfif>
		<cfreturn hash(arguments.provider&arguments.extensionId)>
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
	
	
	<cffunction name="getDetailByUid" returntype="struct" output="yes">
		<cfargument name="uid" required="yes" type="string">
		<cfset var detail={}>
		<cfset detail.all=[]>
		<cfset var tmp="">
		<cfif isQuery(data)><cfloop query="data">
			<cfif data.uid EQ uid>
				<cfset tmp=querySlice(data,data.currentrow,1)>
				<cfset ArrayAppend(detail.all,tmp)>
				<cfif not structKeyExists(detail,"data") or detail.data.version LT tmp.version>
					<cfset detail.data=tmp>
				</cfif>
			</cfif>
		</cfloop></cfif>
		
		<!--- installed --->
		<cfloop query="extensions">
			<cfif createId(extensions.provider,extensions.id) EQ uid>
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
	
	
	<cffunction name="getDumpNail" returntype="string" output="no">
		<cfargument name="imgUrl" required="yes" type="string">
		<cfargument name="width" required="yes" type="number" default="80">
		<cfargument name="height" required="yes" type="number" default="40">
		<cftry>
			<cfif not len(trim(arguments.imgURL))><cfreturn ""></cfif>
			
			<cfset var id=hash(arguments.imgURL&"-"&width&"-"&height)>
			<cfparam name="application.railodumps" default="#{}#">
			<cfif not structKeyExists(application.railodumps,id)>
				<cfset application.railodumps[id]="">
				<cfset var data="">
				<cfset img="">
				<cffile action="readbinary" file="#arguments.imgURL#" variable="data">
				<cfimage action="read" source="#data#" name="img">
				
				
				<cfif img.height GT height or img.width GT width>
					<cfif img.height GT height >
						<cfimage action="resize" source="#img#" height="#height#" name="img">
					</cfif>
					<cfif img.width GT width>
						<cfimage action="resize" source="#img#" width="#width#" name="img">
					</cfif>
					<cfset data=toBase64(img)>
				<cfelse>
					<cfset data=toBase64(data)>
				</cfif>
				<cfset var mimetypes={png:'png',gif:'gif',jpg:'jpeg'}>
				<cfset ext=listLast(arguments.imgURL,'.')>
				<cfset application.railodumps[id]="data:image/#mimetypes[ext]#;base64,#trim(data)#">
			</cfif>
			<cfreturn application.railodumps[id]>
			<cfcatch>
				<cfreturn "">
			</cfcatch>
		</cftry>
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


<cffunction name="getData" output="no">
	<cfargument name="providers">
	<cfargument name="err">
	<cfset var data="">
    <cfloop query="providers">
        <cftry>
            <cfset local.provider=loadCFC(providers.url)>
            <cfset local._apps=provider.listApplications()>
            <cfset local._info=provider.getInfo()>
            <cfset local._url=providers.url>
            <cfif IsSimpleValue(data)>
                <cfset data=queryNew(_apps.columnlist&",provider,info,uid")>
            </cfif>
            
            <!--- check if all column exists --->
            <cfloop list="#_apps.columnlist#" index="local.col">
                <cfif not queryColumnExists(data,col)><cfset QueryAddColumn(data,col,array())></cfif>
            </cfloop>
            
            <cfloop query="_apps">
                <cfset QueryAddRow(data)>
                <cfloop list="#_apps.columnlist#" index="col">
                    <cfset data[col][data.recordcount]=_apps[col]>
                </cfloop>
                <cfset data.provider[data.recordcount]=_url>
                <cfset data.info[data.recordcount]=_info>
                <cfset data.uid[data.recordcount]=createId(_url,_apps.id)>
            </cfloop>
            
            
            <cfcatch>
                <cfif len(err.message)>
                    <cfset err.message&="<br>can't load provider [#providers.url#]">
                <cfelse>
                    <cfset err.message="can't load provider [#providers.url#]">
                </cfif>
            </cfcatch>
        </cftry>
    </cfloop>
    <cfif isQuery(data)><cfset querySort(query:data,names:"name,uid,category")></cfif>
    <cfreturn data>
</cffunction>


</cfsilent>