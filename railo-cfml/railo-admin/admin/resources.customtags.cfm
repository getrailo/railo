<!--- <cfif isDefined("form")>
	<cfinclude template="act/resources.act_mapping.cfm">
</cfif> --->
<cfset error.message="">
<cfset error.detail="">


<cfscript>
function arrayRemoveValue(arr,value){
	var index=arrayFindNoCase(arr,value);
	if(index GT 0)ArrayDeleteAt(arr,index);
}
</cfscript>

<!--- 
Defaults --->
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfparam name="error" default="#struct(message:"",detail:"")#">

<!--- <cfset hasAccess=securityManager.getAccess("custom_tag") EQ ACCESS.YES> --->
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="custom_tag"
	secValue="yes">


<cfset flushName="#stText.Buttons.flush# (#structCount(ctCacheList())#)">
<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
    <cfcase value="#flushName#">
    	 <cfset ctCacheClear()>
    </cfcase>
		<cfcase value="#stText.Buttons.Update#">
		<!--- update --->
			<cfif form.subAction EQ "setting">
            	<cfif form.extensions EQ "custom">
                	<cfset form.extensions=form.extensions_custom>
                </cfif>
            
				<cfadmin 
						action="updateCustomTagSetting"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						deepSearch="#isDefined('form.customTagDeepSearchDesc') and form.customTagDeepSearchDesc EQ true#"
						localSearch="#isDefined('form.customTagLocalSearchDesc') and form.customTagLocalSearchDesc EQ true#"
						customTagPathCache="#isDefined('form.customTagPathCache') and form.customTagPathCache EQ true#"
                        
                        
                        
						extensions="#form.extensions#"
			remoteClients="#request.getRemoteClients()#">
			<cfelseif form.subAction EQ "#stText.Buttons.Update#">
				<cfset data.virtuals=toArrayFromForm("virtual")>
				<cfset data.physicals=toArrayFromForm("physical")>
				<cfset data.archives=toArrayFromForm("archive")>
				<cfset data.primaries=toArrayFromForm("primary")>
				<cfset data.trusteds=toArrayFromForm("trusted")>
				<cfset data.rows=toArrayFromForm("row")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.physicals)#">
					<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">
						<cfset data.trusteds[idx]=isDefined("data.trusteds[#idx#]") and data.trusteds[idx]>
						
						
					<cfadmin 
						action="updateCustomTag"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						virtual="#data.virtuals[idx]#"
						physical="#data.physicals[idx]#"
						archive="#data.archives[idx]#"
						primary="#data.primaries[idx]#"
						trusted="#data.trusteds[idx]#"
			remoteClients="#request.getRemoteClients()#">

<!--- 						<cfset admin.updateCustomTag(data.virtuals[idx],
						data.physicals[idx],data.archives[idx],data.primaries[idx],data.trusteds[idx])> --->
					</cfif>
				</cfloop>
		
			<cfelseif form.subAction EQ "#stText.Buttons.Delete#">
				<cfset data.virtuals=toArrayFromForm("virtual")>
				<cfset data.rows=toArrayFromForm("row")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.virtuals)#">
					
					<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">
						<cfadmin 
							action="removeCustomTag"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							
							virtual="#data.virtuals[idx]#"
			remoteClients="#request.getRemoteClients()#">
					
						<!--- <cfset admin.removeCustomTag(data.virtuals[idx])> --->
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
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output--->
<cfif error.message NEQ "">
<cfoutput><span class="CheckError">
#error.message#<br>
#error.detail#
</span><br><br></cfoutput>
</cfif>

<cfadmin 
	action="getCustomTagMappings"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="mappings">
	
	

<cfadmin 
	action="getCustomtagSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="setting">

<!--- 
list all mappings and display necessary edit fields --->
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


function checkTheRadio(field) {
	
	var radios=field.form['extensions'];
	radios[radios.length-1].checked=true;
}

</script>

<cfoutput>

<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>

<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#stText.CustomTags.CustomtagSetting#</h2></td>
</tr>


<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
<input type="hidden" name="subAction" value="setting" />
<tr>
	<td class="tblHead" width="150">#stText.CustomTags.customTagDeepSearch#</td>
	<td class="tblContent">
	<cfif hasAccess>
    	<input type="checkbox" class="checkbox" name="customTagDeepSearchDesc" value="yes" <cfif setting.deepsearch>checked</cfif>>
    <cfelse>
    	<b>#yesNoFormat(setting.deepsearch)#</b>
	</cfif>
    
    <span class="comment">#stText.CustomTags.customTagDeepSearchDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.CustomTags.customTagLocalSearch#</td>
	<td class="tblContent">
	<cfif hasAccess>
    	<input type="checkbox" class="checkbox" name="customTagLocalSearchDesc" value="yes" <cfif setting.localsearch>checked</cfif>>
    <cfelse>
    	<b>#yesNoFormat(setting.localsearch)#</b>
	</cfif>
	<span class="comment">#stText.CustomTags.customTagLocalSearchDesc#</span></td>
	
</tr>

<!--- component path cache ---->
<tr>
	<td class="tblHead" width="150">#stText.CustomTags.customTagPathCache#</td>
	<td class="tblContent">
	<cfif hasAccess>
    	<input type="checkbox" class="checkbox" name="customTagPathCache" value="yes" <cfif setting.customTagPathCache>checked</cfif>>
    <cfelse>
    	<b>#yesNoFormat(setting.customTagPathCache)#</b>
	</cfif>
	<span class="comment">#stText.CustomTags.customTagPathCacheDesc#</span>
	<cfif setting.customTagPathCache><br /><input type="submit" class="submit" name="mainAction" value="#flushName#"></cfif></td>
</tr>



<cfset arrExt=array('cfc','cfm','cfml')>
<cfset lstSetExt=ArrayToList(setting.extensions)>
<tr>
	<td class="tblHead" width="150">#stText.CustomTags.extensions#</td>
	<td class="tblContent">
    
    	<cfset modes=array(
			struct(mode:'classic',ext:'cfm'),
			struct(mode:'standard',ext:'cfm,cfml'),
			struct(mode:'mixed',ext:'cfm,cfc'),
			struct(mode:'modern',ext:'cfc')
		
		)>
    	<cfif hasAccess>
      	<cfset has=false>
        <table class="tbl">
        <cfloop array="#modes#" index="mode">
        <tr>
            <td><input type="radio" class="checkbox" name="extensions" value="#mode.ext#"<cfif mode.ext EQ lstSetExt> checked="checked"<cfset has=true></cfif>></td>
            <td><table class="tbl">
                    <tr>
                        <td class="tblContent" style="width:100px"><b>#mode.ext#</b></td>
                        <td class="comment"><span >#stText.CustomTags.mode[mode.mode]#</span></td>
                    </tr>
                </table></td>
        </tr>
        </cfloop>
        <tr>
            <td><input type="radio" class="checkbox" name="extensions" value="custom"<cfif not has> checked="checked"</cfif>></td>
            <td><input type="text"  onClick="checkTheRadio(this)" name="extensions_custom" value="#ArrayToList(setting.extensions)#" required="no"  style="width:113px">
            <span class="comment">#stText.CustomTags.mode.custom#</span></td>
        </tr>
        </table>
        <cfelse>
        <b>#lstSetExt#</b><br />
        </cfif>
        <span class="comment">#stText.CustomTags.extensionsDesc#</span>
    
    
    </td>
    
    
    
    
    
    
    
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr></cfif>
</cfform>
</cfoutput>
</table>
<br><br>


<cfoutput>

<table class="tbl" width="740">
<tr>
	<td colspan="5"><h2>#stText.CustomTags.CustomtagMappings#</h2>
#stText.CustomTags.CustomtagMappingsDesc#</td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td><cfif hasAccess><input type="checkbox" class="checkbox" 
			name="rro" onclick="selectAll(this)"></cfif></td>
		<td class="tblHead" nowrap>#stText.CustomTags.Physical#</td>
		<td class="tblHead" nowrap>#stText.CustomTags.Archive#</td>
		<td class="tblHead" nowrap>#stText.CustomTags.Primary#</td>
		<td class="tblHead" nowrap>#stText.CustomTags.Trusted#</td>
	</tr>
	<cfset count=0>

<cfloop query="mappings">
		<!--- and now display --->
	<tr>
		<td height="28">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><cfif not mappings.ReadOnly><cfset count=count+1>
			<input type="hidden" name="virtual_#mappings.currentrow#" value="#mappings.virtual#"><input type="checkbox" class="checkbox" 
			name="row_#mappings.currentrow#" value="#mappings.currentrow#">
			</cfif></td>
		</tr>
		</table>
		
		</td>
		<cfset css=iif(len(mappings.physical) EQ 0 and len(mappings.strPhysical) NEQ 0,de('Red'),de(''))>
		<td class="tblContent#css#" title="#mappings.strphysical#
#mappings.physical#" nowrap><cfif mappings.ReadOnly>#cut(mappings.strphysical,40)#<cfelse><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#mappings.currentrow#" value="#mappings.strphysical#" required="no"  
			style="width:260px" 
			message="#stText.CustomTags.PhysicalMissing##mappings.currentrow#)"></cfif></td>
		
		<cfset css=iif(len(mappings.archive) EQ 0 and len(mappings.strArchive) NEQ 0,de('Red'),de(''))>
		<td class="tblContent#css#" title="#mappings.strarchive#
#mappings.archive#" nowrap><cfif mappings.ReadOnly>#cut(mappings.strarchive,40)#<cfelse><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="archive_#mappings.currentrow#" value="#mappings.strarchive#" required="no"  
			style="width:260px" 
			message="#stText.CustomTags.ArchiveMissing##mappings.currentrow#)"></cfif></td>
		
		<td class="tblContent" nowrap><cfif mappings.ReadOnly><cfif mappings.physicalFirst>physical<cfelse>archive</cfif><cfelse><select name="primary_#mappings.currentrow#" onChange="checkTheBox(this)">
			<option value="physical" <cfif mappings.physicalFirst>selected</cfif>>#stText.CustomTags.physical#</option>
			<option value="archive" <cfif not mappings.physicalFirst>selected</cfif>>#stText.CustomTags.archive#</option>
		</select></cfif></td>
		
		<td class="tblContent" nowrap><cfif mappings.readOnly>#iif(mappings.Trusted,de("Yes"),de("No"))#<cfelse><input type="checkbox" class="checkbox" 
		name="trusted_#mappings.currentrow#" onClick="checkTheBox(this)" value="yes" <cfif mappings.trusted>checked</cfif>></cfif></td>
	</tr>
</cfloop>
<cfif hasAccess>
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#mappings.recordcount+1#" value="#mappings.recordcount+1#">
			<input type="hidden" name="virtual_#mappings.recordcount+1#" value="/#mappings.recordcount+1#"></td>
		</tr>
		</table>
		
		</td>
		<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#mappings.recordcount+1#" value="" required="no"  style="width:260px"></td>
		<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="archive_#mappings.recordcount+1#" value="" required="no"  style="width:260px" ></td>
		<td class="tblContent" nowrap><select name="primary_#mappings.recordcount+1#" onChange="checkTheBox(this)">
			<option value="physical" selected>#stText.CustomTags.physical#</option>
			<option value="archive">#stText.CustomTags.archive#</option>
		</select></td>
		<td class="tblContent" nowrap><input onClick="checkTheBox(this)" type="checkbox" class="checkbox" 
		name="trusted_#mappings.recordcount+1#" value="yes"></td>
	</tr>
</cfif>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="8" line>
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