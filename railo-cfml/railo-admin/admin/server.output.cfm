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
				showVersion="#isDefined('form.showVersion') and form.showVersion#"
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



<!--- 
Create Datasource --->
<cfoutput><table class="tbl" width="740">
<colgroup>
    <col width="250">
    <col width="490">
</colgroup>
<tr>
	<td colspan="2">#stText.setting[request.adminType]#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">

<!--- Supress Whitespace --->
<tr>
	<td class="tblHead" width="150">#stText.setting.whitespace#</td>
	<td class="tblContent">
		<span class="comment">
		<cfif hasAccess>
			<input type="checkbox" name="suppressWhitespace" value="true" <cfif setting.suppressWhitespace>checked="checked"</cfif>>
		<cfelse>
			<input type="hidden" name="suppressWhitespace" value="#setting.suppressWhitespace#">
		</cfif>#stText.setting.whitespaceDescription#</span>
	</td>
</tr>

<!--- Show Version --->
<tr>
	<td class="tblHead" width="150">#stText.setting.showVersion#</td>
	<td class="tblContent">
		
		<cfif hasAccess>
			<input type="checkbox" name="showVersion" value="true" <cfif setting.showVersion>checked="checked"</cfif>>
		<cfelse>
			<b>#iif(setting.showVersion,de('Yes'),de('No'))#</b>
			<input type="hidden" name="showVersion" value="#setting.showVersion#">
		</cfif><span class="comment">#stText.setting.showVersionDescription#</span>
	</td>
</tr>

<cfset stText.setting.suppressContent="Supress Content for CFC Remoting">
<cfset stText.setting.suppressContentDescription="Suppress content written to response stream when a Component is invoked remotely. Only work when content not was flushed before.">
<!--- Supress Content when CFC Remoting --->
<tr>
	<td class="tblHead" width="150">#stText.setting.suppressContent#</td>
	<td class="tblContent">
		
		<cfif hasAccess>
			<input type="checkbox" name="suppressContent" value="true" <cfif setting.suppressContent>checked="checked"</cfif>>
		<cfelse>
			<b>#iif(setting.suppressContent,de('Yes'),de('No'))#</b>
			<input type="hidden" name="showVersion" value="#setting.suppressContent#">
		</cfif><span class="comment">#stText.setting.suppressContentDescription#</span>
	</td>
</tr>

<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input class="submit" type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>
<br><br>