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


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>
<!--- 
Create Datasource --->
<cfoutput>


<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>



<table class="tbl" width="100%">
<colgroup>
    <col width="150">
    <col>
</colgroup>
<tr>
	<td colspan="2">#stText.charset[request.adminType]#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">

<!--- Template --->
<tr>
	<td class="tblHead" width="150">#stText.charset.templateCharset#</td>
	<td class="tblContent">
		<span class="comment">#stText.charset.templateCharsetDescription#</span><br />
		<cfif hasAccess>
		<cfinput type="text" name="templateCharset" value="#charset.templateCharset#" 
			style="width:200px" required="no" message="#stText.charset.missingTemplateCharset#">
		
		<cfelse>
			<input type="hidden" name="templateCharset" value="#charset.templateCharset#">
		
			<b>#charset.templateCharset#</b>
		</cfif>
	</td>
</tr>

<!--- Web --->
<tr>
	<td class="tblHead" width="150">#stText.charset.webCharset#</td>
	<td class="tblContent">
		<span class="comment">#stText.charset.webCharsetDescription#</span><br />
		<cfif hasAccess>
		<cfinput type="text" name="webCharset" value="#charset.webCharset#" 
			style="width:200px" required="no" message="#stText.charset.missingWebCharset#">
		
		<cfelse>
			<input type="hidden" name="webCharset" value="#charset.webCharset#">
		
			<b>#charset.webCharset#</b>
		</cfif>
	</td>
</tr>

<!--- Resource --->
<tr>
	<td class="tblHead" width="150">#stText.charset.resourceCharset#</td>
	<td class="tblContent">
		<span class="comment">#stText.charset.resourceCharsetDescription#</span><br />
		<cfif hasAccess>
		<cfinput type="text" name="resourceCharset" value="#charset.resourceCharset#" 
			style="width:200px" required="no" message="#stText.charset.missingResourceCharset#">
		
		<cfelse>
			<input type="hidden" name="resourceCharset" value="#charset.resourceCharset#">
		
			<b>#charset.resourceCharset#</b>
		</cfif>
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