<cfoutput>

<script>
function doFocus() {<cfoutput>
	document.forms.login.login_password#request.adminType#.focus();
	document.forms.login.login_password#request.adminType#.select(); </cfoutput>
}

</script>


<cfparam name="cookie.railo_admin_lang" default="en">
<cfset session.railo_admin_lang = cookie.railo_admin_lang>
<cfif isDefined('url.action')><cfset self=request.self&"?action="&url.action><cfelse><cfset self=request.self></cfif>
<cfparam name="languages" default="#{en:'English',de:'Deutsch'}#">
<table class="tbl">
<cfform onerror="customError" name="login" action="#self#" method="post">

<tr>
	<td class="tblHead" width="120" align="right"><label for="login_password#request.adminType#">#stText.Login.Password#</label></td>
	<td class="tblContent" width="200"><cfinput type="password" name="login_password#request.adminType#" id="login_password#request.adminType#" value=""   passthrough='autocomplete="off"'
		style="width:200px" required="yes" message="#stText.Login.PasswordMissing#"></td>
</tr>
<cfoutput>
	<cfset f="">
	<cfloop collection="#languages#" item="key"><cfif f EQ "" or key EQ session.railo_admin_lang><cfset f=key></cfif></cfloop>
	<tr>
		<td class="tblHead" width="100" align="right">#stText.Login.language#</td>
		<cfset aLangKeys = structKeyArray(languages)>
		<cfset arraySort(aLangKeys, "text")>
		<td class="tblContent" width="200"><select name="lang">
		<cfloop from="1" to="#arrayLen(aLangKeys)#" index="iKey">
			<cfset key = aLangKeys[iKey]>
			<option value="#key#" <cfif key EQ session.railo_admin_lang>selected</cfif>>#languages[key]#</option>
		</cfloop>
		</select></td>
	</tr>
	
	<tr>
		<td class="tblHead" width="100" align="right">#stText.Login.rememberMe#</td>
		<td class="tblContent" width="200"><select name="rememberMe">
		<option value="s">#stText.Login.s#</option>
		<option value="d">#stText.Login.d#</option>
		<option value="ww">#stText.Login.ww#</option>
		<option value="m">#stText.Login.m#</option>
		<option value="yyyy">#stText.Login.yyyy#</option>
		</select></td>
	    
	</tr>

</cfoutput>
<tr>
	<td colspan="2" align="right"><input class="submit" type="submit" name="submit" value="#stText.Buttons.Submit#"></td>
</tr>
</cfform>
</table>
</cfoutput>