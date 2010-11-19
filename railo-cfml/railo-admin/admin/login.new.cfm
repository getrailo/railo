<cfparam name="cookie.railo_admin_lang" default="en">
<cfset session.railo_admin_lang = cookie.railo_admin_lang>
<cfparam name="languages" default="#{en:'English',de:'Deutsch'}#">
<table class="tbl">
<cfform action="#request.self#" method="post">
<cfoutput>
<tr>
	<td class="tblHead" width="200" align="right">#stText.Login.Password#</td>
	<td class="tblContent" width="200"><cfinput type="password" name="new_password" value="" passthrough='autocomplete="off"'
		style="width:200px" required="yes" message="#stText.Login.PasswordMissing#"></td>
</tr>
<tr>
	<td class="tblHead" width="200" align="right">#stText.Login.RetypePassword#</td>
	<td class="tblContent" width="200"><cfinput type="password" name="new_password_re" value="" passthrough='autocomplete="off"'
		style="width:200px" required="yes" message="#stText.Login.RetypePasswordMissing#"></td>
</tr>
<cfset f="">
<cfloop collection="#languages#" item="key"><cfif f EQ "" or key EQ session.railo_admin_lang><cfset f=key></cfif></cfloop>
<tr>
	<td class="tblHead" width="100" align="right">#stText.Login.language#</td>
	<td class="tblContent" width="200"><select name="lang" onchange="changePic(this.options[this.selectedIndex].value)">
	<cfloop collection="#languages#" item="key"><option value="#key#" <cfif key EQ session.railo_admin_lang>selected</cfif>>#languages[key]#</option></cfloop>
	</select></td>
</tr>




<tr>
	<td colspan="2" width="100"><input type="submit" class="submit" name="submit" value="#stText.Buttons.Submit#"></td>
</tr>
</cfoutput>
</cfform>
</table>
