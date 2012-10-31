<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="getOutputSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="setting">

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

<cfif hasAccess>
	<cftry>
		<cfswitch expression="#form.mainAction#">
		<!--- UPDATE --->
			<cfcase value="#stText.Buttons.Update#">
				
				<cfadmin 
					action="updateOutputSetting"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					suppressWhiteSpace="#isDefined('form.suppressWhitespace') and form.suppressWhitespace#"
					suppressContent="#isDefined('form.suppressContent') and form.suppressContent#"
					allowCompression="#isDefined('form.allowCompression') and form.allowCompression#"
					contentLength=""
					remoteClients="#request.getRemoteClients()#">
		
			</cfcase>
		<!--- reset to server setting --->
			<cfcase value="#stText.Buttons.resetServerAdmin#">
				
				<cfadmin 
					action="updateOutputSetting"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					suppressWhiteSpace=""
					suppressContent=""
					showVersion=""
					allowCompression=""
					contentLength=""
					
					remoteClients="#request.getRemoteClients()#">
		
			</cfcase>
		</cfswitch>
		<cfcatch>
			<cfset error.message=cfcatch.message>
			<cfset error.detail=cfcatch.Detail>
		</cfcatch>
	</cftry>
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>


<cfif not hasAccess>
	<cfset noAccess(stText.setting.noAccess)>
</cfif>

<cfoutput>
	<div class="pageintro">
		#stText.setting[request.adminType]#
	</div>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<!--- Supress Whitespace --->
				<tr>
					<th scope="row">#stText.setting.whitespace#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" name="suppressWhitespace" class="checkbox" value="true" <cfif setting.suppressWhitespace>checked="checked"</cfif>>
						<cfelse>
							<b>#yesNoFormat(setting.suppressWhitespace)#</b>
							<!---<input type="hidden" name="suppressWhitespace" value="#setting.suppressWhitespace#">--->
						</cfif>
						<div class="comment">#stText.setting.whitespaceDescription#</div>
					</td>
				</tr>
				<!--- Allow Compression --->
				<tr>
					<th scope="row">#stText.setting.AllowCompression#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" name="AllowCompression" class="checkbox" value="true" <cfif setting.AllowCompression>checked="checked"</cfif>>
						<cfelse>
							<b>#iif(setting.AllowCompression,de('Yes'),de('No'))#</b>
							<!---<input type="hidden" name="AllowCompression" value="#setting.AllowCompression#">--->
						</cfif>
						<div class="comment">#stText.setting.AllowCompressionDescription#</div>
					</td>
				</tr>

				<cfset stText.setting.suppressContent="Supress Content for CFC Remoting">
				<cfset stText.setting.suppressContentDescription="Suppress content written to response stream when a Component is invoked remotely. Only work when content not was flushed before.">
				<!--- Supress Content when CFC Remoting --->
				<tr>
					<th scope="row">#stText.setting.suppressContent#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" name="suppressContent" class="checkbox" value="true" <cfif setting.suppressContent>checked="checked"</cfif>>
						<cfelse>
							<b>#iif(setting.suppressContent,de('Yes'),de('No'))#</b>
							<!---<input type="hidden" name="suppressContent" value="#setting.suppressContent#">--->
						</cfif>
						<div class="comment">#stText.setting.suppressContentDescription#</div>
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
							<input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.Update#">
							<input class="button reset" type="reset" name="cancel" value="#stText.Buttons.Cancel#">
							<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>
</cfoutput>