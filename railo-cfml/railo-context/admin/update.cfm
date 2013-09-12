<cfsetting showdebugoutput="false" enablecfoutputonly="true">

<cfparam name="session.alwaysNew" default="false" type="boolean">


<cffunction name="getAviableVersion" output="false">
	<cfargument name="update">
	<cfset var http="">
	<cftry>
		<cfhttp 
			url="#update.location#/railo/remote/version/Info.cfc?method=getpatchversionfor&level=#server.ColdFusion.ProductLevel#&version=#server.railo.version#" 
			method="get" resolveurl="no" result="http">
		<cfwddx action="wddx2cfml" input="#http.fileContent#" output="local.wddx">
		<cfset session.avaiableVersion=wddx>
		<cfreturn session.avaiableVersion>
		<cfcatch>
			<cfreturn "">
		</cfcatch>
	</cftry>
</cffunction>

<cftry>

	<cfset adminType=url.adminType>
	<cfset password=session["password"&adminType]>
	<cfset id="rai:"&hash(adminType&":"&password)>
	<cfif not structKeyExists(session,id)>
		<cfset session[id]={}>
	</cfif>

	<cfif !structKeyExists(session[id],"content") 
		|| !structKeyExists(session[id],"last") 
		|| DateDiff("m",session[id].last,now()) GT 5
		|| session.alwaysNew>
		<cfinclude template="web_functions.cfm">
		
		<cfset self = adminType & ".cfm">
		<cfset stText.services.update.update="A patch <b>({available})</b> is available for your current version <b>({current})</b>.">

		<!--- Core --->
		<cfif adminType == "server">
			<cfadmin 
				action="getUpdate"
				type="#adminType#"
				password="#password#"
				returnvariable="update">
			<cfset curr=server.railo.version>
			<cfset avi=getAviableVersion(update)>
			<cfset hasUpdate=curr LT avi>
		</cfif>

		<!--- Extensions --->
		<cfparam name="err" default="#struct(message:"",detail:"")#">
		<cfinclude template="extension.functions.cfm">
		<cfadmin 
			action="getExtensions"
			type="#adminType#"
			password="#password#"
			returnVariable="extensions"><!--- #session["password"&url.adminType]# --->
		<cfif extensions.recordcount GT 0>
			<cfadmin 
				action="getExtensionProviders"
				type="#adminType#"
				password="#password#"
				returnVariable="providers">
			<cfset data=getData(providers,err)>

			<cfsavecontent variable="ext" trim="true">
				<cfloop query="extensions">
					<cfif !updateAvailable(extensions)>
						<cfcontinue>
					</cfif>
					<cfset uid=createId(extensions.provider,extensions.id)>
					<cfset link="">
					<cfset dn="">
					<cfset link="#self#?action=extension.applications&action2=detail&uid=#uid#">
					<cfoutput>
						<a href="#link#" style="text-decoration:none;">- #extensions.label#</a><br>
					</cfoutput>
				</cfloop>
			</cfsavecontent>
		</cfif>

		<cfsavecontent variable="content" trim="true">
			<cfoutput>
				<cfif adminType == "server">
					<h3>
						<a href="server.cfm?action=services.update" style="text-decoration:none;">Core</a>
					</h3>
					<div class="comment">
						<cfif hasUpdate>
							#replace( stText.services.update.update, { '{available}': avi, '{current}': curr } )#<br>
						<cfelse>
							Your core is up to date!
						</cfif>
					</div>
				</cfif>

				<h3>
					<a href="#self#?action=extension.applications" style="text-decoration:none;">Extensions</a>
				</h3>
				<div class="comment">
					<cfif not extensions.recordcount>
						You have no extensions installed yet.
					<cfelseif len(ext)>
					   There are some updates available for your installed Extensions.<br>
					   #ext#
					<cfelse>
						All your Extensions are up to date!
					</cfif>
				</div>
			</cfoutput>
		</cfsavecontent>
		<cfset session[id].content=content>
		<cfset session[id].last=now()> 
	<cfelse>
		<cfset content=session[id].content>
	</cfif>

	<cfoutput>#content#</cfoutput>
	
	<cfcatch>
		<cfoutput>
			<div class="error">
				Failed to retrieve update information:
				#cfcatch.message# #cfcatch.detail#
			</div>
		</cfoutput>
	</cfcatch>
</cftry>