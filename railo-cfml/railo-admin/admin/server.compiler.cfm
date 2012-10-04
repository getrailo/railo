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





<!--- 
Create Datasource --->
<cfoutput><table class="tbl" width="100%">
<colgroup>
    <col width="230">
    <col>
</colgroup>
<tr>
	<td colspan="2">#stText.setting.compiler#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">

<!--- Dot Notation Upper Case --->
<tr>
	<td class="tblHead" width="150">#stText.setting.dotNotation#</td>
	<td class="tblContent">
		
		<cfif hasAccess>
			<select name="dotNotation">
            	<option value="uc"<cfif setting.dotNotationUpperCase> selected="selected"</cfif>>#stText.setting.dotNotationUpperCase#</option>
            	<option value="oc"<cfif !setting.dotNotationUpperCase> selected="selected"</cfif>>#stText.setting.dotNotationOriginalCase#</option>
            </select><br />
		<cfelse>
			<b>#(setting.dotNotationUpperCase)?stText.setting.dotNotationUpperCase:stText.setting.dotNotationOriginalCase#</b><input type="hidden" name="dotNotation" value="#setting.dotNotationUpperCase?'uc':'oc'#">
		</cfif><span class="comment">#replace(stText.setting.dotNotationDesc,'
','<br />','all')#</span>
	</td>
</tr>

<!--- Supress Whitespace in front of cfargument --->
<tr>
	<td class="tblHead" width="150">#stText.setting.supressWSBeforeArg#</td>
	<td class="tblContent">
		
		<cfif hasAccess>
        	<input type="checkbox" name="supressWSBeforeArg" value="true" <cfif setting.supressWSBeforeArg>checked="checked"</cfif> />
		<cfelse>
			<b>#yesNoFormat(setting.supressWSBeforeArg)#</b><br /><input type="hidden" name="supressWSBeforeArg" value="#setting.supressWSBeforeArg#">
		</cfif><span class="comment">#stText.setting.supressWSBeforeArgDesc#</span>
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