<cfoutput>
<!---<script language="JavaScript" type="text/javascript">
function changePic(lang)	{
	flag.change(lang);
}
function FlagNode(lang)	{
	this.lang=lang;
	this.img=new Image();
	this.img.src='resources/img/'+this.lang+'.gif.cfm';
}
function Flag(arr)	{
	this.flagNode={};
	for(var i=0;i< arr.length;i++)	{
		this.flagNode[arr[i].toLowerCase()]=new FlagNode(arr[i]);
		
	}
	this.change=function (lang)	{
		lang=lang.toLowerCase();
		if(document.images)	{
			if(document.images['flag'])	{
				document.images['flag'].src=this.flagNode[lang].img.src;
			}
		}
	};var flag=new Flag(new Array('de','en'));
}--->
<script>
function doFocus() {<cfoutput>
	document.forms.login.login_password#request.adminType#.focus();
	document.forms.login.login_password#request.adminType#.select(); </cfoutput>
}

</script>


<cfparam name="cookie.railo_admin_lang" default="en">
<cfset session.railo_admin_lang = cookie.railo_admin_lang>
<cfif isDefined('url.action')><cfset self=request.self&"?action="&url.action><cfelse><cfset self=request.self></cfif>
<cfset languages=struct(en:'English',de:'Deutsch')>
<table class="tbl">
<cfform name="login" action="#self#" method="post">

<tr>
	<td class="tblHead" width="120" align="right">#stText.Login.Password#</td>
	<td class="tblContent" width="200"><cfinput type="password" name="login_password#request.adminType#" value=""   passthrough='autocomplete="off"'
		style="width:200px" required="yes" message="#stText.Login.PasswordMissing#"></td>
</tr>
<cfoutput>
<cfset f="">
<cfloop collection="#languages#" item="key"><cfif f EQ "" or key EQ session.railo_admin_lang><cfset f=key></cfif></cfloop>
<tr>
	<td class="tblHead" width="100" align="right">#stText.Login.language#<!---<img name="flag" src="resources/img/#f#.gif.cfm" width="23" height="14">---></td>
	<td class="tblContent" width="200"><select name="lang" <!--- onchange="changePic(this.options[this.selectedIndex].value)"--->>
	<cfloop collection="#languages#" item="key"><option value="#key#" <cfif key EQ session.railo_admin_lang>selected</cfif>>#languages[key]#</option></cfloop>
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