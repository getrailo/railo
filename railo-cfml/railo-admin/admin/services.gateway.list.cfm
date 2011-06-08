

<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.update#">
            <cfadmin 
                action="updateGatewayEntry"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                object="#form.object#"
                template="#form.template#"
                remoteClients="#request.getRemoteClients()#">				
		</cfcase>
    <!--- delete --->
		<cfcase value="#stText.Buttons.Delete#">
				<cfset data.rows=toArrayFromForm("row")>
				<cfset data.ids=toArrayFromForm("id")>
				<cfloop index="idx" from="1" to="#arrayLen(data.ids)#">
					<cfif isDefined("data.rows[#idx#]") and data.ids[idx] NEQ "">
						<cfadmin 
							action="removeGatewayEntry"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							id="#data.ids[idx]#"
							remoteClients="#request.getRemoteClients()#">
						
					</cfif>
				</cfloop>
		</cfcase>
		<cfcase value="#stText.Buttons.restart#">
				<cfset data.rows=toArrayFromForm("row")>
				<cfset data.ids=toArrayFromForm("id")>
				<cfloop index="idx" from="1" to="#arrayLen(data.ids)#">
					<cfif isDefined("data.rows[#idx#]") and data.ids[idx] NEQ "">
						<cfadmin 
							action="gateway"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							id="#data.ids[idx]#"
                            
                            gatewayAction="restart"
                            
							remoteClients="#request.getRemoteClients()#">
						
					</cfif>
				</cfloop>
		</cfcase>
		<cfcase value="#stText.Buttons.stopstart#">
				<cfset data.rows=toArrayFromForm("row")>
				<cfset data.ids=toArrayFromForm("id")>
				<cfloop index="idx" from="1" to="#arrayLen(data.ids)#">
					<cfif isDefined("data.rows[#idx#]") and data.ids[idx] NEQ "">
                    	<cfadmin 
                            action="getGatewayEntry"
                            type="#request.adminType#"
                            password="#session["password"&request.adminType]#"
                            id="#data.ids[idx]#"
                            returnVariable="gateway">
						
						<cfswitch expression="#gateway.state#">
                            <cfcase value="running"><cfset ga="stop"></cfcase>
                            <cfcase value="failed,stopped"><cfset ga="start"><cfset css="Red"></cfcase>
                            <cfdefaultcase><cfset ga=""></cfdefaultcase>
                        </cfswitch>
                        <cfif len(ga)>
                        <cfadmin 
							action="gateway"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							id="#data.ids[idx]#"
                            
                            gatewayAction="#ga#"
                            
							remoteClients="#request.getRemoteClients()#">
						</cfif>
					</cfif>
				</cfloop>
		</cfcase>
		<cfcase value="#stText.Buttons.verify#">
			<cfset data.id=toArrayFromForm("id")>
				<cfset data.rows=toArrayFromForm("row")>
				<cfloop index="idx" from="1" to="#arrayLen(data.ids)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
						<cftry>
							<cfadmin 
								action="verifyCacheConnection"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								name="#data.ids[idx]#">
								<cfset stVeritfyMessages["#data.ids[idx]#"].Label = "OK">
							<cfcatch>
								<!--- <cfset error.message=error.message&data.ids[idx]&": "&cfcatch.message&"<br>"> --->
								<cfset stVeritfyMessages[data.ids[idx]].Label = "Error">
								<cfset stVeritfyMessages[data.ids[idx]].message = cfcatch.message>
							</cfcatch>
						</cftry>
					</cfif>
				</cfloop>
				
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
    
<cfset querySort(entries,"id")>
<cfset srcLocal=queryNew("id,class,cfcpath,custom,readonly,driver,state")>
<cfset srcGlobal=queryNew("id,class,cfcpath,custom,readonly,driver,state")>

<cfloop query="entries">	
	<cfif not entries.readOnly>
    	<cfset tmp=srcLocal>
	<cfelse>
    	<cfset tmp=srcGlobal>
	</cfif>
	<cfset QueryAddRow(tmp)>
    <cfset QuerySetCell(tmp,"id",entries.id)>
    <cfset QuerySetCell(tmp,"class",entries.class)>
    <cfset QuerySetCell(tmp,"cfcPath",entries.cfcPath)>
    <cfset QuerySetCell(tmp,"custom",entries.custom)>
    <cfset QuerySetCell(tmp,"readonly",entries.readonly)>
    <cfset QuerySetCell(tmp,"driver",entries.driver)>
    <cfset QuerySetCell(tmp,"state",entries.state)>
</cfloop>
<cfoutput>


<!--- 
Error Output--->
<cfset printError(error)>

<!---- READ ONLY ---->
<cfif request.adminType EQ "web" and srcGlobal.recordcount>
	<h2>#stText.Settings.gateway.titleReadOnly#</h2>
	#stText.Settings.cache.descReadOnly#
<table class="tbl" width="570">

<cfform action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td width="20"><input type="checkbox" class="checkbox" name="rowreadonly" onclick="selectAll(this)"></td>
		<td width="250" class="tblHead" nowrap>#stText.Settings.gateway.id#</td>
		<td width="250" class="tblHead" nowrap># stText.Settings.gateway.type#</td>
		<td width="250" class="tblHead" nowrap># stText.Settings.gateway.state#</td>
		<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>
	<cfloop query="srcGlobal">
    
    <cfswitch expression="#srcGlobal.state#">
    	<cfcase value="running"><cfset css="Green"></cfcase>
    	<cfcase value="failed,stopped"><cfset css="Red"></cfcase>
    	<cfdefaultcase><cfset css="Yellow"></cfdefaultcase>
    </cfswitch>
    	<cfset driver=drivers[srcGlobal.class]>
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
			<input type="checkbox" class="checkbox" name="row_#srcGlobal.currentrow#" value="#srcGlobal.currentrow#">
			</td>
		</tr>
		</table>
		</td>
		<td class="tblContent#css#" nowrap><input type="hidden" name="id_#srcGlobal.currentrow#" value="#srcGlobal.id#">#srcGlobal.id#</td>
		<td class="tblContent#css#" nowrap>#driver.getLabel()#</td>
		<td class="tblContent#css#" nowrap>#srcGlobal.state#</td>
		<td class="tblContent#css#" nowrap valign="middle" align="center">
				<cfif StructKeyExists(stVeritfyMessages, srcGlobal.id)>
					<cfif stVeritfyMessages[srcGlobal.id].label eq "OK">
						<span class="CheckOk">#stVeritfyMessages[srcGlobal.id].label#</span>
					<cfelse>
						<span class="CheckError" title="#stVeritfyMessages[srcGlobal.id].message##Chr(13)#">#stVeritfyMessages[srcGlobal.id].label#</span>
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							title="#stVeritfyMessages[srcGlobal.id].message##Chr(13)#">
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
			<td><cfmodule template="img.cfm" src="tp.gif" width="8" height="1"></td>		
			<td><cfmodule template="img.cfm" src="#request.admintype#-bgcolor.gif" width="1" height="20"></td>
			<td></td>
		 </tr>
		 <tr>
			<td></td>
			<td valign="top"><cfmodule template="img.cfm" src="#request.admintype#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#request.admintype#-bgcolor.gif" width="36" height="1"></td>
			<td>&nbsp;
				<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.verify#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfform>
</table>
</cfif>

<!--- LIST --->
<cfif srcLocal.recordcount>
	<h2>#stText.Settings.gateway.titleExisting#</h2>
	#stText.Settings.gateway.descExisting#
    
<table class="tbl" width="570">

<cfform action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td width="20"><input type="checkbox" class="checkbox" name="rowreadonly" onclick="selectAll(this)"></td>
		<td width="240" class="tblHead" nowrap>#stText.Settings.gateway.id#</td>
		<td width="180" class="tblHead" nowrap># stText.Settings.gateway.type#</td>
		<td width="100" class="tblHead" nowrap># stText.Settings.gateway.state#</td>
		<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>
	<cfloop query="srcLocal">
    <cfif IsSimpleValue(srcLocal.driver)><cfcontinue></cfif>
    <cfswitch expression="#srcLocal.state#">
    	<cfcase value="running"><cfset css="Green"></cfcase>
    	<cfcase value="failed,stopped"><cfset css="Red"></cfcase>
    	<cfdefaultcase><cfset css="Yellow"></cfdefaultcase>
    </cfswitch>
		
    <tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
			<input type="checkbox" class="checkbox" name="row_#srcLocal.currentrow#" value="#srcLocal.currentrow#">
			</td>
            <td>
            <a href="#request.self#?action=#url.action#&action2=create&id=#Hash(srcLocal.id)#">
			<cfmodule template="img.cfm" src="edit.png" hspace="2" border="0"></a>
            </td>
		</tr>
		</table>
		</td>
		<td class="tblContent#css#" nowrap><input type="hidden" name="id_#srcLocal.currentrow#" value="#srcLocal.id#">#srcLocal.id#</td>
		<td class="tblContent#css#" nowrap>#srcLocal.driver.getLabel()#</td>
		<td class="tblContent#css#" nowrap>#srcLocal.state#</td>
		<td class="tblContent#css#" nowrap valign="middle" align="center">
				<cfif StructKeyExists(stVeritfyMessages, srcLocal.id)>
					<cfif stVeritfyMessages[srcLocal.id].label eq "OK">
						<span class="CheckOk">#stVeritfyMessages[srcLocal.id].label#</span>
					<cfelse>
						<span class="CheckError" title="#stVeritfyMessages[srcLocal.id].message##Chr(13)#">#stVeritfyMessages[srcLocal.id].label#</span>
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							title="#stVeritfyMessages[srcLocal.id].message##Chr(13)#">
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
			<td><cfmodule template="img.cfm" src="tp.gif" width="8" height="1"></td>		
			<td><cfmodule template="img.cfm" src="#request.admintype#-bgcolor.gif" width="1" height="20"></td>
			<td></td>
		 </tr>
		 <tr>
			<td></td>
			<td valign="top"><cfmodule template="img.cfm" src="#request.admintype#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#request.admintype#-bgcolor.gif" width="36" height="1"></td>
			<td>&nbsp;
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.refresh#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.delete#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.restart#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.stopstart#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfform>
</table>
<br><br>
</cfif>


</cfoutput>


<!--- 
	Create gateway entry --->
<cfif access EQ "yes">
<cfoutput>
	<cfset _drivers=ListSort(StructKeyList(drivers),'textnocase')>
	
    <cfif listLen(_drivers)>
    <h2>#stText.Settings.gateway.titleCreate#</h2>
	<table class="tbl" width="350">
	<cfform action="#request.self#?action=#url.action#&action2=create" method="post">
	<tr>
		<td class="tblHead" width="50">#stText.Settings.gateway.id#</td>
		<td class="tblContent" width="300"><cfinput type="text" name="_id" value="" style="width:300px" required="yes" 
			message="#stText.Settings.gateway.nameMissing#"></td>
	</tr>
	
	<tr>
		<td class="tblHead" width="50">#stText.Settings.gateway.type#</td>
		<td class="tblContent" width="300"><select name="name">
					<cfloop list="#_drivers#" index="key">
                    <cfset driver=drivers[key]>
                    <option value="#key#">#trim(driver.getLabel())#</option>
					</cfloop>
				</select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" class="submit" name="run" value="#stText.Buttons.create#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		</td>
	</tr>
	</cfform>
	</table>   
	<br><br>
    <cfelse>
    #stText.Settings.gateway.noDriver#
    </cfif>
    
	</cfoutput>
<cfelse>
 	<cfset noAccess(stText.Settings.gateway.noAccess)>


</cfif>

    
    
