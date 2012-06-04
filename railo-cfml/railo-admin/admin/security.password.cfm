<!--- <cfset classConfig=createObject("java","railo.runtime.config.ConfigWeb")>
<cfset STRICT=classConfig.SCOPE_STRICT>
<cfset SMALL=classConfig.SCOPE_SMALL>
<cfset STANDART=classConfig.SCOPE_STANDART> --->
<cfset error.message="">
<cfset error.detail="">
<!--- <cfset hasAccess=securityManager.getAccess("setting") EQ ACCESS.YES>

<cfset hasAccess=securityManagerGet("setting","yes")> --->


<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- save settings --->
		<cfcase value="#stText.Buttons.save#">
			<cfadmin 
					action="updateLoginSettings"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
                    
					captcha="#structKeyExists(form,"captcha") and form.captcha#"
					delay="#form.delay#">
			
		</cfcase>
	<!--- CHANGE --->
		<cfcase value="#stText.Buttons.Change#">
			<cfif len(form._old_password) LT 6>
				<cfset error.message="#stText.Login.OldTooShort#">
			<cfelseif len(form._new_password) LT 6>
				<cfset error.message="#stText.Login.NewTooShort#">
			<cfelseif form._new_password NEQ form._new_password_re>
				<cfset error.message="#stText.Login.UnequalPasswords#">
			<cfelse>
				<cfadmin 
					action="updatePassword"
					type="#request.adminType#"
					oldPassword="#form._old_password#"
					newPassword="#form._new_password#">
				<cfset session["password"&request.adminType]=form._new_password>
			</cfif> 
		
		</cfcase>
	<!--- UPDATE Default Password --->
		<cfcase value="#stText.Buttons.Update#">
			<cfif len(form._new_password) LT 6>
				<cfset error.message="#stText.Login.NewTooShort#">
			<cfelse>
				<cfadmin 
					action="updateDefaultPassword"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					newPassword="#form._new_password#">
			</cfif>
		</cfcase>
        
        <cfcase value="#stText.Buttons.delete#">
        		
				<cfadmin 
					action="removeDefaultPassword"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#">
		</cfcase>
        
	<!--- reset individual password --->
		<cfcase value="#stText.Buttons.Reset#">
			<cfif len(form.contextPath)>
				<cfadmin 
					action="resetPassword"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					contextPath="#form.contextPath#">
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
Error Output --->
<cfset printError(error)>


<cfadmin 
        action="getLoginSettings"
        type="#request.adminType#"
		password="#session["password"&request.adminType]#"
   		returnVariable="settings">


<!--- 
settings --->
<cfif request.adminType EQ "server">
<cfoutput>
<table class="tbl" width="740">
<tr>
	<td colspan="3"><h2>#stText.Login.settings#</h2></td>
</tr>
</cfoutput>

<cfoutput><cfform action="#request.self#?action=#url.action#" method="post">
<tr>
	<th scope="row">#stText.Login.useCaptcha#</th>
	<td>
		
		<cfinput type="checkbox" name="captcha" checked="#settings.captcha#" value="true"><br /><div class="comment">#stText.Login.useCaptchaDesc#</div>
	</td>
</tr>
<tr>
	<th scope="row">#stText.Login.delay#</th>
	<td>
		<select name="delay"><cfset hasDelay=false>
        	<cfloop list="0,1,5,10,30,60" index="i"><option <cfif settings.delay EQ i><cfset hasDelay=true>selected="selected"</cfif>>#i#</option></cfloop>
            <cfif not hasDelay><option selected="selected">#settings.delay#</option></cfif>
        </select> #stText.Login.seconds#<br /><div class="comment">#stText.Login.delayDesc#</div>
	</td>
</tr>
<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.save#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfform></cfoutput>
</table>
<br><br>
</cfif>
<!--- 
change password --->
<cfoutput>
  
<table class="tbl" width="740">
<tr>
	<td colspan="3"><h2>#stText.Login.ChangePassword#</h2>#stText.Login.ChangePasswordDescription#</td>
</tr>
</cfoutput>

<cfoutput><cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
<tr>
	<th scope="row">#stText.Login.OldPassword#</th>
	<td>
		<div class="comment">#stText.Login.OldPasswordDescription#</div><br>
		<cfinput type="password" name="_old_password" value="" passthrough='autocomplete="off"'
		style="width:200px" required="yes" message="#stText.Login.OldPasswordMissing#">
	</td>
</tr>
<tr>
	<th scope="row">#stText.Login.NewPassword#</th>
	<td>
		<div class="comment">#stText.Login.NewPasswordDescription#</div><br>
		<cfinput type="password" name="_new_password" value="" passthrough='autocomplete="off"'
		style="width:200px" required="yes" message="#stText.Login.NewPasswordMissing#">
	</td>
</tr>
<tr>
	<th scope="row">#stText.Login.RetypePassword#</th>
	<td>
		<div class="comment">#stText.Login.RetypeNewPassword#</div><br>
		<cfinput type="password" name="_new_password_re" value="" passthrough='autocomplete="off"' 
		style="width:200px" required="yes" message="#stText.Login.RetypeNewPasswordMissing#">
	</td>
</tr>

<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Change#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfform></cfoutput>
</table>
<br><br>

<cfif request.adminType EQ "server">
<cftry>
	<cfset hasDefaultPW=true>
	<cfadmin 
		action="getDefaultPassword"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		returnVariable="defaultPassword">
		<cfcatch type="any">
			<cfset hasDefaultPW=false>
		</cfcatch>
</cftry>
<!--- 
Set default password --->
<cfif hasDefaultPW>
<cfoutput>
<h2>#stText.Login.DefaultPassword#</h2>
<table class="tbl" width="600">
<tr>
	<td colspan="3">#stText.Login.DefaultPasswordDescription#</td>
</tr>
</cfoutput>
<tr>
	<td colspan="3"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfoutput><cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
<tr>
	<th scope="row">#stText.Login.Password#</th>
	<td>
		<div class="comment">#stText.Login.NewPasswordDescription#</div><br>
		<cfinput type="text" name="_new_password" value="#defaultPassword#" 
		style="width:200px" required="no" message="#stText.Login.NewPasswordMissing#">
	</td>
</tr>
<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.delete#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfform></cfoutput>
</table>
<br><br>
</cfif>


<cfset hasContextes=true>
<cftry>
<cfadmin 
					action="getContextes"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					returnVariable="contextes">
	<cfcatch type="application">
		<cfset hasContextes=false>
	</cfcatch>
</cftry>					
<cfif hasContextes>
<!--- 
Reset Password --->
<cfoutput><h2>#stText.Login.resetWebPW#</h2>
<table class="tbl" width="600">
<tr>
	<td colspan="3">#stText.Login.resetWebPWDescription#</td>
</tr>
</cfoutput>
<tr>
	<td colspan="3"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfoutput><cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
<tr>
	<th scope="row">#stText.Login.web#</th>
	<td>
		<cfsilent>
		<cfset size=0>
		<cfset QueryAddColumn(contextes,"text",array())>
		<cfloop query="contextes">
				<cfif len(contextes.label)><cfset path=contextes.label&" ("&contextes.path&")"><cfelse><cfset path=contextes.path></cfif>
				<cfset contextes.text=path>
				<cfif size LT len(path)><cfset size=len(path)></cfif>
		</cfloop>
		</cfsilent>
		<select name="contextPath">
				<option value=""></option>
			<cfloop query="contextes"><option value="#contextes.path#">#contextes.text#</option></cfloop>
		</select>
	</td>
</tr>

<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Reset#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfform></cfoutput>
</table>
<br><br>
</cfif>


</cfif>