

<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
            <cfadmin 
                action="removeCacheDefaultConnection"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                remoteClients="#request.getRemoteClients()#">				
		</cfcase>
		<cfcase value="#stText.Buttons.update#">
            <cfadmin 
                action="updateCacheDefaultConnection"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                object="#StructKeyExists(form,'object')?form.object:''#"
                template="#StructKeyExists(form,'template')?form.template:''#"
                query="#StructKeyExists(form,'query')?form.query:''#"
                resource="#StructKeyExists(form,'resource')?form.resource:''#"
                remoteClients="#request.getRemoteClients()#">				
		</cfcase>
    <!--- delete --->
		<cfcase value="#stText.Buttons.Delete#">
			<cfset data.names=toArrayFromForm("name")>
				<cfset data.rows=toArrayFromForm("row")>
				<cfset data.names=toArrayFromForm("name")>
				<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
						<cfadmin 
							action="removeCacheConnection"
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
				
				<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
						<cftry>
							<cfadmin 
								action="verifyCacheConnection"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								name="#data.names[idx]#">
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
    
<cfset querySort(connections,"name")>
<cfset srcLocal=queryNew("name,class,custom,default,readonly,storage")>
<cfset srcGlobal=queryNew("name,class,custom,default,readonly,storage")>





<cfloop query="connections">
	<cfif not connections.readOnly>
    	<cfset tmp=srcLocal>
	<cfelse>
    	<cfset tmp=srcGlobal>
	</cfif>
	<cfset QueryAddRow(tmp)>
    <cfset QuerySetCell(tmp,"name",connections.name)>
    <cfset QuerySetCell(tmp,"class",connections.class)>
    <cfset QuerySetCell(tmp,"custom",connections.custom)>
    <cfset QuerySetCell(tmp,"default",connections.default)>
    <cfset QuerySetCell(tmp,"storage",connections.storage)>
    <cfset QuerySetCell(tmp,"readonly",connections.readonly)>
</cfloop>
<cfset querySort(connections,"default")>
<cfoutput>

<script>
function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}

function selectAll(field) {
	var form=field.form;
	for(var key in form.elements){
		if((""+form.elements[key].name).indexOf("row_")==0){
			form.elements[key].checked=field.checked;
		}
	}
}
</script>

	<cfif  access NEQ "yes"><cfset noAccess(stText.Settings.cache.noAccess)></cfif>

<!---- READ ONLY ---->
<cfif request.adminType EQ "web" and srcGlobal.recordcount>

<table class="tbl" width="740">
<tr>
		<td colspan="4"><h2>#stText.Settings.cache.titleReadOnly#</h2>
	#stText.Settings.cache.descReadOnly#</td>
	</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
	<tr>
		<cfif access EQ "yes"><td width="20"><input type="checkbox" class="checkbox" name="rowreadonly" onclick="selectAll(this)"></td></cfif>
		<td width="225" class="tblHead" nowrap>#stText.Settings.cache.name#</td>
		<td width="225" class="tblHead" nowrap># stText.Settings.cache.type#</td>
		<td width="50" class="tblHead" nowrap># stText.Settings.cache.storage#</td>
		<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>
	<cfloop query="srcGlobal">
    	<cfset driver=drivers[srcGlobal.class]>
	<tr><cfif access EQ "yes">
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		
        <tr>
			<td>
			<input type="checkbox" class="checkbox" name="row_#srcGlobal.currentrow#" value="#srcGlobal.currentrow#">
			</td>
		</tr>
		</table>
		</td></cfif>
		<td class="tblContent" nowrap><input type="hidden" name="name_#srcGlobal.currentrow#" value="#srcGlobal.name#">#srcGlobal.name#</td>
		<td class="tblContent" nowrap>#driver.getLabel()#</td>
		<td class="tblContent" nowrap>#yesNoFormat(srcGlobal.storage)#</td>
		<td class="tblContent" nowrap valign="middle" align="center">
				<cfif StructKeyExists(stVeritfyMessages, srcGlobal.name)>
					<cfif stVeritfyMessages[srcGlobal.name].label eq "OK">
						<span class="CheckOk">#stVeritfyMessages[srcGlobal.name].label#</span>
					<cfelse>
						<span class="CheckError" title="#stVeritfyMessages[srcGlobal.name].message##Chr(13)#">#stVeritfyMessages[srcGlobal.name].label#</span>
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							title="#stVeritfyMessages[srcGlobal.name].message##Chr(13)#">
					</cfif>
				<cfelse>
					&nbsp;				
				</cfif>
			</td>
	</tr>
	</cfloop>
    <cfif access EQ "yes">
	<tr>
		<td colspan="4">
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
    </cfif>
</cfform>
</table><br /><br />
</cfif>

<!--- LIST CACHE --->
<cfif srcLocal.recordcount and access EQ "yes">

<table class="tbl" width="740" border="0">
<tr>
		<td colspan="4"><h2>#stText.Settings.cache.titleExisting#</h2>#stText.Settings.cache.descExisting#</td>
	</tr>
	
<cfform action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td width="60"><input type="checkbox" class="checkbox" name="rowreadonly" onclick="selectAll(this)"></td>
		<td width="280" class="tblHead" nowrap>#stText.Settings.cache.name#</td>
		<td width="280" class="tblHead" nowrap># stText.Settings.cache.type#</td>
		<td width="50" class="tblHead" nowrap># stText.Settings.cache.storage#</td>
		<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>
	<cfloop query="srcLocal">
    	<cftry>
    		<cfset driver=drivers[srcLocal.class]>
        	<cfcatch><cfcontinue></cfcatch>
        </cftry>
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
			<input type="checkbox" class="checkbox" name="row_#srcLocal.currentrow#" value="#srcLocal.currentrow#">
			</td>
            <td>
            <a href="#request.self#?action=#url.action#&action2=create&name=#Hash(srcLocal.name)#">
			<cfmodule template="img.cfm" src="edit.png" hspace="2" border="0"></a>
            </td>
		</tr>
		</table>
		</td>
		<td class="tblContent" nowrap><input type="hidden" name="name_#srcLocal.currentrow#" value="#srcLocal.name#">#srcLocal.name#</td>
		<td class="tblContent" nowrap>#driver.getLabel()#</td>
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
	<tr>
		<td colspan="4">
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
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.delete#">
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
	select default cache --->
<cfif connections.recordcount and access EQ "yes">
<cfoutput>
	
	
    
	<table class="tbl" width="740">
    <tr>
		<td colspan="2"><h2>#stText.Settings.cache.defaultTitle#</h2>#stText.Settings.cache.defaultDesc#</td>
	</tr>
	<cfform action="#request.self#?action=#url.action#" method="post">
    <cfloop index="type" list="object,template,query,resource"><!---  --->
	<tr>
		<td class="tblHead" width="50">#stText.Settings.cache['defaulttype'& type]#</td>
		<td class="tblContent" width="300"><select name="#type#">
					<option value="">------</option>
                    <cfloop query="connections">
                    <option value="#connections.name#" <cfif connections.default EQ type>selected="selected"</cfif>>#connections.name#</option>
					</cfloop>
				</select>
                <br /><span class="comment">#stText.Settings.cache['defaulttype' &type& 'Desc']#</span></td>
	</tr>
    </cfloop>
	<tr>
		<td colspan="2">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.update#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
		</td>
	</tr>
	</cfform>
	</table>   
	<br><br>   
	</cfoutput>
</cfif>
<!--- 
	Create Datasource --->
<cfif access EQ "yes">
<cfoutput>
	<cfset _drivers=ListSort(StructKeyList(drivers),'textnocase')>
	
    <cfif listLen(_drivers)>
    
	<table class="tbl" width="350">
    
	<tr>
		<td colspan="2"><h2>#stText.Settings.cache.titleCreate#</h2></td>
	</tr>
	<cfform action="#request.self#?action=#url.action#&action2=create" method="post">
	<tr>
		<td class="tblHead" width="50">#stText.Settings.cache.Name#</td>
		<td class="tblContent" width="300"><cfinput type="text" name="_name" value="" style="width:300px" required="yes" 
			message="#stText.Settings.cache.nameMissing#"></td>
	</tr>
	
	<tr>
		<td class="tblHead" width="50">#stText.Settings.cache.type#</td>
		<td class="tblContent" width="300"><select name="class">
					<cfloop list="#_drivers#" index="key">
                    <cfset driver=drivers[key]>
                    <cfset v=trim(driver.getClass())>
					<option value="#v#">#trim(driver.getLabel())#</option>
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
    #stText.Settings.cache.noDriver#
    </cfif>
	</cfoutput>
</cfif>

    

    
