<cfset error.message="">
<cfset error.detail="">

<script language="JavaScript">
	function disableField(oField) {
		var oForm=oField.form;
		
		if (oField.value == 'Select') {
			oForm["debugTemplate_File"].disabled   = true;
			oForm["debugTemplate_Select"].disabled = false;
		} 
		else {
			oForm["debugTemplate_File"].disabled   = false;
			oForm["debugTemplate_Select"].disabled = true;
		}
	}
</script>

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="debugging"
	secValue="yes">

<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
			<cfadmin 
				action="updateDebug"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				debug="#form.debug#"
				debugTemplate="#form["debugTemplate_"&form.debugtype]#"
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfadmin 
				action="updateDebug"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
                debug=""
				debugTemplate=""
                
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>


<cfadmin 
	action="getDebug"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="debug">


<!--- @todo text ---><br><br>
<!--- 
Create Datasource --->
<table class="tbl">
<cfoutput><cfform action="#request.self#?action=#url.action#" method="post" name="debug_settings">
<tr>
	<td class="tblHead" width="150">#stText.Debug.EnableDebugging#</td>
	<td class="tblContent" height="28">
		<cfset lbl=iif(debug.debug,de(stText.general.yes),de(stText.general.no))>
	
		<span class="comment">#stText.Debug.EnableDescription#</span><br />
		<cfif hasAccess>
			<select name="debug">
				<cfif request.admintype EQ "web">
					<option #iif(debug.debugsrc EQ "server",de('selected'),de(''))# value="">#stText.Regional.ServerProp[request.adminType]# <cfif debug.debugsrc EQ "server">(#lbl#) </cfif></option>
					<option #iif(debug.debugsrc EQ "web" and debug.debug,de('selected'),de(''))# value="true">#stText.general.yes#</option>
					<option #iif(debug.debugsrc EQ "web" and not debug.debug,de('selected'),de(''))# value="false">#stText.general.no#</option>
				<cfelse>
					<option #iif(debug.debug,de('selected'),de(''))# value="true">#stText.general.yes#</option>
					<option #iif(debug.debug,de(''),de('selected'))# value="false">#stText.general.no#</option>
				</cfif>
			</select>
		
			<!--- <input type="checkbox" class="checkbox" name="debug" value="yes" <cfif debug.debug>checked</cfif>>--->
		<cfelse>
			<b>#lbl#</b> <input type="hidden" name="debug" value="#debug.debug#">
		</cfif>
		
		
	</td>
</tr> 
<tr>
	<td class="tblHead" width="150" height="28">#stText.Debug.DebugTemplate#</td>
	<cfset css=iif(len(debug.debugTemplate) EQ 0 and len(debug.strdebugTemplate) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" title="#debug.strDebugTemplate#
#debug.DebugTemplate#">
		<span class="comment">#stText.Debug.DebugTemplateDescription#</span><br>
		<cfif LCase(left(debug.debugTemplate, 41)) eq "#cgi.context_path#/railo-context/admin/templates/debugging/">
			<cfset bDisableFile = True>
		<cfelse>
			<cfset bDisableFile = False>
		</cfif>
		<cfif hasAccess>
			<cfsilent>		
				<cfif structKeyExists(session,"passwordserver")>
						<cfdirectory action="LIST" directory="../templates/debugging/" name="debug_templates" serverpassword="#session.passwordserver#">
				<cfelse>
					<cftry>
						<cfdirectory action="LIST" directory="../templates/debugging/" name="debug_templates">
						<cfcatch type="security">
							<cfadmin 
								action="getDebuggingList"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								returnVariable="debug_templates">			
						</cfcatch>		
					</cftry>
				</cfif>
				<cfset isFromTemplate=false>
				<cfset path=GetDirectoryFromPath(mid(GetDirectoryFromPath(cgi.SCRIPT_NAME),1,len(GetDirectoryFromPath(cgi.SCRIPT_NAME))-1))>
				<cfloop query="debug_Templates">
					<cfif debug.debugTemplate EQ expandPath(path&"templates/debugging/" & debug_Templates.Name)>
						<cfset isFromTemplate=true>
					</cfif>
				</cfloop>
			</cfsilent>
			<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td><input type="radio" class="radio" name="DebugType" value="Select" onclick="disableField(this)" <cfif isFromTemplate>checked</cfif>></td>
				<td>
					
					<select name="debugTemplate_Select" id="debugTemplate_Select" <cfif not isFromTemplate>disabled</cfif>>
						<cfloop query="debug_Templates">
							<cfif mid(debug_Templates.Name,1,1) EQ "."><cfcontinue></cfif>
							<cfset sName = path&"templates/debugging/" & debug_Templates.Name>
							<option value="#sName#"<cfif expandPath(sName) eq debug.debugTemplate> selected</cfif>>#debug_Templates.Name#</option>
						</cfloop>
					</select>
				</td>
			</tr>
			<tr>
				<td><input type="radio" class="radio" name="DebugType" value="File" onclick="disableField(this)" <cfif not isFromTemplate>checked</cfif>></td>
				<td><input type="text" name="debugTemplate_File" value="#debug.strdebugTemplate#" id="debugTemplate_File" <cfif isFromTemplate>disabled</cfif>
					style="width:450px"></td>
			</tr>
			</table>
		<cfelse>
			<b>#debug.debugTemplate#</b>
			<input type="hidden" name="debugTemplate" value="#debug.strDebugTemplate#">
		</cfif>
	</td>
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>
<br><br>