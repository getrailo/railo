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
	action="getApplicationSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="appSettings">
<cfadmin 
	action="getApplicationListener"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="listener">
	

<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction1" default="none">
<cfparam name="form.mainAction2" default="none">
<cfparam name="form.subAction" default="none">

<cftry>
<!--- generell --->
	<cfswitch expression="#form.mainAction1#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
		
			<cfif form.scriptProtect EQ "custom">
				<cfparam name="form.scriptProtect_custom" default="none">
				<cfset form.scriptProtect=form.scriptProtect_custom>
			</cfif>
		
			<cfadmin 
				action="updateApplicationSetting"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				scriptProtect="#form.scriptProtect#"
				AllowURLRequestTimeout="#structKeyExists(form,'AllowURLRequestTimeout') and form.AllowURLRequestTimeout#"
				requestTimeout="#CreateTimeSpan(form.request_days,form.request_hours,form.request_minutes,form.request_seconds)#"
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
		
			<cfadmin 
				action="updateApplicationSetting"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				scriptProtect=""
				AllowURLRequestTimeout=""
				requestTimeout=""
                
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	</cfswitch>

<!--- listener --->
	<cfswitch expression="#form.mainAction2#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
		
			<cfadmin 
				action="updateApplicationListener"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				listenerType="#form.type#"
				listenerMode="#form.mode#"
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
		
			<cfadmin 
				action="updateApplicationListener"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				listenerType=""
				listenerMode=""
                
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
<script>
function deSelectCustom(form) {
	form.scriptProtect_custom[0].checked=false;
	form.scriptProtect_custom[1].checked=false;
	form.scriptProtect_custom[2].checked=false;
	form.scriptProtect_custom[3].checked=false;
	
}
function selectCustom(form) {
	form.scriptProtect[2].checked=true;
	
}
</script>



<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>


<table class="tbl" width="740">
<colgroup>
    <col width="150">
    <col width="590">
</colgroup>
<tr>
	<td colspan="2">
<cfif request.adminType EQ "server">
	#stText.application.Server#
<cfelse>
	#stText.application.Web#
</cfif>
	</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">


<!--- script-protect --->
<tr>
	<td class="tblHead" width="150">#stText.application.scriptProtect#</td>
	<td class="tblContent">
		<span class="comment">#stText.application.scriptProtectDescription#</span>
		<cfif hasAccess>
		
		<cfset isNone=appSettings.scriptProtect EQ  "none">
		<cfset isAll=appSettings.scriptProtect EQ  "all">
		<cfset isCustom=not isNone and not isAll>
		
		<table class="tbl">
		<tr>
			<td class="tblHead">none</td>
			<td class="tblContent"><input type="radio" class="radio" onclick="deSelectCustom(this.form)" name="scriptProtect" value="none" <cfif isNone>checked="checked"</cfif>><span class="comment">#stText.application.scriptProtectNone#</span></td>
		</tr>
		<tr>
			<td class="tblHead">all</td>
			<td class="tblContent"><input type="radio" onclick="deSelectCustom(this.form)" class="radio" name="scriptProtect" value="all" <cfif isAll>checked="checked"</cfif>><span class="comment">#stText.application.scriptProtectAll#</span></td>
		</tr>
		<tr>
			<td class="tblHead">custom</td>
			<td class="tblContent"><input type="radio" class="radio" name="scriptProtect" value="custom" <cfif isCustom>checked="checked"</cfif>><span class="comment">#stText.application.scriptProtectCustom#</span>
			<table class="tbl">
			<tr>
				<td class="tblHead">cgi</td>
				<td class="tblContent"><input type="checkbox" onclick="selectCustom(this.form);" class="checkbox" name="scriptProtect_custom" 
				<cfif ListFindNoCase(appSettings.scriptProtect,'cgi')> checked="checked"</cfif> value="cgi"></td>
				<td class="tblHead">cookie</td>
				<td class="tblContent"><input type="checkbox" onclick="selectCustom(this.form);" class="checkbox" name="scriptProtect_custom" 
				<cfif ListFindNoCase(appSettings.scriptProtect,'cookie')> checked="checked"</cfif> value="cookie"></td>
				<td class="tblHead">form</td>
				<td class="tblContent"><input type="checkbox" onclick="selectCustom(this.form);" class="checkbox" name="scriptProtect_custom" 
				<cfif ListFindNoCase(appSettings.scriptProtect,'form')> checked="checked"</cfif> value="form"></td>
				<td class="tblHead">url</td>
				<td class="tblContent"><input type="checkbox" onclick="selectCustom(this.form);" class="checkbox" name="scriptProtect_custom" 
				<cfif ListFindNoCase(appSettings.scriptProtect,'url')> checked="checked"</cfif> value="url"></td>
			
			</tr>
			</table>
			</td>
		</tr>
		</table>
		<cfelse>
			<input type="hidden" name="scriptProtect" value="#appSettings.scriptProtect#">
		
			<br /><b>#appSettings.scriptProtect#</b>
		</cfif>
	</td>
</tr>


<!--- request timeout --->
<tr>
	<td class="tblHead" width="150">#stText.application.RequestTimeout#</td>
	<td class="tblContent">
		<cfset timeout=appSettings.requestTimeout>
		<span class="comment">#stText.application.RequestTimeoutDescription#</span>
		<table class="tbl">
		<tr>
			<td class="tblHead">#stText.General.Days#</td>
			<td class="tblHead">#stText.General.Hours#</td>
			<td class="tblHead">#stText.General.Minutes#</td>
			<td class="tblHead">#stText.General.Seconds#</td>
		</tr>
		
		<cfif hasAccess>
		<tr>
			<td class="tblContent"><cfinput type="text" name="request_days" value="#appSettings.requestTimeout_day#" 
				style="width:40px" required="yes" validate="integer" 
				message="#stText.Scopes.TimeoutDaysValue#request#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="request_hours" value="#appSettings.requestTimeout_hour#" 
				style="width:40px" required="yes" validate="integer" 
				message="#stText.Scopes.TimeoutHoursValue#request#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="request_minutes" value="#appSettings.requestTimeout_minute#" 
				style="width:40px" required="yes" validate="integer" 
				message="#stText.Scopes.TimeoutMinutesValue#request#stText.Scopes.TimeoutEndValue#"></td>
			<td class="tblContent"><cfinput type="text" name="request_seconds" value="#appSettings.requestTimeout_second#" 
				style="width:40px" required="yes" validate="integer" 
				message="#stText.Scopes.TimeoutSecondsValue#request#stText.Scopes.TimeoutEndValue#"></td>
		</tr>
		<cfelse>
		<tr>
			<td class="tblContent" align="center"><b>#appSettings.requestTimeout_day#</b></td>
			<td class="tblContent" align="center"><b>#appSettings.requestTimeout_hour#</b></td>
			<td class="tblContent" align="center"><b>#appSettings.requestTimeout_minute#</b></td>
			<td class="tblContent" align="center"><b>#appSettings.requestTimeout_second#</b></td>
		</tr>
		</cfif>
		</table>
		
	</td>
</tr>

<!--- request timeout url --->
<tr>
	<td class="tblHead" width="150">#stText.application.AllowURLRequestTimeout#</td>
	<td class="tblContent">
		<cfif hasAccess>
            <input type="checkbox" name="AllowURLRequestTimeout" value="true"
			<cfif appSettings.AllowURLRequestTimeout>  checked="checked"</cfif>>
		<cfelse>
        	<input type="hidden" name="AllowURLRequestTimeout" value="#appSettings.AllowURLRequestTimeout#">
            <b>#yesNoFormat(appSettings.AllowURLRequestTimeout)#</b>
        </cfif>
		
			<span class="comment">#stText.application.AllowURLRequestTimeoutDesc#</span>
	</td>
</tr>




<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction1" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction1" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>

</cfform>
</table>
<br /><br />

<h2>#stText.application.listener#</h2>
#stText.application.listenerDescription#

<table class="tbl" width="740">

<cfform action="#request.self#?action=#url.action#" method="post">

<!--- listener type --->
<tr>
	<td class="tblHead">#stText.application.listenerType#</td>
	<td class="tblContent">
	<cfif hasAccess>
		<span class="comment">#stText.application.listenerTypeDescription#</span><br />
		<table class="tbl" width="100%">
        <cfloop index="key" list="none,classic,modern,mixed">
		<tr>
			<td width="200" class="tblHead" nowrap="nowrap">#stText.application['listenerType_' & key]#</td>
			<td width="400" class="tblContent"><input type="radio" name="type" value="#key#" <cfif listener.type EQ key>checked="checked"</cfif>>
			<span class="comment">#stText.application['listenerTypeDescription_' & key]#</span></td>
		</tr>
		</cfloop>
		</table>
	<cfelse>
		<input type="hidden" name="type" value="#listener.type#">
		<b>#listener.type#</b><br />
		<span class="comment">#stText.application['listenerTypeDescription_' & listener.type]#</span>
	</cfif>
	</td>
</tr>


<!--- listener mode --->
<tr>
	<td class="tblHead">#stText.application.listenerMode#</td>
	<td class="tblContent">
	<cfif hasAccess>
		<span class="comment">#stText.application.listenerModeDescription#</span><br />
		<table class="tbl" width="100%">
		<cfloop index="key" list="curr,root,curr2root">
		<tr>
			<td width="200" class="tblHead" nowrap="nowrap">#stText.application['listenerMode_' & key]#</td>
			<td width="400" class="tblContent"><input type="radio" name="mode" value="#key#" <cfif listener.mode EQ key>checked="checked"</cfif>>
			<span class="comment">#stText.application['listenerModeDescription_' & key]#</span></td>
		</tr>
		</cfloop>
		</table>
	<cfelse>
		<input type="hidden" name="type" value="#listener.mode#">
		<b>#listener.mode#</b><br />
		<span class="comment">#stText.application['listenerModeDescription_' & listener.mode]#</span>
	</cfif>
	</td>
</tr>




<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="3">
<tr>
	<td colspan="3">
		<input type="submit" class="submit" name="mainAction2" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction2" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>
<br><br>