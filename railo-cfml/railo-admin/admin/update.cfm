<cfsetting showdebugoutput="no">

<cfset adminType=url.adminType>
<cfset password=session["password"&adminType]>
<cfset id="rai:"&hash(adminType&":"&password)>
<cfif not structKeyExists(session,id)>
	<cfset session[id]={}>
</cfif>
<cftry>
<cfif not structKeyExists(session[id],"content") or not structKeyExists(session[id],"last") or DateDiff("m",session[id].last,now()) GT 5> 

<cfinclude template="web_functions.cfm">

<cfset self = adminType & ".cfm">
<cfset stText.services.update.update="A patch {avaiable} is available for your current version {current}.">

<!--- Core --->
<cfif adminType eq "server">
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

<cfsavecontent variable="ext" trim="true"><cfoutput><cfloop query="#extensions#">
	<cfif !updateAvailable(extensions)><cfcontinue></cfif>
		<cfset uid=createId(extensions.provider,extensions.id)>
		<cfset link="">
        <cfset dn="">
        <cfset link="#self#?action=extension.applications&action2=detail&uid=#uid#">
    		<a href="#link#" style="text-decoration:none;">- #extensions.label#</a><br />
            
	
</cfloop></cfoutput></cfsavecontent>
</cfif>

<cfsavecontent variable="content">
<cfoutput>
<cfif adminType eq "server" or extensions.recordcount NEQ 0>
<table class="tbl" width="100%">

<tr>
	<td><h2>Update Info</h2></td>
</tr>
<cfif adminType eq "server">
<tr>
	<td class="tblContent">
    <b><a href="server.cfm?action=services.update" style="text-decoration:none;">Core</a></b><br />
<cfif hasUpdate>
	#replace(replace(replace(stText.services.update.update,'{available}','<b>(#avi#)</b>'),'{current}','<b>(#curr#)</b>'),'{avaiable}','<b>(#avi#)</b>')#<br />
<cfelse>
	Your core is up to date!
</cfif>
    </td>
</tr>
</cfif>
<cfif extensions.recordcount NEQ 0>
<tr>
	<td class="tblContent">
    <b><a href="#self#?action=extension.applications" style="text-decoration:none;">Extensions</a></b><br />
<cfif len(ext)>
       There are some updates available for your installed Extensions.<br />
	   #ext#
<cfelse>
	All your Extensions are up to date!
</cfif>
    </td>
</tr>
</cfif>
</table>
</cfif>
</cfoutput>
</cfsavecontent>
	<cfset session[id].content=content>
    <cfset session[id].last=now()> 
<cfelse>
	<cfset content=session[id].content>
</cfif>

<cfoutput>#content#</cfoutput>
	<cfcatch></cfcatch>
</cftry>