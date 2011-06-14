<!--- <cfset classConfig=createObject("java","railo.runtime.config.ConfigWeb")>
<cfset STRICT=classConfig.SCOPE_STRICT>
<cfset SMALL=classConfig.SCOPE_SMALL>
<cfset STANDART=classConfig.SCOPE_STANDART> --->
<cfset error.message="">
<cfset error.detail="">
<!--- <cfset hasAccess=securityManager.getAccess("setting") EQ ACCESS.YES>

<cfset hasAccess=securityManagerGet("setting","yes")> --->


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

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
    
		<cfcase value="#stText.Buttons.Update#">
			<cfif form.locale EQ "other">
				<cfset form.locale=form.locale_other>
			</cfif>
			
			<cfadmin 
				action="updateRegional"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				timezone="#form.timezone#"
				locale="#form.locale#"
				timeserver="#form.timeserver#"
				remoteClients="#request.getRemoteClients()#"
				>
		
		</cfcase>
        <!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfif form.locale EQ "other">
				<cfset form.locale=form.locale_other>
			</cfif>
			
			<cfadmin 
				action="updateRegional"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				timezone=""
				locale=""
				timeserver=""
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
	action="getLocales"
	locale="#stText.locale#"
	returnVariable="locales">
	
<cfadmin 
	action="getTimeZones"
	locale="#stText.locale#"
	returnVariable="timezones">
<cfadmin 
	action="getRegional"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="regional">


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


<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>




<table class="tbl" width="740">
<colgroup>
    <col width="150">
    <col width="590">
</colgroup>
<tr>
	<td colspan="2">
<cfif request.adminType EQ "server">
	#stText.Regional.Server#
</cfif>
	#stText.Regional.Web#
	</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
<!---
replaced with encoding output
<tr>
	<td class="tblHead" width="150">#stText.Regional.DefaultEncoding#</td>
	<td class="tblContent">
		<span class="comment">#stText.Regional.DefaultEncodingDescription#</span>
		<cfif hasAccess>
		<cfinput type="text" name="defaultencoding" value="#regional.defaultEncoding#" 
			style="width:200px" required="yes" message="#stText.regional.missingEncoding#">
		
		<cfelse>
			<input type="hidden" name="defaultencoding" value="#regional.defaultEncoding#">
		
			<b>#regional.defaultEncoding#</b>
		</cfif>
	</td>
</tr>
--->

<tr>
	<td class="tblHead" width="150">#stText.Regional.Locale#</td>
	<td class="tblContent">
		<span class="comment">#stText.Regional.LocaleDescription#</span><br>
		<cfif hasAccess>
		<cfset hasFound=false>
		<cfset keys=structSort(locales,'textnocase')>
		
        
        
        <select name="locale">
			<option selected value=""> --- #stText.Regional.ServerProp[request.adminType]# --- </option>
			 ---><cfloop collection="#keys#" item="i"><cfset key=keys[i]><option value="#key#" <cfif key EQ regional.locale>selected<cfset hasFound=true></cfif>>#locales[key]#</option><!--- 
			 ---></cfloop>
		</select>
        
		<!--- <input type="text" name="locale_other" value="<cfif not hasFound>#regional.locale#</cfif>" style="width:200px"> --->
		<cfelse>
			<b>#regional.locale#</b>
		</cfif>
	</td>
</tr>
<cfquery name="timezones" dbtype="query">
	select * from timezones order by id,display
</cfquery>
<tr>
	<td class="tblHead" width="150">#stText.Regional.TimeZone#</td>
	<td class="tblContent">
		<span class="comment">#stText.Regional.TimeZoneDescription#</span>
		<cfif hasAccess>
		<select name="timezone">
			<option selected value=""> --- #stText.Regional.ServerProp[request.adminType]# --- </option>
			<cfoutput query="timezones">
				<option value="#timezones.id#"
				<cfif timezones.id EQ regional.timezone>selected</cfif>>
				#timezones.id# - #timezones.display#</option>
			</cfoutput>
		</select>
		<cfelse>
			<b>#regional.timezone#</b>
		</cfif>
		<!--- <cfinput type="text" name="timezone" value="#config.timezone.getId()#" style="width:200px" required="yes" message="Missing value for timezone"> --->
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Regional.TimeServer#</td>
	<td class="tblContent"><span class="comment">#stText.Regional.TimeServerDescription#</span>
	<cfif hasAccess>
		<br /><cfinput type="text" name="timeserver" value="#regional.timeserver#" 
			style="width:200px" required="no" message="#stText.Regional.TimeServerMissing#">
			
	<cfelse>
		<b>#regional.timeserver#</b>
	</cfif>
	</td>
</tr>
<tr>
	<td colspan="2">
	<span class="comment">
		#stText.Overview.ServerTime#
		#stText.Overview.DateTime#
		#dateFormat(nowServer(),"mm/dd/yyyy")#
		#timeFormat(nowServer(),"HH:mm:ss")#<br>
        
		#stText.Overview.DateTime#
		#dateFormat(now(),"mm/dd/yyyy")#
		#timeFormat(now(),"HH:mm:ss")#<br>
        
        
        
	</span>
	</td>
</tr>
<cfif hasAccess>

<cfmodule template="remoteclients.cfm" colspan="2">


<tr>
	<td colspan="2">
		<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input class="submit" type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>
<br><br>