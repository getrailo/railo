<cfset Component=createObject("java","railo.runtime.Component")>
<cfset REMOTE=Component.ACCESS_REMOTE>
<cfset PUBLIC=Component.ACCESS_PUBLIC>
<cfset PACKAGE=Component.ACCESS_PACKAGE>
<cfset PRIVATE=Component.ACCESS_PRIVATE>


<cfset error.message="">
<cfset error.detail="">

<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
			<cfset admin.updateBaseComponent(form.baseComponent)>
			<cfset admin.updateComponentDataMemberDefaultAccess(form.dataMemberDefaultAccess)>
			<cfset admin.store()>
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
<cfoutput>#stText.Components.Component#
<table class="tbl">
<cfform action="#request.self#?action=#url.action#" method="post">
<tr>
	<td class="tblHead" width="150">#stText.Components.BaseComponent#</td>
	<td class="tblContent">
		#stText.Components.BaseComponentDescription#
		<cfinput type="text" name="baseComponent" value="#admin.baseComponent#" style="width:350px" 
		required="no" 
		message="#stText.Components.BaseComponentMissing#">
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Components.DataMemberAccessType#</td>
	<td class="tblContent">
		<cfset access=config.componentDataMemberDefaultAccess>  
		#stText.Components.DataMemberAccessTypeDescription#
      	 <select name="dataMemberDefaultAccess" style="width:200px">
				<option value="#PRIVATE#" <cfif access EQ PRIVATE>selected</cfif>>#stText.Components.DMATPrivate#</option>
				<option value="#PACKAGE#" <cfif access EQ PACKAGE>selected</cfif>>#stText.Components.DMATPackage#</option>
				<option value="#PUBLIC#" <cfif access EQ PUBLIC>selected</cfif>>#stText.Components.DMATPublic#</option>
				<option value="#REMOTE#" <cfif access EQ REMOTE>selected</cfif>>#stText.Components.DMATRemote#</option>
		</select>
	</td>
</tr>
<tr>
	<td colspan="2">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
	</td>
</tr>
</cfform></cfoutput>
</table>
<br><br>