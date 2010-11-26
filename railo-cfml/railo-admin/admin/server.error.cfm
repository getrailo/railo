<cfset error.message="">
<cfset error.detail="">

<script language="JavaScript">
	function disableField(oField,statusCode) {
		var oForm=oField.form;
		
		if (oField.value == 'Select') {
			oForm["errorTemplate_File"+statusCode].disabled   = true;
			oForm["errorTemplate_Select"+statusCode].disabled = false;
		} 
		else {
			oForm["errorTemplate_File"+statusCode].disabled   = false;
			oForm["errorTemplate_Select"+statusCode].disabled = true;
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
				action="updateError"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				template500="#form["errorTemplate_"&form.errtype500&500]#"
				template404="#form["errorTemplate_"&form.errtype404&404]#"
				statuscode="#isDefined('form.doStatusCode')#"
				
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
		
			<cfadmin 
				action="updateError"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
                template500=""
				template404=""
				statuscode=""
				
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
	action="getError"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="err">


<cfoutput>

<!--- 
Create Datasource --->
<table class="tbl" width="740">
<tr>
	<td colspan="2">#stText.err.descr#</td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
<cfloop list="500,404" index="statusCode">
<tr>
	<td class="tblHead" width="150" height="28">#stText.err.errorTemplate[statusCode]#</td>
	<cfset css=iif(len(err.templates[statusCode]) EQ 0 and len(err.templates[statusCode]) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" title="#err.str[statusCode]#
#err.str[statusCode]#">
		<span class="comment">#stText.err.errorTemplateDescription[statusCode]#</span><br>
		<cfif LCase(left(err.templates[statusCode], 41)) eq "#cgi.context_path#/railo-context/admin/templates/error/">
			<cfset bDisableFile = True>
		<cfelse>
			<cfset bDisableFile = False>
		</cfif>
		<cfif hasAccess>
			<cfsilent>		
			</cfsilent>
				<cfif structKeyExists(session,"passwordserver")>
						<cfdirectory action="LIST" directory="../templates/error/" name="err_templates" serverpassword="#session.passwordserver#">
				<cfelse>
					<cftry>
						<cfdirectory action="LIST" directory="../templates/error/" name="err_templates">
						<cfcatch type="security">
							<cfadmin 
								action="getErrorList"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								returnVariable="err_templates">			
						</cfcatch>		
					</cftry>
				</cfif>
				<cfset isFromTemplate=false>
				<cfset path=GetDirectoryFromPath(mid(GetDirectoryFromPath(cgi.SCRIPT_NAME),1,len(GetDirectoryFromPath(cgi.SCRIPT_NAME))-1))>
				
				<cfloop query="err_Templates">
					<cfif err.templates[statusCode] EQ expandPath(path&"templates/error/" & err_Templates.Name)>
						<cfset isFromTemplate=true>
					</cfif>
				</cfloop>
			<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td><input type="radio" class="radio" name="errType#statusCode#" value="Select" onclick="disableField(this,#statusCode#)" <cfif isFromTemplate>checked</cfif>></td>
				<td>
					
					<select name="errorTemplate_Select#statusCode#" id="errorTemplate_Select#statusCode#" <cfif not isFromTemplate>disabled</cfif>>
						<cfloop query="err_Templates">
							<cfif mid(err_Templates.Name,1,1) EQ "." or err_Templates.type EQ "dir"><cfcontinue></cfif>
							<cfset sName = path&"templates/error/" & err_Templates.Name>
							<option value="#sName#"<cfif expandPath(sName) eq err.templates[statusCode]> selected</cfif>>#err_Templates.Name#</option>
						</cfloop>
					</select>
				</td>
			</tr>
			<tr>
				<td><input type="radio" class="radio" name="errType#statusCode#" value="File" onclick="disableField(this,#statusCode#)" <cfif not isFromTemplate>checked</cfif>></td>
				<td><input type="text" name="errorTemplate_File#statusCode#" value="#err.str[statusCode]#" id="errorTemplate_File[statusCode]" <cfif isFromTemplate>disabled</cfif>
					style="width:450px"></td>
			</tr>
			</table>
		<cfelse>
			<b>#err.str[statusCode]#</b>
			<input type="hidden" name="errorTemplate#statusCode#" value="#err.str[statusCode]#">
		</cfif>
	</td>
</tr>
</cfloop>


<tr>
	<td class="tblHead" width="150">#stText.err.errorStatusCode#</td>
	<td class="tblContent">
		<cfif hasAccess>
		<input class="checkbox" type="checkbox" class="checkbox" name="doStatusCode" value="yes" <cfif err.doStatusCode>checked</cfif>>
		<cfelse>
		<b>#YesNoFormat(err.doStatusCode)#</b><br />
		</cfif>
		<span class="comment">#stText.err.errorStatusCodeDescription#</span><br>
      	
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