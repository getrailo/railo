
<cfset hasAccess=true>

    






<cfoutput>
<cfset existing=struct()>
<cfif extensions.recordcount>
<!--- 
Installed Applications --->
<h2>#stText.ext.installed#</h2>
#stText.ext.installeddesc#

<table class="tbl" width="650">
<tr>
    <td colspan="5"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">


<tr>
		<td width="20"></td>
		<td width="225" class="tblHead" nowrap><input type="text" name="nameFilter" style="width:225px" value="#session.extFilter.name#" /></td>
		<td width="130" class="tblHead" nowrap><input type="text" name="categoryFilter" style="width:130px" value="#session.extFilter.category#" /></td>
		<td width="225" class="tblHead" nowrap><input type="text" name="providerFilter" style="width:225px" value="#session.extFilter.provider#" /></td>
		<td class="tblHead" nowrap><input type="submit" class="submit" name="mainAction" value="filter"></td>
	</tr>
	<tr>
		<td width="380" colspan="7" align="right"></td>
	</tr>

<tr>
    <td width="20"></td>
    <td width="225" class="tblHead" nowrap>#stText.ext.application#</td>
    <td width="130" class="tblHead" nowrap>#stText.ext.category#</td>
    <td width="225" class="tblHead" nowrap>#stText.ext.provider#</td>
    <td width="50" class="tblHead" nowrap>#stText.ext.updateAvailable#</td>
</tr>
<cfloop query="extensions">
		<cfset existing[hash(extensions.provider&extensions.id)]=true>
        
        <cftry>
        	<cfset prov=getProviderData(extensions.provider)>
            <cfset provTitle=prov.info.title>
            <cfcatch>
            	<cfset provTitle="">
            </cfcatch>
        </cftry>
        
        <cfif
			doFilter(session.extFilter.name,extensions.label,false)
			and
			doFilter(session.extFilter.category,extensions.category,false)
			and
			doFilter(session.extFilter.provider,provTitle,false)
			>
     	
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
				<input type="radio" class="radio" name="row" value="#extensions.currentrow#">
                <input type="hidden" name="id_#extensions.currentrow#" value="#extensions.id#">
                <input type="hidden" name="hashProvider_#extensions.currentrow#" value="#hash(extensions.provider)#">
				</td>
			</tr>
			</table>
			</td>
			<td class="tblContent" nowrap><a href="#request.self#?action=#url.action#&action2=detail&hashProvider=#hash(extensions.provider)#&id=#extensions.id#">#extensions.label#</a></td>
			<td class="tblContent" nowrap>#extensions.category#</td>
			<td class="tblContent" nowrap><a href="#request.self#?action=#url.action#&action2=provider&hashProvider=#hash(extensions.provider)#">#provTitle#</a></td>
			<td class="tblContent" nowrap valign="middle" align="center">#yesNoFormat(updateAvailable(extensions))#</td>
		</tr>
        </cfif>
	
</cfloop>		
	
	



<tr>
    <td colspan="4">
     <table border="0" cellpadding="0" cellspacing="0">
     <tr>
        <td><cfmodule template="tp.cfm"  width="7" height="1"></td>		
        <td><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="10"></td>
        <td></td>
     </tr>
     <tr>
        <td></td>
        <td valign="top"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="14"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="36" height="1"></td>
        <td>&nbsp;
        <input type="submit" class="submit" name="mainAction" value="#stText.Buttons.update#">
        <input type="submit" class="submit" name="mainAction" value="#stText.Buttons.uninstall#">
        </td>	
    </tr>
     </table>
     </td>
</tr></cfform>
</table>
<br><br>
</cfif>










<cfsilent>
<cfset appRecCount=0>
<cfloop from="1" to="#arrayLen(apps)#" index="i">
	<cfset app=apps[i]>
    <cfloop query="app">
            <cfif StructKeyExists(existing,hash(urls[i]&app.id))><cfcontinue></cfif>  
            <cfif (app.type EQ "all" or app.type EQ request.adminType or ( app.type EQ "" and "web" EQ request.adminType))>
                <cfset appRecCount++>
            </cfif>
    </cfloop>
</cfloop>
</cfsilent>

<cfif appRecCount>
<!--- 
Not Installed Applications --->

<h2>#stText.ext.notInstalled#</h2>
#stText.ext.notInstalleddesc#

<table class="tbl" width="650">
<tr>
    <td colspan="5"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">


<tr>
		<td width="20"></td>
		<td width="225" class="tblHead" nowrap><input type="text" name="nameFilter2" style="width:225px" value="#session.extFilter2.name#" /></td>
		<td width="130" class="tblHead" nowrap><input type="text" name="categoryFilter2" style="width:130px" value="#session.extFilter2.category#" /></td>
		<td width="225" class="tblHead" nowrap><input type="text" name="providerFilter2" style="width:225px" value="#session.extFilter2.provider#" /></td>
		<td class="tblHead" nowrap><input type="submit" class="submit" name="mainAction" value="filter"></td>
	</tr>
	<tr>
		<td width="380" colspan="7" align="right"></td>
	</tr>

<tr>
    <td width="20">&nbsp;<br />&nbsp;</td>
    <td width="225" class="tblHead" nowrap>#stText.ext.application#</td>
    <td width="130" class="tblHead" nowrap>#stText.ext.category#</td>
    <td width="225" class="tblHead" colspan="2" nowrap>#stText.ext.provider#</td>
</tr>

<cfset row=0>
<cfloop from="1" to="#arrayLen(apps)#" index="i">
	<cfset app=apps[i]>
	<cfset info=infos[i]>
	<cfloop query="app">
        <cfif StructKeyExists(existing,hash(urls[i]&app.id))><cfcontinue></cfif>
        
        <cfif (app.type EQ "all" or app.type EQ request.adminType or ( app.type EQ "" and "web" EQ request.adminType))  and
			doFilter(session.extFilter2.name,app.label,false)
			and
			doFilter(session.extFilter2.category,app.category,false)
			and
			doFilter(session.extFilter2.provider,info.title,false)
			>
        <cfset row++>
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
				<input type="radio" class="radio" name="row" value="#row#">
                <input type="hidden" name="id_#row#" value="#app.id#">
                <input type="hidden" name="hashProvider_#row#" value="#hash(urls[i])#">
				</td>
				
			</tr>
			</table>
			</td>
			<td class="tblContent" nowrap><a href="#request.self#?action=#url.action#&action2=detail&hashProvider=#hash(urls[i])#&id=#app.id#">#app.label#</a></td>
			<td class="tblContent" nowrap>#app.category#</td>
			<td class="tblContent" colspan="2" nowrap><a href="#request.self#?action=#url.action#&action2=provider&hashProvider=#hash(urls[i])#">#info.title#</a></td>
		</tr>
        </cfif>
	</cfloop>
</cfloop>		
	
	



<tr>
    <td colspan="4">
     <table border="0" cellpadding="0" cellspacing="0">
     <tr>
        <td><cfmodule template="tp.cfm"  width="7" height="1"></td>		
        <td><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="10"></td>
        <td></td>
     </tr>
     <tr>
        <td></td>
        <td valign="top"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="14"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="36" height="1"></td>
        <td>&nbsp;
        <input type="submit" class="submit" name="mainAction" value="#stText.Buttons.install#">
        </td>	
    </tr>
     </table>
     </td>
</tr></cfform>
</table>
</cfif>
</cfoutput>
