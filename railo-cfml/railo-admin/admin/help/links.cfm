<cfsilent>
	<cfset request.bLoggedIn = false>
	<cfif request.adminType eq "web">
		<cfset request.bLoggedIn = structKeyExists(session, "passwordweb")>
	<cfelse>
		<cfset request.bLoggedIn = structKeyExists(session, "passwordserver")>
	</cfif>
	<cfif len(trim(cgi.query_string))>
		<cfset sHelpKey = cgi.script_name & "?" & cgi.query_string>
	<cfelse>
		<cfset sHelpKey = cgi.script_name>
	</cfif>
	<cfset sVideo  = "">
	<cfset sVideoDescription = "">
	<cfset sGlobal = "">
	<cfif request.bLoggedIn>
		<cfset sLocal = Hash(sHelpKey)>
		<cfset sLoggedIn = "">
	<cfelse>
		<cfset sLocal    = Hash(sHelpKey) & "lo">
		<cfset sLoggedIn = "?loggedout=true">
	</cfif>
	<!--- if New help was submitted, store it --->
	<cfif structKeyExists(form, "addHelp")>
		<cfset saveHelp(sLocal)>
	</cfif>
	<cfset sContent = getHelp(sLocal)>
	
	<!--- now set the texts --->
	<cfset stText = caller.stText>
</cfsilent>

<cfoutput>
<table width="185" height="82" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="8" align="right" valign="top"><a href="#other#.cfm"><cfmodule template="../img.cfm" src="#ad#-to.png" vspace="5" /></td>
	</tr>
<!---	This is important for the documentation extensions 	
<table width="185" height="112" border="0" cellpadding="0" cellspacing="0" style="background-image: url(<cfmodule type="css" template="img.cfm" src="toolbar_bg_flip.png" />); background-repeat: no-repeat; background-position: bottom right;margin-bottom:-30px">
	<tr>
		<td colspan="8" align="right"><a href="#other#.cfm"><cfmodule template="../img.cfm" src="#ad#-to.png" vspace="5" /></td>
	</tr>
	<tr>
		<td width="42">&nbsp;</td>
		<td valign="bottom" align="center" width="35">
			<cfset stTmp = getHelpStruct(sHelpKey & sLoggedIn, "video")>
			<cfif structCount(stTmp) neq 0>
				<cfset sHint = stText.help.videohelp>
				<cfif structKeyExists(stTmp, "notFound")>
					<cfset sHint &= Chr(13) & stTmp.notFound>
				</cfif>
				<!--- get the language specific help --->
				<a href="##movie" name="modal" title="#sHint#" alt="#sHint#"><cfmodule template="../img.cfm" src="movie.png"></a>
				<cfset sVideo            = "http://www.getrailo.org/help/videos/" & stTmp.content.video>
				<cfset sVideoDescription = stTmp.content.description>
			<cfelse>
				<cfset sHint = stText.help.videohelpnotavailable>
				<cfmodule template="../img.cfm" src="movie_inactive.png" alt="#sHint#" title="#sHint#">
			</cfif>
		</td>
		<td valign="bottom" align="center" width="1"><cfmodule template="../img.cfm" src="icon_toolbar_line.png" width="1"></td>
		<td valign="bottom" align="center" width="35">
			<cfset stTmp = getHelpStruct(sHelpKey & sLoggedIn, "html")>
			<cfif structCount(stTmp) neq 0>
				<cfset sHint = stText.help.globalhelp>
				<cfif structKeyExists(stTmp, "notFound")>
					<cfset sHint &= Chr(13) & stTmp.notFound>
				</cfif>
				<a href="##global" name="modal" title="#sHint#" alt="#sHint#"><cfmodule template="../img.cfm" src="help_global.png"></a>
				<cfset sGlobal = "http://www.getrailo.org/help/html/" & stTmp.content>
			<cfelse>
				<cfset sHint = stText.help.globalhelpnotavailable>
				<cfmodule template="../img.cfm" src="help_global_inactive.png" alt="#sHint#" title="#sHint#">
			</cfif>
		</td>
		<cfif request.bLoggedIn>
			<td valign="bottom" align="center" width="1"><cfmodule template="../img.cfm" src="icon_toolbar_line.png" width="1"></td>
			<td valign="bottom" align="center" width="35">
				<cfif len(trim(sContent))>
					<cfset sHint = stText.help.localHelp>
					<a href="##localHelp" title="#sHint#" alt="#sHint#" name="modal"><cfmodule template="../img.cfm" src="note.png"></a>
				<cfelse>
					<cfset sHint = stText.help.addLocalHelp>
					<a href="##localHelp" title="#sHint#" alt="#sHint#" name="modal"><cfmodule template="../img.cfm" src="note_empty.png"></a>
				</cfif>
			</td>
		</cfif>
		<td valign="bottom" align="center" width="1"><cfmodule template="../img.cfm" src="icon_toolbar_line.png" width="1"></td>
		<td valign="bottom" align="center" width="35">
			<cfset sHint = stText.help.logout>
			<cfif request.bLoggedIn>
				<a href="#cgi.script_name#?action=logout" title="#sHint#" alt="#sHint#"><cfmodule template="../img.cfm" src="logout.png"></a>
			<cfelse>
				<cfmodule template="../img.cfm" src="logout_inactive.png">
			</cfif>
		</td>
	</tr>
</table>
<cfinclude template="boxes.cfm">
			--->
</table>
</cfoutput>

<!--- Funktionen zuoberst --->

<cffunction name="saveHelp" output="No">
	<cfargument name="sKey" required="Yes">
	<cfset var aContent = []>
	<cfset var iElement = 0>
	<cftry>
		<cfadmin 
			action="storageGet"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			key="#sKey#"
			returnVariable="aContent">
		<cfcatch>
			<cfset aContent = []>
		</cfcatch>
	</cftry>
	<cfloop from="#arrayLen(aContent)#" to="1" step="-1" index="iElement">
		<cfif structKeyExists(form, "del_" & aContent[iElement].sID)>
			<cfset ArrayDeleteAt(aContent, iElement)>
		</cfif>
	</cfloop>
	<cfif len(trim(form.addLocalHelp))>
		<cfset arrayAppend(aContent, {date:now(), content:form.addLocalHelp, sID:arrayLen(aContent)+1})>
	</cfif>
	<cfadmin 
		action="storageSet"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		value="#aContent#"
		key="#sKey#">
</cffunction>


<cffunction name="getHelp" output="No" returntype="string">
	<cfargument name="sKey" required="Yes">
	<cfset var aContent  = []>
	<cfset var stElement = "">
	<cfset var sContent  = "">
	<cftry>
		<cfadmin 
			action="storageGet"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			key="#sKey#"
			returnVariable="aContent">
		<cfcatch>
			<cfset aContent = []>
		</cfcatch>
	</cftry>
	<cfsavecontent variable="sContent">
		<cfloop array="#aContent#" index="stElement">
			<cfoutput>
			<h2>#lsDateFormat(stElement.date)# #lsTimeFormat(stElement.date)#</h2>
			#stElement.content#
			<div style="float:right"><input src="resources/img/delete.png" type="image" border="0" alt="delete entry" title="delete entry" name="del_#stElement.sID#" id="del_#stElement.sID#"></div>
			<hr>
			</cfoutput>
		</cfloop>
	</cfsavecontent>
	<cfreturn sContent>
</cffunction>

<cffunction name="getHelpStruct" returntype="struct" output="No">
	<cfargument name="sKey" required="Yes">
	<cfargument name="sType" required="Yes">
	<cfset var stRet = {}>
	<cfset var stTmp = request.stWebHelp.helpfiles>
	<cfif structKeyExists(stTmp, sKey)>
			<!--- get the language specific help --->
		<cfif structKeyExists(stTmp[sKey], session.railo_admin_lang)>
			<cfset local.sLang = session.railo_admin_lang>
		<cfelseif structKeyExists(stTmp[sKey], "en")>
			<!--- english is default --->
			<cfset local.sLang = "en">
			<cfset stRet.notFound = stText.help[sType].languageHelpNotAvailable>
		</cfif>
		<!--- return the content --->
		<cfif arguments.sType eq "video">
			<cfset stRet.content = {video:stTmp[sKey][sLang].video,
									description:stTmp[sKey][sLang].description}>
		<cfelse>
			<cfset stRet.content = stTmp[sKey][sLang].help>
		</cfif>
	</cfif>
	<cfreturn stRet>
</cffunction>