<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
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
			<cfset dotNotUpper=true>
			<cfif isDefined('form.dotNotation') and form.dotNotation EQ "oc">
            	<cfset dotNotUpper=false>
            </cfif>
            <cfif not isDefined('form.supressWSBeforeArg')>
            	<cfset form.supressWSBeforeArg=false>
            </cfif>
            
			<cfadmin 
				action="updateCompilerSettings"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				dotNotationUpperCase="#dotNotUpper#"
                supressWSBeforeArg="#form.supressWSBeforeArg#"
				remoteClients="#request.getRemoteClients()#">
	
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			
			<cfadmin 
				action="updateCompilerSettings"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				dotNotationUpperCase=""
				supressWSBeforeArg=""
				
				remoteClients="#request.getRemoteClients()#">
	
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- 
Error Output --->
<cfset printError(error)>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>




<cfadmin 
	action="getCompilerSettings"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="setting">


<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>

<cfoutput>
	<div class="pageintro">#stText.setting.compiler#</div>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<!--- Supress Whitespace --->
				<tr>
					<th scope="row">#stText.setting.dotNotation#</th>
					<td>
						<cfif hasAccess>
							<select name="dotNotation">
								<option value="uc"<cfif setting.dotNotationUpperCase> selected="selected"</cfif>>#stText.setting.dotNotationUpperCase#</option>
								<option value="oc"<cfif !setting.dotNotationUpperCase> selected="selected"</cfif>>#stText.setting.dotNotationOriginalCase#</option>
							</select>
						<cfelse>
							<b>#(setting.dotNotationUpperCase)?stText.setting.dotNotationUpperCase:stText.setting.dotNotationOriginalCase#</b>
							<input type="hidden" name="dotNotation" value="#setting.dotNotationUpperCase?'uc':'oc'#">
						</cfif>
						<div class="comment">#replace(stText.setting.dotNotationDesc, server.separator.line, '<br />', 'all')#</div>
					</td>
				</tr>
				<!--- Supress Whitespace in front of cfargument --->
				<tr>
					<th scope="row">#stText.setting.supressWSBeforeArg#</th>
					<td>
						<cfif hasAccess>
        					<input type="checkbox" name="supressWSBeforeArg" value="true" <cfif setting.supressWSBeforeArg>checked="checked"</cfif> />
						<cfelse>
							<b>#yesNoFormat(setting.supressWSBeforeArg)#</b><br /><input type="hidden" name="supressWSBeforeArg" value="#setting.supressWSBeforeArg#">
						</cfif>
						<div class="comment">#stText.setting.supressWSBeforeArgDesc#</div>
					</td>
				</tr>

				<cfif hasAccess>
					<cfmodule template="remoteclients.cfm" colspan="2">
				</cfif>
			</tbody>
			<cfif hasAccess>
				<tfoot>
					<tr>
						<td colspan="2">
							<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
							<input type="reset" class="button reset" name="cancel" value="#stText.Buttons.Cancel#">
							<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>
</cfoutput>