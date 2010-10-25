<!--- 
Defaults --->
<cfparam name="form.mainAction" default="none">
<cfparam name="error" default="#struct(message:"",detail:"")#">
<cfparam name="stveritfymessages" default="#struct()#">
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="mail"
	secValue="yes">
	
<cfadmin 
	action="getMailSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="mail">
<cfadmin 
	action="getMailServers"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="ms">

<cfscript>
stars="*********";
function toPassword(host,pw){
	var i=1;
	if(pw EQ stars){
		for(i=ms.recordcount;i>0;i--){
			if(host EQ ms.hostname[i]) return ms.password[i];
		}
	}
	return pw;
}
</cfscript>

<!--- 
ACTIONS --->
<cftry>
<cfswitch expression="#form.mainAction#">

<!--- Setting --->
	<cfcase value="#stText.Buttons.Setting#">
		<cfif form._mainAction EQ stText.Buttons.update>
		<cfadmin 
			action="updateMailSetting"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			
			logfile="#form.logFile#"
			loglevel="#form.loglevel#"
			spoolEnable="#isDefined("form.spoolenable") and form.spoolenable#"
			spoolInterval="#form.spoolInterval#"
			timeout="#form.timeout#"
			defaultEncoding="#form.defaultEncoding#"
			remoteClients="#request.getRemoteClients()#">
         <cfelseif form._mainAction EQ stText.Buttons.resetServerAdmin>
		<!--- reset to server setting --->
        <cfadmin 
			action="updateMailSetting"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			
			logfile=""
			loglevel=""
			spoolEnable=""
			spoolInterval=""
			timeout=""
			defaultEncoding=""
            
			remoteClients="#request.getRemoteClients()#">
         </cfif>
	</cfcase>
        

<!--- UPDATE --->
	<cfcase value="#stText.Buttons.Update#">
	<!--- update --->
		<cfif form.subAction EQ "#stText.Buttons.Update#">
						
			<cfset data.hosts=toArrayFromForm("hostname")>
			<cfset data.usernames=toArrayFromForm("username")>
			<cfset data.passwords=toArrayFromForm("password")>
			<cfset data.ports=toArrayFromForm("port")>
			<cfset data.tlss=toArrayFromForm("tls")>
			<cfset data.ssls=toArrayFromForm("ssl")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop index="idx" from="1" to="#arrayLen(data.hosts)#">
			
					
				<cfif isDefined("data.rows[#idx#]") and data.hosts[idx] NEQ "">
					<cfparam name="data.ports[#idx#]" default="25">
					<cfif trim(data.ports[idx]) EQ ""><cfset data.ports[idx]=25></cfif>
			
					<cfadmin 
						action="updateMailServer"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						hostname="#data.hosts[idx]#"
						dbusername="#data.usernames[idx]#"
						dbpassword="#toPassword(data.hosts[idx],data.passwords[idx])#"
						port="#data.ports[idx]#"
						tls="#isDefined("data.tlss[#idx#]") and data.tlss[idx]#"
						ssl="#isDefined("data.ssls[#idx#]") and data.ssls[idx]#"
						remoteClients="#request.getRemoteClients()#">
				</cfif>
			</cfloop>
	<!--- delete --->
		
		<cfelseif form.subAction EQ "#stText.Buttons.Delete#">
			<cfset data.rows=toArrayFromForm("row")>
			<cfset data.hosts=toArrayFromForm("hostname")>
			<!---  @todo
			<cflock type="exclusive" scope="application" timeout="5"></cflock> --->
				<cfset len=arrayLen(data.hosts)>
				<cfloop index="idx" from="1" to="#len#">
					<cfif isDefined("data.rows[#idx#]") and data.hosts[idx] NEQ "">
						
					<cfadmin 
						action="removeMailServer"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						hostname="#data.hosts[idx]#"
						remoteClients="#request.getRemoteClients()#">
					</cfif>
				</cfloop>
		
		<cfelseif form.subAction EQ "#stText.Buttons.Verify#">
			<cfset data.rows=toArrayFromForm("row")>
			<cfset data.hosts=toArrayFromForm("hostName")>
			<cfset data.usernames=toArrayFromForm("username")>
			<cfset data.passwords=toArrayFromForm("password")>
			<cfset data.ports=toArrayFromForm("port")>
				
				
				<cfset doNotRedirect=true>
				<cfloop index="idx" from="1" to="#arrayLen(data.rows)#">
					<cfif isDefined("data.rows[#idx#]") and isDefined("data.hosts[#idx#]") and data.hosts[idx] NEQ "">
						<cftry>
							<cfadmin 
								action="verifyMailServer"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								hostname="#data.hosts[idx]#"
								port="#data.ports[idx]#"
								mailusername="#data.usernames[idx]#"
								mailpassword="#toPassword(data.hosts[idx],data.passwords[idx])#">
								<cfset stVeritfyMessages[data.hosts[idx]].Label = "OK">
							<cfcatch>
								<cfset stVeritfyMessages[data.hosts[idx]].Label = "Error">
								<cfset stVeritfyMessages[data.hosts[idx]].message = cfcatch.message>
							</cfcatch>
						</cftry>
					</cfif>
				</cfloop>
				
				
				
		</cfif>
	</cfcase>
</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and not isDefined('doNotRedirect')>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output--->
<cfset printError(error)>

<script>
function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}
</script>
<!---
Mail Settings
		
		@todo help text --->
<cfoutput><h2>#stText.Mail.Settings#</h2>
<table class="tbl" width="600">
<tr>
	<td colspan="2"></td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
<cfset css=iif(len(mail.logfile) EQ 0 and len(mail.strlogfile) NEQ 0,de('Red'),de(''))>
<tr>
	<td class="tblHead" width="150">#stText.mail.DefaultEncoding#</td>
	<td class="tblContent">
		<span class="comment">#stText.mail.DefaultEncodingDescription#</span>
		<cfif hasAccess>
		<cfinput type="text" name="defaultencoding" value="#mail.defaultEncoding#" 
			style="width:200px" required="no" message="#stText.mail.missingEncoding#">
		
		<cfelse>
			<input type="hidden" name="defaultencoding" value="#mail.defaultEncoding#">
		
			<b>#mail.defaultEncoding#</b>
		</cfif>
	</td>
</tr>
<tr>
	<td class="tblHead" width="100" nowrap>#stText.Mail.LogFile#</td>
	<td class="tblContent#css#" width="450" height="28" title="#mail.strlogfile#
#mail.logfile#"><cfif hasAccess><cfinput type="text" name="logFile" 
	value="#mail.strlogfile#" required="no"  
	style="width:450px" message="#stText.Mail.LogFileMissing#"><cfelse><b>#mail.strlogfile#</b></cfif></td>
</tr>
<tr>
	<td class="tblHead" width="100" height="28" nowrap>#stText.Mail.Level#</td>
	<td class="tblContent" width="450"><cfif hasAccess>
	<cfset levels=array("INFO","DEBUG","WARN","ERROR","FATAL")>
	<select name="logLevel">
		<cfloop index="idx" from="1" to="#arrayLen(levels)#"><option<cfif levels[idx] EQ mail.logLevel> selected</cfif>>#levels[idx]#</option></cfloop>
	</select>
	<cfelse><b>#mail.logLevel#</b></cfif></td>
</tr>
<tr>
	<td class="tblHead" width="100" height="28" nowrap>#stText.Mail.SpoolEnabled#</td>
	<td class="tblContent" width="450"><cfif hasAccess><input <cfif mail.spoolEnable>checked</cfif> 
	type="checkbox" class="checkbox" name="spoolEnable" value="yes"><cfelse><b>#iif(mail.spoolEnable,de('Yes'),de('No'))#</b></cfif></td>
</tr>
<tr>
	<td class="tblHead" width="100" height="28" nowrap>#stText.Mail.SpoolInterval#</td>
	<td class="tblContent" width="450"><cfif hasAccess><cfinput type="text" name="spoolInterval" 
	value="#mail.spoolInterval#" validate="integer" style="width:50px" 
	required="no"><cfelse><b>#mail.spoolInterval#</b></cfif></td>
</tr>
<tr>
	<td class="tblHead" width="100" height="28" nowrap>#stText.Mail.Timeout#</td>
	<td class="tblContent" width="450"><cfif hasAccess><cfinput type="text" name="timeout" 
	value="#mail.timeout#" validate="integer" style="width:50px" required="no"><cfelse><b>#mail.timeout#</b></cfif></td>
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2"><cfoutput>
		<input type="hidden" name="mainAction" value="#stText.Buttons.Setting#">
		<input type="submit" class="submit" name="_mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="canel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="_mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</cfoutput></td>
</tr></cfif>
</cfform>
</table>
<br><br>
</cfoutput>

<cfoutput>
<!--- 		
Existing Collection --->
<h2>#stText.Mail.MailServers#</h2>
#stText.Mail.MailServersDescription#
<table class="tbl" width="600">
<tr>
	<td colspan="5"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<tr>
	<td></td>
	<td class="tblHead" nowrap>#stText.Mail.Server#</td>
	<td class="tblHead" nowrap>#stText.Mail.Username#</td>
	<td class="tblHead" nowrap>#stText.Mail.Password#</td>
	<td class="tblHead" nowrap>#stText.Mail.Port#</td>
	<td class="tblHead" nowrap>#stText.Mail.tls#</td>
	<td class="tblHead" nowrap>#stText.Mail.ssl#</td>
			<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
	<cfloop query="ms">
		<tr>
			<td height="26">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><cfif not ms.readonly><input type="checkbox" class="checkbox" name="row_#ms.currentrow#" 
				value="#ms.currentrow#"></cfif></td>
			</tr>
			</table>
			
			</td>
		<!--- host --->
			<td class="tblContent" nowrap><input type="hidden" name="hostName_#ms.currentrow#" 
			value="#ms.hostName#">#ms.hostName#</td>
		<!--- username --->
			<td class="tblContent" nowrap><cfif ms.readonly>#ms.username#&nbsp;<cfelse><cfinput 
				onKeyDown="checkTheBox(this)" type="text" name="username_#ms.currentrow#" 
				value="#ms.username#" required="no"  style="width:120px" 
				message="#stText.Mail.UserNameMissing##ms.currentrow#)"></cfif></td>
		<!--- password --->
			<td class="tblContent" nowrap><cfif ms.readonly>***********&nbsp;<cfelse>
            
            <cfinput 
				onKeyDown="checkTheBox(this)" type="password" passthrough='autocomplete="off"' onClick="this.value='';"
				name="password_#ms.currentrow#" value="#stars#" required="no"  
				style="width:120px" message="#stText.Mail.PasswordMissing##ms.currentrow#)"></cfif></td>
		<!--- port --->
			<td class="tblContent" nowrap><cfif ms.readonly>#ms.port#&nbsp;<cfelse><cfinput onKeyDown="checkTheBox(this)" 
				type="text" name="port_#ms.currentrow#" value="#ms.port#" required="no"  
				style="width:40px" validate="integer" 
				message="#stText.Mail.PortErrorFirst##ms.currentrow##stText.Mail.PortErrorLast#"></cfif></td>
		<!--- tls --->
			<td class="tblContent" nowrap><cfif ms.readonly>#ms.tls#&nbsp;<cfelse>
			<cfinput onClick="checkTheBox(this)" 
				type="checkbox" name="tls_#ms.currentrow#" value="true" required="no"   checked="#ms.tls#"
				></cfif></td>
		<!--- ssl --->
			<td class="tblContent" nowrap><cfif ms.readonly>#ms.ssl#&nbsp;<cfelse>
			<cfinput onClick="checkTheBox(this)" 
				type="checkbox" name="ssl_#ms.currentrow#" value="true" required="no"   checked="#ms.ssl#"
				></cfif></td>
		<!--- check --->
			<td class="tblContent" nowrap valign="middle" align="center">
				<cfif StructKeyExists(stVeritfyMessages, ms.hostName)>
					<cfif stVeritfyMessages[ms.hostName].label eq "OK">
						<span class="CheckOk">#stVeritfyMessages[ms.hostName].label#</span>
					<cfelse>
						<span class="CheckError" title="#stVeritfyMessages[ms.hostName].message##Chr(13)#">#stVeritfyMessages[ms.hostName].label#</span>
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							title="#stVeritfyMessages[ms.hostName].message##Chr(13)#">
					</cfif>
				<cfelse>
					&nbsp;				
				</cfif>
			</td>
		</tr>
	</cfloop>
	<cfif hasAccess>
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><input type="checkbox" class="checkbox" name="row_#ms.recordcount+1#" value="0"></td>
			</tr>
			</table>
			
			</td>
			<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)"  
			type="text" name="hostName_#ms.recordcount+1#" value="" required="no"  style="width:220px"></td>
			<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" name="username_#ms.recordcount+1#" value="" required="no"  style="width:120px"></td>
			<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="password" name="password_#ms.recordcount+1#" passthrough='autocomplete="off"' value="" required="no"  style="width:120px"></td>
			<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" 
			type="text" name="port_#ms.recordcount+1#" value="" required="no" validate="integer" 
			message="Value for Port (Row #ms.recordcount+1#) must be of type number" style="width:40px"></td>
			<td class="tblContent" nowrap><cfinput onClick="checkTheBox(this)" type="checkbox" name="tls_#ms.recordcount+1#" value="true" required="no"></td>
			<td class="tblContent" nowrap><cfinput onClick="checkTheBox(this)" type="checkbox" name="ssl_#ms.recordcount+1#" value="true" required="no"></td>
			<td class="tblContent" nowrap valign="middle" align="center">&nbsp;</td>
			
		</tr>
	</cfif>
	<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="8" line=true>
		<tr>
			<td colspan="8">
			 <table border="0" cellpadding="0" cellspacing="0">
			 <tr>
				<td><cfmodule template="tp.cfm"  width="10" height="1"></td>		
				<td><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="10"></td>
				<td></td>
			 </tr>
			 <tr>
				<td></td>
				<td valign="top"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="36" height="1"></td>
				<td>&nbsp;
				<input type="hidden" name="mainAction" value="#stText.Buttons.Update#">
				<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Verify#">
				<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Update#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
				<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Delete#">
				</td>	
			</tr>
			 </table>
			 </td>
		</tr>
	</cfif>
</cfform>
</cfoutput>
</table>
