<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
	secValue="yes">

<cfadmin 
	action="getScope"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="scope">

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
				action="updateScope"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				sessionType="#form.sessionType#"
				localMode="#form.localMode#"
				scopeCascadingType="#form.scopeCascadingType#"
				allowImplicidQueryCall="#isDefined("form.allowImplicidQueryCall") and form.allowImplicidQueryCall#"
				mergeFormAndUrl="#isDefined("form.mergeFormAndUrl") and form.mergeFormAndUrl#"
				
				
				
				clientTimeout="#CreateTimeSpan(form.client_days,form.client_hours,form.client_minutes,form.client_seconds)#"
				sessionTimeout="#CreateTimeSpan(form.session_days,form.session_hours,form.session_minutes,form.session_seconds)#"
				applicationTimeout="#CreateTimeSpan(form.application_days,form.application_hours,form.application_minutes,form.application_seconds)#"
				sessionManagement="#isDefined("form.sessionManagement") and form.sessionManagement#"
				clientManagement="#isDefined("form.clientManagement") and form.clientManagement#"
				clientCookies="#isDefined("form.clientCookies") and form.clientCookies#"
				domaincookies="#isDefined("form.domaincookies") and form.domaincookies#"
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfadmin 
				action="updateScope"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				sessionType=""
				localMode=""
				scopeCascadingType=""
				allowImplicidQueryCall=""
				mergeFormAndUrl=""
				sessionTimeout=""
				applicationTimeout=""
				sessionManagement=""
				clientManagement=""
				clientCookies=""
				domaincookies=""
                clientTimeout=""
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
<cfoutput>




<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>


<table class="tbl" width="740">
<colgroup>
    <col width="150">
    <col width="590">
</colgroup>
<tr>
	<td colspan="2">
<cfif request.adminType EQ "server">
	#stText.Scopes.Server#
<cfelse>
	#stText.Scopes.Web#
</cfif>
	</td>
</tr>

<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
<tr>
	<td class="tblHead" width="150">#stText.Scopes.Cascading#</td>
	<td class="tblContent">
		<span class="comment">#stText.Scopes.CascadingDescription#</span><br>
		<cfset type=scope.scopeCascadingType>
		<cfif hasAccess>
		<select name="scopeCascadingType">
			<option value="strict" <cfif type EQ "strict">selected</cfif>>#ucFirst(stText.Scopes.Strict)#</option>
			<option value="small" <cfif type EQ "small">selected</cfif>>#ucFirst(stText.Scopes.Small)#</option>
			<option value="standard" <cfif type EQ "standard">selected</cfif>>#ucFirst(stText.Scopes.Standard)#</option>
		</select>
		<cfelse>
		<b>#ucFirst(type)#</b>
		</cfif>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.CascadeToResultSet#</td>
	<td class="tblContent">
		<cfif hasAccess>
		<input class="checkbox" type="checkbox" class="checkbox" name="allowImplicidQueryCall" 
			value="yes" <cfif scope.allowImplicidQueryCall>checked</cfif>>
		<cfelse>
			<b>#iif(scope.allowImplicidQueryCall,de('Yes'),de('No'))#</b>
		</cfif><span class="comment">#stText.Scopes.CascadeToResultSetDescription#</span>
	</td>
</tr>

<tr>
	<td class="tblHead" width="150">#stText.Scopes.SessionType#</td>
	<td class="tblContent">
		<span class="comment">#stText.Scopes.SessionTypeDescription#</span><br>
		<cfif hasAccess>
		<select name="sessionType">
			<option value="cfml" <cfif scope.sessionType EQ "cfml">selected</cfif>>#stText.Scopes.SessionType_cfml#</option>
			<option value="j2ee" <cfif scope.sessionType EQ "j2ee">selected</cfif>>#stText.Scopes.SessionType_j2ee#</option>
		</select>
		<cfelse>
			<b>#scope.sessionType#</b>
		</cfif>
	</td>
</tr>


<!--- 
Merge URL and Form --->
<tr>
	<td class="tblHead" width="150">#stText.Scopes.mergeUrlForm#</td>
	<td class="tblContent">
		<cfif hasAccess>
			<input type="checkbox" class="checkbox" name="mergeFormAndUrl" value="yes" 
			<cfif scope.mergeFormAndUrl>checked</cfif>>
		<cfelse>
			<b>#iif(scope.mergeFormAndUrl,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Scopes.mergeUrlFormDescription#</span>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.SessionManagement#</td>
	<td class="tblContent">
		<cfif hasAccess>
			<input type="checkbox" class="checkbox" name="sessionManagement" value="yes" 
			<cfif scope.SessionManagement>checked</cfif>>
		<cfelse>
			<b>#iif(scope.sessionManagement,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Scopes.SessionManagementDescription#</span>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.ClientManagement#</td>
	<td class="tblContent">
		<cfif hasAccess>
			<input type="checkbox" class="checkbox" name="clientManagement" value="yes" 
			<cfif scope.clientManagement>checked</cfif>>
		<cfelse>
			<b>#iif(scope.clientManagement,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Scopes.ClientManagementDescription#</span>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.DomainCookies#</td>
	<td class="tblContent">
		<cfif hasAccess>
			<input type="checkbox" class="checkbox" name="domainCookies" value="yes" 
			<cfif scope.domainCookies>checked</cfif>>
		<cfelse>
			<b>#iif(scope.domainCookies,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Scopes.DomainCookiesDescription#</span>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.ClientCookies#</td>
	<td class="tblContent">
		<cfif hasAccess>
			<input type="checkbox" class="checkbox" name="clientCookies" value="yes" 
			<cfif scope.clientCookies>checked</cfif>>
		<cfelse>
			<b>#iif(scope.clientCookies,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Scopes.ClientCookiesDescription#</span>
	</td>
</tr>

<tr>
	<td class="tblHead" width="150">#stText.Scopes.LocalMode#</td>
	<td class="tblContent">
		<span class="comment">#stText.Scopes.LocalModeDesc#</span>
		<cfif hasAccess>
			
		
			
			<select name="LocalMode">
				<option value="always" <cfif scope.LocalMode EQ "always">selected</cfif>>#stText.Scopes.LocalModeAlways#</option>
				<option value="update" <cfif scope.LocalMode EQ "update">selected</cfif>>#stText.Scopes.LocalModeUpdate#</option>
			</select>
		<cfelse>
			<br /><b>#scope.localMode#</b><input type="hidden"  name="LocalMode" value="#scope.localMode#">
		</cfif>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.SessionTimeout#</td>
	<td class="tblContent">
		<cfset timeout=scope.sessionTimeout>
		<span class="comment">#stText.Scopes.SessionTimeoutDescription#</span>
		<table class="tbl">
		<tr>
			<td class="tblHead">#stText.General.Days#</td>
			<td class="tblHead">#stText.General.Hours#</td>
			<td class="tblHead">#stText.General.Minutes#</td>
			<td class="tblHead">#stText.General.Seconds#</td>
		</tr>
		<cfif hasAccess>
		<tr>
			<td class="tblContent"><cfinput type="text" name="session_days" value="#scope.sessionTimeout_day#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutDaysValue#Session#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="session_hours" value="#scope.sessionTimeout_hour#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutHoursValue#Session#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="session_minutes" value="#scope.sessionTimeout_minute#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutMinutesValue#Session#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="session_seconds" value="#scope.sessionTimeout_second#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutSecondsValue#Session#stText.Scopes.TimeoutEndValue#"></td>
		</tr>
		<cfelse>
		<tr>
			<td class="tblContent" align="center"><b>#scope.sessionTimeout_day#</b></td>
			<td class="tblContent" align="center"><b>#scope.sessionTimeout_hour#</b></td>
			<td class="tblContent" align="center"><b>#scope.sessionTimeout_minute#</b></td>
			<td class="tblContent" align="center"><b>#scope.sessionTimeout_second#</b></td>
		</tr>
		</cfif>
		</table>
		
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.ApplicationTimeout#</td>
	<td class="tblContent">
		<span class="comment">#stText.Scopes.ApplicationTimeoutDescription#</span>
		<cfset timeout=scope.applicationTimeout>
		<table class="tbl">
		<tr>
			<td class="tblHead">#stText.General.Days#</td>
			<td class="tblHead">#stText.General.Hours#</td>
			<td class="tblHead">#stText.General.Minutes#</td>
			<td class="tblHead">#stText.General.Seconds#</td>
		</tr>
		<cfif hasAccess>
		<tr>
			<td class="tblContent"><cfinput type="text" name="application_days" value="#scope.applicationTimeout_day#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutDaysValue#application#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="application_hours" value="#scope.applicationTimeout_hour#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutHoursValue#application#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="application_minutes" value="#scope.applicationTimeout_minute#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutMinutesValue#application#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="application_seconds" value="#scope.applicationTimeout_second#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutSecondsValue#application#stText.Scopes.TimeoutEndValue#"></td>
		</tr>
		<cfelse>
		<tr>
			<td class="tblContent" align="center"><b>#scope.applicationTimeout_day#</b></td>
			<td class="tblContent" align="center"><b>#scope.applicationTimeout_hour#</b></td>
			<td class="tblContent" align="center"><b>#scope.applicationTimeout_minute#</b></td>
			<td class="tblContent" align="center"><b>#scope.applicationTimeout_second#</b></td>
		</tr>
		</cfif>
		</table>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Scopes.ClientTimeout#</td>
	<td class="tblContent">
		<span class="comment">#stText.Scopes.ClientTimeoutDescription#</span>
		<cfset timeout=scope.clientTimeout>
		<table class="tbl">
		<tr>
			<td class="tblHead">#stText.General.Days#</td>
			<td class="tblHead">#stText.General.Hours#</td>
			<td class="tblHead">#stText.General.Minutes#</td>
			<td class="tblHead">#stText.General.Seconds#</td>
		</tr>
		<cfif hasAccess>
		<tr>
			<td class="tblContent"><cfinput type="text" name="client_days" value="#scope.clientTimeout_day#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutDaysValue#client#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="client_hours" value="#scope.clientTimeout_hour#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutHoursValue#client#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="client_minutes" value="#scope.clientTimeout_minute#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutMinutesValue#client#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="client_seconds" value="#scope.clientTimeout_second#" style="width:40px" required="yes" validate="integer" message="#stText.Scopes.TimeoutSecondsValue#client#stText.Scopes.TimeoutEndValue#"></td>
		</tr>
		<cfelse>
		<tr>
			<td class="tblContent" align="center"><b>#scope.clientTimeout_day#</b></td>
			<td class="tblContent" align="center"><b>#scope.clientTimeout_hour#</b></td>
			<td class="tblContent" align="center"><b>#scope.clientTimeout_minute#</b></td>
			<td class="tblContent" align="center"><b>#scope.clientTimeout_second#</b></td>
		</tr>
		</cfif>
		</table>
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
