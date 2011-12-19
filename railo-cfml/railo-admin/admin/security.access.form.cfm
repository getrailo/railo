<cfparam name="stVeritfyMessages" default="#struct()#">

<cffunction name="def" returntype="string" output="false">
	<cfargument name="key" type="string">
	<cfreturn "">
	<cfif not structKeyExists(variables,"daccess")>
		<cfreturn "">
	<cfelseif variables.daccess[key]>
		<cfreturn "Green">
	</cfif>
		<cfreturn "Red">
</cffunction>

<cfoutput>

<table class="tbl" width="600">
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>


<tr>
	<td colspan="2"><h2>#stText.Security.general#</h2>
		#stText.Security.generalDesc#
	</td>
</tr>

<cfform action="#go(url.action,"update#iif(type EQ "generell",de('Default'),de(''))#SecurityManager")#" method="post">
<cfset values=struct(
			'open':"open",
			'protected':"password protected",
			'close':"closed"
		)>
<!--- Access Read --->
<tr>
	<td class="tblHead" width="150">#stText.Security.accessRead#</td>
	<td class="tblContent#def('access_read')#">
		<span class="comment">#stText.Security.accessReadDesc#</span>
		
		<br /><select name="#prefix#access_read">
			<cfoutput><cfloop collection="#values#" item="idx" ><option value="#idx#" <cfif access.access_read eq idx>selected="selected"</cfif>><cfif structKeyExists(stText.Security.DatasourceTextes,idx)>#stText.Security.DatasourceTextes[idx]#<cfelse>#values[idx]#</cfif></option></cfloop></cfoutput>
		</select>
		<!--- input type="checkbox" class="checkbox" name="#prefix#Datasource" value="yes" <cfif access.datasource>checked</cfif> --->
		
	</td>
</tr>


<!--- Access Write --->
<tr>
	<td class="tblHead" width="150">#stText.Security.accessWrite#</td>
	<td class="tblContent#def('access_write')#">
		<span class="comment">#stText.Security.accessWriteDesc#</span>
		<br /><select name="#prefix#access_write">
			<cfoutput><cfloop collection="#values#" item="idx" ><option value="#idx#" <cfif access.access_write eq idx>selected="selected"</cfif>><cfif structKeyExists(stText.Security.DatasourceTextes,idx)>#stText.Security.DatasourceTextes[idx]#<cfelse>#values[idx]#</cfif></option></cfloop></cfoutput>
		</select>
		<!--- input type="checkbox" class="checkbox" name="#prefix#Datasource" value="yes" <cfif access.datasource>checked</cfif> --->
		
	</td>
</tr>




<tr>
	<td colspan="2"><br /><h2>#stText.Security.WebAdministrator#</h2>
		#stText.Security.WebAdministratorDescription#
	</td>
</tr>

<!--- <cfform action="#go(url.action,"update#iif(type EQ "generell",de('Default'),de(''))#SecurityManager")#" method="post">--->

<!--- Setting --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Settings#</td>
	<td class="tblContent#def('setting')#">
		<input type="checkbox" class="checkbox" name="#prefix#Setting" value="yes" <cfif access.setting>checked</cfif>>
		<span class="comment">#stText.Security.SettingsDescription#</span>
	</td>
</tr>

<!--- Mail --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Mail#</td>
	<td class="tblContent#def('mail')#">
		<input type="checkbox" class="checkbox" name="#prefix#Mail" value="yes" <cfif access.mail>checked</cfif>>
		<span class="comment">#stText.Security.MailDescription#</span>
	</td>
</tr> 

<!--- Datasource --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Datasource#</td>
	<td class="tblContent#def('datasource')#">
		<span class="comment">#stText.Security.DatasourceDescription#</span>
		<cfset values=struct(
			'-1':"yes",
			'0':"no",
			'1':"1",
			'2':"2",
			'3':"3",
			'4':"4",
			'5':"5",
			'6':"6",
			'7':"7",
			'8':"8",
			'9':"9",
			'10':"10"
		)>
		<br /><select name="#prefix#Datasource">
			<cfoutput><cfloop index="idx" from="-1" to="10" ><option value="#values[idx]#" <cfif access.datasource eq idx>selected="selected"</cfif>><cfif structKeyExists(stText.Security.DatasourceTextes,idx)>#stText.Security.DatasourceTextes[idx]#<cfelse>#idx#</cfif></option></cfloop></cfoutput>
		</select>
		<!--- input type="checkbox" class="checkbox" name="#prefix#Datasource" value="yes" <cfif access.datasource>checked</cfif> --->
		
	</td>
</tr>

<!--- Mapping --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Mapping#</td>
	<td class="tblContent#def('mapping')#">
		<input type="checkbox" class="checkbox" name="#prefix#Mapping" value="yes" <cfif access.mapping>checked</cfif>>
		<span class="comment">#stText.Security.MappingDescription#</span>
	</td>
</tr>

<!--- Remote --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Remote#</td>
	<td class="tblContent#def('remote')#">
		<input type="checkbox" class="checkbox" name="#prefix#Remote" value="yes" <cfif access.remote>checked</cfif>>
		<span class="comment">#stText.Security.RemoteDescription#</span>
	</td>
</tr>
<!--- CustomTag --->
<tr>
	<td class="tblHead" width="150">#stText.Security.CustomTag#</td>
	<td class="tblContent#def('custom_tag')#">
		<input type="checkbox" class="checkbox" name="#prefix#CustomTag" value="yes" <cfif access.custom_tag>checked</cfif>>
		<span class="comment">#stText.Security.CustomTagDescription#</span>
	</td>
</tr>

<!--- CFX Setting --->
<tr>
	<td class="tblHead" width="150">#stText.Security.CFX#</td>
	<td class="tblContent#def('cfx_setting')#">
		<input type="checkbox" class="checkbox" name="#prefix#CFXSetting" value="yes" <cfif access.cfx_setting>checked</cfif>>
		<span class="comment">#stText.Security.CFXDescription#</span>
	</td>
</tr>

<!--- Cache --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Cache#</td>
	<td class="tblContent#def('cache')#">
		<input type="checkbox" class="checkbox" name="#prefix#Cache" value="yes" <cfif access.cache>checked</cfif>>
		<span class="comment">#stText.Security.CacheDescription#</span>
	</td>
</tr>

<!--- Gateway --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Gateway#</td>
	<td class="tblContent#def('gateway')#">
		<input type="checkbox" class="checkbox" name="#prefix#Gateway" value="yes" <cfif access.gateway>checked</cfif>>
		<span class="comment">#stText.Security.GatewayDescription#</span>
	</td>
</tr>
<!--- ORM --->
<tr>
	<td class="tblHead" width="150">#stText.Security.orm#</td>
	<td class="tblContent#def('orm')#">
		<input type="checkbox" class="checkbox" name="#prefix#Orm" value="yes" <cfif access.orm>checked</cfif>>
		<span class="comment">#stText.Security.ormDescription#</span>
	</td>
</tr>
<!--- Debugging --->
<tr>
	<td class="tblHead" width="150">#stText.Security.Debugging#</td>
	<td class="tblContent#def('debugging')#">
		<input type="checkbox" class="checkbox" name="#prefix#Debugging" value="yes" <cfif access.debugging>checked</cfif>>
		<span class="comment">#stText.Security.DebuggingDescription#</span>
	</td>
</tr>
<!--- Search
<tr>
	<td class="tblHead" width="150">#stText.security.search#</td>
	<td class="tblContent" style="#def('search')#">
		<input type="checkbox" class="checkbox" name="#prefix#Search" value="yes" <cfif access.search>checked</cfif>>
		<span class="comment">#stText.Security.SearchDescription#</span>
	</td>
</tr> --->
<input type="hidden" name="#prefix#Search" value="yes">
<!--- Scheduled Task 
<tr>
	<td class="tblHead" width="150">#stText.Security.ScheduledTask#</td>
	<td class="tblContent" style="#def('scheduled_task')#">
		<input type="checkbox" class="checkbox" name="#prefix#ScheduledTask" value="yes" <cfif access.scheduled_task>checked</cfif>>
		<span class="comment">#stText.Security.ScheduledTaskDescription#</span>
	</td>
</tr>
--->







<input type="hidden" name="#prefix#ScheduledTask" value="yes">
<tr>
	<td colspan="2"><br><h2>#stText.Security.CFMLEnvironment#</h2>
	#stText.Security.CFMLEnvironmentDescription#
	</td>
</tr>
<!--- File --->
<tr>
	<td class="tblHead" width="150">#stText.Security.File#</td>
	<td class="tblContent"><span class="comment">#stText.Security.FileDescription#</span><select name="#prefix#File" onChange="changeFileAccessVisibility('fileAccess',this)">
			<option <cfif access.file EQ "all">selected</cfif>>#stText.Security.FileAll#</option>
			<option <cfif access.file EQ "local">selected</cfif>>#stText.Security.FileLocal#</option>
			<option <cfif access.file EQ "none">selected</cfif>>#stText.Security.FileNone#</option>
		</select>
	
    
    <script>
function changeFileAccessVisibility(name,field){
	var display=0;
	if(field){
		display=field.value!='local'?1:2;
	}
	var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName(name);
	var s=null;
	for(var i=0;i<tds.length;i++) {
		if(tds[i].name && tds[i].name!=name)continue;
		s=tds[i].style;
		if(display==1) s.display='none';
		else if(display==2) s.display='';
		else if(s.display=='none') s.display='';
		else s.display='none';
	}
}

</script>

   


<table class="tbl" width="400">
<tr>
	<td ><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<tr  name="fileAccess" style="display:#access.file EQ 'local'?'':'none'#">
	<td colspan="5">#stText.Security.FileCustom#<br />
<span class="comment">#stText.Security.FileCustomDesc#</span></td>
</tr>
<tr name="fileAccess" style="display:#access.file EQ 'local'?'':'none'#">
	<td width="350" class="tblHead" nowrap>#stText.Security.FilePath#</td>
</tr>

	<cfloop index="idx" from="1" to="#arrayLen(access.file_access)#">
		<tr name="fileAccess" style="display:#access.file EQ 'local'?'':'none'#">
		<!--- path --->
			<td class="tblContent" nowrap><cfinput 
				type="text" name="path_#idx#" 
				value="#access.file_access[idx]#" required="no"  style="width:400px"></td>
		
		</tr>
	</cfloop>
	<!--- INSERT --->
	
		<tr name="fileAccess" style="display:#access.file EQ 'local'?'':'none'#">
			<td class="tblContent" nowrap><cfinput type="text" name="path_#arrayLen(access.file_access)+1#" value="" required="no" style="width:400px"></td>
		</tr>
	
</table>
   

	</td>
</tr>

<!--- Direct Java Access --->
<tr>
	<td class="tblHead" width="150">#stText.Security.JavaAccess#</td>
	<td class="tblContent#def('direct_java_access')#">
		<input type="checkbox" class="checkbox" name="#prefix#DirectJavaAccess" value="yes" <cfif access.direct_java_access>checked</cfif>>
		<span class="comment">#stText.Security.JavaAccessDescription#</span>
	</td>
</tr>


<!--- CFX Usage 
<tr>
	<td class="tblHead" width="150">CFX Usage</td>
	<td class="tblContent">
		<input type="checkbox" class="checkbox" name="#prefix#CFXUsage" value="yes" <cfif access.cfx_usage>checked</cfif>>
		<span class="comment">Enable or disable the CFX functionality for the instaces</span>
	</td>
</tr>--->

<tr>
	<td colspan="2"><br><h2>#stText.Security.Functions#</h2>#stText.Security.FunctionsDescription#</td>
</tr>


<!--- Tags --->
<tr>
	<!--- Execute --->
	<td class="tblHead" width="150">#stText.Security.TagExecute#</td>
	<td class="tblContent#def('tag_execute')#">
		<input type="checkbox" class="checkbox" name="#prefix#TagExecute" value="yes" <cfif access.tag_execute>checked</cfif>>
		<span class="comment">#stText.Security.TagExecuteDescription#</span>
	</td>
</tr>
		<!--- Import --->
<tr>
	<td class="tblHead" width="150">#stText.Security.TagImport#</td>
	<td class="tblContent#def('tag_import')#">
		<input type="checkbox" class="checkbox" name="#prefix#TagImport" value="yes" <cfif access.tag_import>checked</cfif>>
		<span class="comment">#stText.Security.TagImportDescription#</span>
	</td>
</tr>
		<!--- Object --->
<tr>
	<td class="tblHead" width="150">#stText.Security.TagObject#</td>
	<td class="tblContent#def('tag_object')#">
		<input type="checkbox" class="checkbox" name="#prefix#TagObject" value="yes" <cfif access.tag_object>checked</cfif>>
		<span class="comment">#stText.Security.TagObjectDescription#</span>
	</td>
</tr>
		<!--- Registry --->
<tr>
	<td class="tblHead" width="150">#stText.Security.TagRegistry#</td>
	<td class="tblContent#def('tag_registry')#">
		<input type="checkbox" class="checkbox" name="#prefix#TagRegistry" value="yes" <cfif access.tag_registry>checked</cfif>>
		<span class="comment">#stText.Security.TagRegistryDescription#</span>
	</td>
</tr>
		<!--- CFX --->
<tr>
	<td class="tblHead" width="150">#stText.Security.CFXTags#</td>
	<td class="tblContent#def('cfx_usage')#">
		<input type="checkbox" class="checkbox" name="#prefix#CFXUsage" value="yes" <cfif access.cfx_usage>checked</cfif>>
		<span class="comment">#stText.Security.CFXTagsDescription#</span>
	</td>
</tr>


<tr>
	<td colspan="2"><br></td>
</tr>
<cfif type NEQ "special">
<cfmodule template="remoteclients.cfm" colspan="2">
</cfif>
<tr>
	<td colspan="2">
		<input type="hidden" name="mainAction" value="#prefix#Udpate">
		<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Update#">
		<input onClick="window.location='#go(url.action)#';" type="button" class="button" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfform></cfoutput>
</table>

<cfif access.file EQ "all"><script>changeFileAccessVisibility('fileAccess');</script></cfif>
<br><br>

