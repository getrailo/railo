<cfadmin 
	action="getRemoteClients"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="clients">

<cfparam name="url.row" default="0">
<cfif url.row GT 0 and url.row LTE clients.recordcount>
	<cfset form.mainAction=stText.Buttons.verify>
	<cfset form['url_'&row]=clients.url[row]>
	<cfset form['row_'&row]=row>
	
</cfif>
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="remote"
	secValue="yes">

<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Delete#">
			<cfset data.urls=toArrayFromForm("url")>
			<cfset data.rows=toArrayFromForm("row")>
			
			<cfloop index="idx" from="1" to="#arrayLen(data.urls)#">
				<cfif isDefined("data.rows[#idx#]") and data.urls[idx] NEQ "">
					<cfadmin 
						action="removeRemoteClient"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						url="#data.urls[idx]#">
					
				</cfif>
			</cfloop>
		</cfcase>
		<cfcase value="#stText.Buttons.verify#">
        	
				<cfset data.urls=toArrayFromForm("url")>
				<cfset data.rows=toArrayFromForm("row")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.urls)#">
					<cfif isDefined("data.rows[#idx#]") and data.urls[idx] NEQ "">
						<cfadmin 
							action="getRemoteClient"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							url="#data.urls[idx]#"
							returnVariable="rclient">
						
						<cftry>
             
							<cfadmin 
								action="verifyRemoteClient"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								attributeCollection="#rclient#">
								<cfset stVeritfyMessages["#data.urls[idx]#"].Label = "OK">
							<cfcatch>
								<!--- <cfset error.message=error.message&data.names[idx]&": "&cfcatch.message&"<br>"> --->
								<cfset stVeritfyMessages[data.urls[idx]].Label = "Error">
								<cfset stVeritfyMessages[data.urls[idx]].message = cfcatch.message>
							</cfcatch>
						</cftry>
					</cfif>
				</cfloop>
				
		</cfcase>
		<cfcase value="#stText.Buttons.Update#">
			
			<cfadmin 
				action="updatePSQ"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				psq="#structKeyExists(form,"psq") and form.psq#">
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq stText.Buttons.verify>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>
<!--- 
Error Output --->
<cfset printError(error)>



<cfadmin 
    action="getRemoteClientUsage"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    
    returnVariable="usage">

<!--- 
list all mappings and display necessary edit fields --->
<script>
function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}
</script>


<cfif clients.recordcount>
	<cfoutput>
	#stText.remote.desc#
	
	<h2>#stText.remote.listClients#</h2>
	<table class="tbl">
	
	
	
	<cfform action="#request.self#?action=#url.action#" method="post">
		<tr>
			<td width="20"></td>
			<td width="#iif(request.adminType NEQ "web",400,320)#" class="tblHead" nowrap>#stText.remote.label#</td>
			<cfloop query="usage">
            <td width="80" class="tblHead">#usage.displayname#</td>
			</cfloop>
            <td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
		</tr>
		<cfloop query="clients">
			<cfset css=iif(len(clients.usage),de('Green'),de('Red'))>
			
			<!--- and now display --->
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
<cfif hasAccess><input type="checkbox" class="checkbox" name="row_#clients.currentrow#" value="#clients.currentrow#">
				<input type="hidden" name="url_#clients.currentrow#" value="#clients.url#"></cfif>
				<!--- <input type="hidden" name="password_#clients.currentrow#" value="#clients.Password#">--->
				</td>
				<td>
<cfif hasAccess><a href="#request.self#?action=#url.action#&action2=create&url=#hash(clients.url)#">
			<cfmodule template="img.cfm" src="edit.png" hspace="2" border="0"></a></cfif></td>
			</tr>
			</table>
			</td>
            <cfset css="">
			<cfif StructKeyExists(stVeritfyMessages, clients.url)>
				<cfset isOK=stVeritfyMessages[clients.url].label eq "OK">
				<cfset css=iif(isOK ,de('Green'),de('Red'))>
            </cfif>
            
			<td class="tblContent#css#" nowrap>#clients.label#</td>
            
			<cfloop query="variables.usage">
				<cfset has=listFindNoCase(clients.usage,variables.usage.code)>
                <td class="tblContent#css#" nowrap title="#variables.usage.displayname#">#YesNoFormat(has)#</td>
            </cfloop>
			
				
			<cfif StructKeyExists(stVeritfyMessages, clients.url)>
			<td class="tblContent#css#" nowrap valign="middle" align="center">
					<cfif isOK>
						<span class="CheckOk">#stVeritfyMessages[clients.url].label#</span>
					<cfelse>
						<span class="CheckError" title="#stVeritfyMessages[clients.url].message##Chr(13)#">#stVeritfyMessages[clients.url].label#</span>
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							alt="#stVeritfyMessages[clients.url].message##Chr(13)#">
					</cfif>
			</td>
			<cfelse>
			<td class="tblContent">&nbsp;</td>			
			</cfif>
		</tr>
		</cfloop>

<cfif hasAccess>
		<tr>
			<td colspan="#iif(request.adminType NEQ "web",5,6)#">
			 <table border="0" cellpadding="0" cellspacing="0">
			 <tr>
				<td><cfmodule template="tp.cfm"  width="10" height="1"></td>		
				<td><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="20"></td>
				<td></td>
			 </tr>
			 <tr>
				<td></td>
				<td valign="top"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="36" height="1"></td>
				<td>&nbsp;
				<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Verify#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
				<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Delete#">
				</td>	
			</tr>
			 </table>
			 </td>
		</tr>
</cfif>
	</cfform>
	</cfoutput>
	</table>
	<br><br>
</cfif>



<cfif hasAccess>
	<cfoutput>
	<!--- 
	Create Remote Client --->
	<cfform action="#request.self#?action=#url.action#&action2=create" method="post">
		<input type="submit" class="submit" name="run" value="#stText.remote.newClient#">
	</cfform>
	</cfoutput>
</cfif>