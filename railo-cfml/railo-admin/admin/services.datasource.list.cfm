<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Delete#">
			<cfset data.names=toArrayFromForm("name")>
				<cfset data.rows=toArrayFromForm("row")>
				<cfset data.names=toArrayFromForm("name")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
						<cfadmin 
							action="removeDatasource"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							name="#data.names[idx]#"
							remoteClients="#request.getRemoteClients()#">
						
					</cfif>
				</cfloop>
		</cfcase>
		<cfcase value="#stText.Buttons.verify#">
			<cfset data.names=toArrayFromForm("name")>
				<cfset data.rows=toArrayFromForm("row")>
				<cfset data.names=toArrayFromForm("name")>
				<cfset data.passwords=toArrayFromForm("password")>
				<cfset data.usernames=toArrayFromForm("username")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
						<cftry>
							<cfadmin 
								action="verifyDatasource"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								name="#data.names[idx]#"
								dbusername="#data.usernames[idx]#"
								dbpassword="#data.passwords[idx]#">
								<cfset stVeritfyMessages["#data.names[idx]#"].Label = "OK">
							<cfcatch>
								<!--- <cfset error.message=error.message&data.names[idx]&": "&cfcatch.message&"<br>"> --->
								<cfset stVeritfyMessages[data.names[idx]].Label = "Error">
								<cfset stVeritfyMessages[data.names[idx]].message = cfcatch.message>
							</cfcatch>
						</cftry>
					</cfif>
				</cfloop>
				
		</cfcase>
        <!--- update --->
		<cfcase value="#stText.Buttons.Update#">
			
			<cfadmin 
				action="updatePSQ"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				psq="#structKeyExists(form,"psq") and form.psq#"
				remoteClients="#request.getRemoteClients()#">
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfadmin 
				action="updatePSQ"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				psq=""
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
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq stText.Buttons.verify>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>

<cfif structKeyExists(url,'verified') and len(url.verified)>
	<cfset stVeritfyMessages={}>
	<cfset stVeritfyMessages[url.verified].Label = "OK">
</cfif>


<cfadmin 
	action="getDatasourceSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="dbSetting">

<cfoutput>	
<!--- 
Create Datasource --->

<table class="tbl" width="540">
<colgroup>
    <col width="150">
    <col width="390">
</colgroup>
<tr>
	<td colspan="2"><h2>#stText.Settings.DatasourceSettings#</h2></td>
</tr>

<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
<tr>
	<td class="tblHead" width="150">#stText.Settings.PreserveSingleQuotes#</td>
	<td class="tblContent">
	<cfif access NEQ 0><input type="checkbox" class="checkbox" name="psq" value="yes" <cfif dbSetting.psq>checked</cfif>><cfelse><b>#yesNoFormat(dbSetting.psq)#</b></cfif>
	<span class="comment">#stText.Settings.PreserveSingleQuotesDescription#</span></td>
	
</tr>
<cfif access NEQ 0>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr></cfif>
</cfform>
</cfoutput>
</table>
<br><br>
<cfadmin 
	action="getDatasources"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="datasources">

<!--- 
list all mappings and display necessary edit fields --->


<!--- <cfset srcLocal=struct()>
<cfset srcGlobal=struct()>
<cfloop collection="#sources#" item="key">
	<cfif sources[key].isReadOnly()>
		<cfset srcGlobal[key]=sources[key]>
	<cfelse>
		<cfset srcLocal[key]=sources[key]>
	</cfif>
</cfloop> --->
<cfset querySort(datasources,"name")>
<cfset srcLocal=queryNew("name,classname,dsn,username,password,readonly,storage")>
<cfset srcGlobal=queryNew("name,classname,dsn,username,password,readonly,storage")>

		
<cfloop query="datasources">
	<cfif not datasources.readOnly>
		<cfset QueryAddRow(srcLocal)>
		<cfset QuerySetCell(srcLocal,"name",datasources.name)>
		<cfset QuerySetCell(srcLocal,"classname",datasources.classname)>
		<cfset QuerySetCell(srcLocal,"dsn",datasources.dsn)>
		<cfset QuerySetCell(srcLocal,"username",datasources.username)>
		<cfset QuerySetCell(srcLocal,"password",datasources.password)>
		<cfset QuerySetCell(srcLocal,"readonly",datasources.readonly)>
		<cfset QuerySetCell(srcLocal,"storage",datasources.storage)>
	<cfelse>
		<cfset QueryAddRow(srcGlobal)>
		<cfset QuerySetCell(srcGlobal,"name",datasources.name)>
		<cfset QuerySetCell(srcGlobal,"classname",datasources.classname)>
		<cfset QuerySetCell(srcGlobal,"dsn",datasources.dsn)>
		<cfset QuerySetCell(srcGlobal,"username",datasources.username)>
		<cfset QuerySetCell(srcGlobal,"password",datasources.password)>
		<cfset QuerySetCell(srcGlobal,"readonly",datasources.readonly)>
		<cfset QuerySetCell(srcGlobal,"storage",datasources.storage)>
	</cfif>
</cfloop>

<cfif request.adminType EQ "web" and srcGlobal.recordcount>
	<cfoutput>
	
	<table class="tbl" border="0">
 	<colgroup>
        <col width="20">
        <col width="35%">
        <col width="35%">
        <col width="15%">
        <col width="15%">
    </colgroup>
	<tr>
		<td colspan="5"><h2>#stText.Settings.ReadOnlyDatasources#</h2>#stText.Settings.ReadOnlyDatasourcesDescription#</td>
	</tr>
	<tr>
		<td colspan="5"><cfmodule template="tp.cfm"  width="1" height="1"></td>
	</tr>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<tr>
			<td><input type="checkbox" class="checkbox" name="rowreadonly" onclick="selectAll(this)"></td>
			<td class="tblHead" nowrap>#stText.Settings.Name#</td>
			<td class="tblHead" nowrap>#stText.Settings.Type#</td>
			<td class="tblHead" nowrap>#stText.Settings.dbStorage#</td>
			<td class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
		</tr>
		<cfloop query="srcGlobal">
			<!--- and now display --->
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
				<input type="checkbox" class="checkbox" name="row_#srcGlobal.currentrow#" value="#srcLocal.currentrow#">
				<input type="hidden" name="username_#srcGlobal.currentrow#" value="#srcGlobal.Username#">
				<input type="hidden" name="password_#srcGlobal.currentrow#" value="#srcGlobal.Password#">
				</td>
			</tr>
			</table>
			</td>
			<td class="tblContent" nowrap><input type="hidden" 
				name="name_#srcGlobal.currentrow#" value="#srcGlobal.name#">#srcGlobal.name#</td>
			<td class="tblContent" nowrap>#getTypeName(srcGlobal.ClassName,srcGlobal.dsn)#</td>
			<td class="tblContent" nowrap>#yesNoFormat(srcGlobal.storage)#</td>
			<td class="tblContent" nowrap valign="middle" align="center">
				<cfif StructKeyExists(stVeritfyMessages, srcGlobal.name)>
					#stVeritfyMessages[srcGlobal.name].label#
					<cfif stVeritfyMessages[srcGlobal.name].label neq "OK">
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							alt="#stVeritfyMessages[srcGlobal.name].message##Chr(13)#">
					</cfif>
				<cfelse>
					&nbsp;				
				</cfif>
			</td>
		</tr>
		</cfloop>
		<tr>
			<td colspan="5">
			 <table border="0" cellpadding="0" cellspacing="0">
			 <tr>
				<td><cfmodule template="tp.cfm"  width="6" height="1"></td>		
				<td><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="20"></td>
				<td></td>
			 </tr>
			 <tr>
				<td></td>
				<td valign="top"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="36" height="1"></td>
				<td>&nbsp;
				<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Verify#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
				</td>	
			</tr>
			 </table>
			 </td>
		</tr>
	</cfform>
	</table>
	<br><br>
	</cfoutput>
</cfif>


<cfif srcLocal.recordcount>
	<cfoutput>
	
	<table class="tbl" width="740">
	<tr>
		<td colspan="5"><h2>#stText.Settings.ListDatasources#</h2>#stText.Settings['ListDatasourcesDesc'& request.adminType ]#</td> 
	</tr>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<tr>
			<td width="60"><input type="checkbox" class="checkbox" name="rowread" onclick="selectAll(this)"></td>
			<td width="205" class="tblHead" nowrap>#stText.Settings.Name#</td>
			<td width="355" class="tblHead" nowrap>#stText.Settings.Type#</td>
			<td width="50" class="tblHead" nowrap>#stText.Settings.dbStorage#</td>
			<td width="70" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
		</tr>
		<cfloop query="srcLocal">
        	<cfset link="#request.self#?action=#url.action#&action2=create&name=#srcLocal.name#">
			<!--- and now display --->
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
				<input type="checkbox" class="checkbox" name="row_#srcLocal.currentrow#" value="#srcLocal.currentrow#">
				<input type="hidden" name="username_#srcLocal.currentrow#" value="#srcLocal.Username#">
				<input type="hidden" name="password_#srcLocal.currentrow#" value="#srcLocal.Password#">
				</td>
				<td><a href="#link#"><cfmodule template="img.cfm" src="edit.png" hspace="2" border="0"></a></td>
			</tr>
			</table>
			</td>
			<td class="tblContent" nowrap><input type="hidden" 
				name="name_#srcLocal.currentrow#" value="#srcLocal.name#">#srcLocal.name#</td>
			<td class="tblContent" nowrap>#getTypeName(srcLocal.ClassName,srcLocal.dsn)#</td>
			<td class="tblContent" nowrap>#yesNoFormat(srcLocal.storage)#</td>
			<td class="tblContent" nowrap valign="middle" align="center">
				<cfif StructKeyExists(stVeritfyMessages, srcLocal.name)>
					<cfif stVeritfyMessages[srcLocal.name].label eq "OK">
						<span class="CheckOk">#stVeritfyMessages[srcLocal.name].label#</span>
					<cfelse>
						<span class="CheckError" title="#stVeritfyMessages[srcLocal.name].message##Chr(13)#">#stVeritfyMessages[srcLocal.name].label#</span>
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							title="#stVeritfyMessages[srcLocal.name].message##Chr(13)#">
					</cfif>
				<cfelse>
					&nbsp;				
				</cfif>
			</td>
		</tr>
		</cfloop>
		
		<cfmodule template="remoteclients.cfm" colspan="4" line="true">
		<tr>
			<td colspan="5">
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
				<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Verify#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
				<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Delete#">
				</td>	
			</tr>
			 </table>
			 </td>
		</tr>
	</cfform>
	</cfoutput>
	</table>
	<br><br>
</cfif>

<cfif access EQ -1 or access GT srcLocal.recordcount>
	<cfoutput>
	<!--- 
	Create Datasource --->
	<h2>#stText.Settings.DatasourceModify#</h2>
	<table class="tbl" width="350">
	<tr>
		<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
	</tr>
	<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=create" method="post">
	<tr>
		<td class="tblHead" width="50">#stText.Settings.Name#</td>
		<td class="tblContent" width="300"><cfinput type="text" name="name" value="" style="width:300px" required="yes" 
			message="#stText.Settings.NameMissing#"></td>
	</tr>
	
	<cfset cdList = {}>
	<cfset cdSkip = {}>
	<cfloop collection="#variables.selectors#" item="key">

		<cfset cdList[ key ] = variables.selectors[ key ].getName()>

		<cfloop array="#variables.selectors[ key ].getOptions()#" index="keySkip">

			<cfset cdSkip[ keySkip ] = true>
		</cfloop>
	</cfloop>
	<cfloop collection="#variables.drivers#" item="key">
	
		<cfif !structKeyExists( cdSkip, key ) && !findNoCase( "(old)", variables.drivers[ key ].getName() )>
		
			<cfset cdList[ key ] = variables.drivers[ key ].getName()>
		</cfif>
	</cfloop>
	
	<cfset keys=StructKeyArray(cdList)>
	<cfset ArraySort(keys,"textNoCase")>
	<tr>
		<td class="tblHead" width="50">#stText.Settings.Type#</td>
		<td class="tblContent" width="300"><select name="type">
					<cfloop array="#keys#" index="key">
					
						<option value="#key#">#cdList[ key ]#</option>
					</cfloop>
				</select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="hidden" name="mark" value="create">
			<input type="submit" class="submit" name="run" value="#stText.Buttons.Create#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		</td>
	</tr>
	</cfform>
	</table>   
	<br><br>
	</cfoutput>
</cfif>