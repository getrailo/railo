<cfset error.message="">
<cfset error.detail="">

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
<cfobjectcache action="size" result="qrySize">



<cfset btnClearTemplateCache=replace(stText.setting.templateCacheClear,'{count}',arrayLen(pagePoolList()))>
<cfset btnClearQueryCache=replace(stText.setting.queryCacheClear,'{count}',qrySize)>




<cftry>
	<cfswitch expression="#form.mainAction#">
	
		<cfcase value="#btnClearTemplateCache#">
			<cfset pagePoolClear()>
		</cfcase>
        <cfcase value="#btnClearQueryCache#">
        	<cfobjectcache action="clear">
		</cfcase>
        <!--- Update ---->
        <cfcase value="#stText.Buttons.Update#">
			<cfadmin 
				action="updatePerformanceSettings"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				inspectTemplate="#form.inspectTemplate#"
				
				remoteClients="#request.getRemoteClients()#"
				>
		
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfadmin 
				action="updatePerformanceSettings"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				inspectTemplate=""
				
				remoteClients="#request.getRemoteClients()#"
				>
		
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<cfadmin 
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	action="getPerformanceSettings"
	returnVariable="Settings">
	
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




<table class="tbl" width="700">
<colgroup>
    <col width="150">
    <col width="550">
</colgroup>
<tr>
	<td colspan="2">
	#stText.setting.cacheDesc#
	</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">

<!--- Template Cache for Request --->
<tr>
	<td class="tblHead" width="150">#stText.setting.inspectTemplate#</td>
	<td class="tblContent">
    	<!--- never --->
    	<input class="radio" type="radio" name="inspectTemplate" value="never"<cfif settings.inspectTemplate EQ "never"> checked="checked"</cfif>>
    	<b>#stText.setting.inspectTemplateNever#</b><br />
		<span class="comment">#stText.setting.inspectTemplateNeverDesc#</span><br>
    	<!--- once --->
    	<input class="radio" type="radio" name="inspectTemplate" value="once"<cfif settings.inspectTemplate EQ "once"> checked="checked"</cfif>>
    	<b>#stText.setting.inspectTemplateOnce#</b><br />
		<span class="comment">#stText.setting.inspectTemplateOnceDesc#</span><br>
    	<!--- always --->
    	<input class="radio" type="radio" name="inspectTemplate" value="always"<cfif settings.inspectTemplate EQ "always"> checked="checked"</cfif>>
    	<b>#stText.setting.inspectTemplateAlways#</b><br />
		<span class="comment">#stText.setting.inspectTemplateAlwaysDesc#</span>
        
	</td>
</tr>
<!--- PagePool --->
<tr>
	<td class="tblHead" width="150">#stText.setting.templateCache#</td>
	<td class="tblContent" style="padding:10px">
    	
      <input class="submit" type="submit" class="submit" name="mainAction" value="#btnClearTemplateCache#">
        <br /><span class="comment">#stText.setting.templateCacheClearDesc#</span>
        
	</td>
</tr>

<!--- Object Cache --->
<tr>
	<td class="tblHead" width="150">#stText.setting.queryCache#</td>
	<td class="tblContent" style="padding:10px">
        <input class="submit" type="submit" class="submit" name="mainAction" value="#btnClearQueryCache#">
        <br /><span class="comment">#stText.setting.queryCacheClearDesc#</span>
	</td>
</tr>



<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		
      <input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.update#">
		<input class="submit" type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>

<br><br>