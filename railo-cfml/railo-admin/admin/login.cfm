<cfoutput>
<cfscript>
letters='0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';

function createRandomText(string length){
	var str='';
	for(var i=0;i<length;i++){
		str&=mid(letters,RandRange(1,len(letters)),1);
	}
	return str;
	
}
</cfscript>
<script>
function doFocus() {<cfoutput>
	document.forms.login.login_password#request.adminType#.focus();
	document.forms.login.login_password#request.adminType#.select(); </cfoutput>
}

</script>
<cfadmin 
        action="getLoginSettings"
        type="#request.adminType#"
   		returnVariable="loginSettings">
<cfparam name="cookie.railo_admin_lang" default="en">
<cfset session.railo_admin_lang = cookie.railo_admin_lang>
<cfif isDefined('url.action')><cfset self=request.self&"?action="&url.action><cfelse><cfset self=request.self></cfif>
<cfparam name="languages" default="#{en:'English',de:'Deutsch'}#">
<table class="tbl">
<cfform onerror="customError" name="login" action="#self#" method="post">

<tr>
	<td class="tblHead" width="120" align="right">#stText.Login.Password#</td>
	<td class="tblContent" width="200"><cfinput type="password" name="login_password#request.adminType#" value=""   passthrough='autocomplete="off"'
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
    <cfif loginSettings.captcha>
	<cfset cap=createRandomText(6)>
    <cfset session.cap=cap>
    <tr>
    	<td class="tblHead" width="100" align="right">#stText.login.captchaHelp#</td>
        
		<td class="tblContent" width="200">
        <cfimage action="captcha" width="160" height="28" text="#cap#" difficulty="medium">
        <a style="font-size : 10px" href="#request.self#<cfif structKeyExists(url,"action")>?action=#url.action#</cfif>">Reload</a><br />
        
        
        <cfinput type="text" name="captcha" value="" passthrough='autocomplete="off"'
		style="width:200px" required="yes" message="#stText.login.captchaHelpMiss#">
        <br /><span class="comment">#stText.login.captchaHelpDesc#</span>
        </td>
	</tr>
    <cfelse>
    	<cfset StructDelete(session,"cap",false)>
    </cfif>
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