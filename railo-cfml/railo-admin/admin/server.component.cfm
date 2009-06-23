<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
	secValue="yes">

<cfadmin 
	action="getComponent"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="component">

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
				action="updateComponent"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				baseComponentTemplate="#form.baseComponentTemplate#"
				componentDumpTemplate="#form.componentDumpTemplate#"
				componentDataMemberDefaultAccess="#form.componentDataMemberDefaultAccess#"
				triggerDataMember="#isDefined('form.triggerDataMember')#"
				useShadow="#isDefined('form.useShadow')#"
				remoteClients="#request.getRemoteClients()#"
				>
		
			<!--- <cfset admin.updateBaseComponent(form.baseComponent)>
			<cfset admin.updateComponentDataMemberDefaultAccess(form.dataMemberDefaultAccess)>
			<cfset admin.updateComponentDumpTemplate(form.componentDumpTemplate)>
			<cfset admin.store()> --->
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
<cfoutput><table class="tbl" width="600">
<tr>
	<td colspan="2">
<cfif request.adminType EQ "server">
	#stText.Components.Server#
<cfelse>
	#stText.Components.Web#
</cfif>
	</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
<tr>
	<td class="tblHead" width="150">#stText.Components.BaseComponent#</td>
	<cfset css=iif(len(component.baseComponentTemplate) EQ 0 and len(component.strBaseComponentTemplate) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" title="#component.strBaseComponentTemplate#
#component.BaseComponentTemplate#">
		<span class="comment">#stText.Components.BaseComponentDescription#</span><br>
		<cfif hasAccess>
		<cfinput type="text" name="baseComponentTemplate" value="#component.strBaseComponentTemplate#" style="width:350px" 
			required="no" 
			message="#stText.Components.BaseComponentMissing#">
		<cfelse>
			<br><b>#component.strbaseComponentTemplate#</b>
		</cfif>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Components.ComponentDumpTemplate#</td>
	<cfset css=iif(len(component.componentDumpTemplate) EQ 0 and len(component.strComponentDumpTemplate) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" title="#component.strcomponentDumpTemplate#
#component.componentDumpTemplate#">
		<span class="comment">#stText.Components.ComponentDumpTemplateDescription#</span><br>
		<cfif hasAccess>
			<cfinput type="text" name="componentDumpTemplate" value="#component.strcomponentDumpTemplate#" style="width:350px" 
				required="no" 
				message="#stText.Components.ComponentDumpTemplateMissing#">
		<cfelse>
			<br><b>#component.strcomponentDumpTemplate#</b>
		</cfif>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Components.DataMemberAccessType#</td>
	<td class="tblContent">
		<cfset access=component.componentDataMemberDefaultAccess>  
		<span class="comment">#stText.Components.DataMemberAccessTypeDescription#</span><br>
      	<cfif hasAccess>
		<select name="componentDataMemberDefaultAccess" style="width:200px">
				<option value="private" <cfif access EQ "private">selected</cfif>>#stText.Components.DMATPrivate#</option>
				<option value="package" <cfif access EQ "package">selected</cfif>>#stText.Components.DMATPackage#</option>
				<option value="public" <cfif access EQ "public">selected</cfif>>#stText.Components.DMATPublic#</option>
				<option value="remote" <cfif access EQ "remote">selected</cfif>>#stText.Components.DMATRemote#</option>
		</select>
		<cfelse>
		<br><b>#access#</b>
		</cfif>
	</td>
</tr>
<!---
Trigger Data Member --->
<tr>
	<td class="tblHead" width="150">#stText.Components.triggerDataMember#</td>
	<td class="tblContent">
		<cfif hasAccess>
		<input class="checkbox" type="checkbox" class="checkbox" name="triggerDataMember" 
			value="yes" <cfif component.triggerDataMember>checked</cfif>>
		<cfelse>
		<br><b>#iif(component.triggerDataMember,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Components.triggerDataMemberDescription#</span><br>
      	
	</td>
</tr>
<!---
Use Shadow --->
<tr>
	<td class="tblHead" width="150">#stText.Components.useShadow#</td>
	<td class="tblContent">
		<cfif hasAccess>
		<input class="checkbox" type="checkbox" class="checkbox" name="useShadow" 
			value="yes" <cfif component.useShadow>checked</cfif>>
		<cfelse>
		<br><b>#iif(component.useShadow,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Components.useShadowDescription#</span><br>
      	
	</td>
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">

<tr>
	<td colspan="2">
		<input class="submit" type="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.cancel#">
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>
<br><br>