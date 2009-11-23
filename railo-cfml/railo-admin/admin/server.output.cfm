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
				
				supressWhiteSpace="#isDefined('form.supressWhitespace') and form.supressWhitespace#"
				showVersion="#isDefined('form.showVersion') and form.showVersion#"
				remoteClients="#request.getRemoteClients()#">
	
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			
			<cfadmin 
				action="updateOutputSetting"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				supressWhiteSpace=""
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
<cfoutput><table class="tbl" width="600">
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
			<input type="checkbox" name="supressWhitespace" value="true" <cfif setting.supressWhitespace>checked="checked"</cfif>>
		<cfelse>
			<input type="hidden" name="supressWhitespace" value="#setting.supressWhitespace#">
		</cfif>#stText.setting.whitespaceDescription#</span>
	</td>
</tr>

<!--- Show Version --->
<cfif server.ColdFusion.ProductLevel NEQ "community">
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
<cfelse>
<input type="hidden" name="showVersion" value="true">
</cfif>

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