<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="getCharset"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="charset">

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
					action="updateCharset"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					templateCharset="#form.templateCharset#"
					webCharset="#form.webCharset#"
					resourceCharset="#form.resourceCharset#"
					remoteClients="#request.getRemoteClients()#">
			
			</cfcase>
			<!--- reset to server setting --->
			<cfcase value="#stText.Buttons.resetServerAdmin#">
				
				<cfadmin 
					action="updateCharset"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					templateCharset=""
					webCharset=""
					resourceCharset=""
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
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>

<cfoutput>
	<cfif not hasAccess>
		<cfset noAccess(stText.setting.noAccess)>
	</cfif>
	
	<div class="pageintro">#stText.charset[request.adminType]#</div>
	
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<!--- Template --->
				<tr>
					<th scope="row">#stText.charset.templateCharset#</th>
					<td>
						<cfif hasAccess>
							<input type="text" class="small" name="templateCharset" value="#charset.templateCharset#" />
						<cfelse>
							<input type="hidden" name="templateCharset" value="#charset.templateCharset#">
							<b>#charset.templateCharset#</b>
						</cfif>
						<div class="comment">#stText.charset.templateCharsetDescription#</div>
					</td>
				</tr>
				
				<!--- Web --->
				<tr>
					<th scope="row">#stText.charset.webCharset#</th>
					<td>
						<cfif hasAccess>
							<input type="text" class="small" name="webCharset" value="#charset.webCharset#">
						<cfelse>
							<input type="hidden" name="webCharset" value="#charset.webCharset#">
							<b>#charset.webCharset#</b>
						</cfif>
						<div class="comment">#stText.charset.webCharsetDescription#</div>
					</td>
				</tr>
				
				<!--- Resource --->
				<tr>
					<th scope="row">#stText.charset.resourceCharset#</th>
					<td>
						<cfif hasAccess>
							<input type="text" class="small" name="resourceCharset" value="#charset.resourceCharset#">
						<cfelse>
							<input type="hidden" name="resourceCharset" value="#charset.resourceCharset#">
							<b>#charset.resourceCharset#</b>
						</cfif>
						<div class="comment">#stText.charset.resourceCharsetDescription#</div>
					</td>
				</tr>
			</tbody>
			<cfif hasAccess>
				<tfoot>
					<cfmodule template="remoteclients.cfm" colspan="2">
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