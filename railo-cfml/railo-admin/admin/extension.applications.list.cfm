
<cfset hasAccess=true>

    






<cfoutput>
<cfset existing=struct()>
<cfif extensions.recordcount>
<!--- 
Installed Applications --->
<table class="tbl" width="740">
<tr>
    <td colspan="5"><h2>#stText.ext.installed#</h2>
#stText.ext.installeddesc#</td>
</tr>

<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">

<tr>
		<td class="tblHead" nowrap><input type="text" name="filter" style="width:100%" value="#session.extFilter.filter#" /></td>
		<td class="tblHead" width="50" nowrap><input type="submit" class="submit" name="mainAction" value="filter"></td>
	</tr>
	<tr>
		<td width="380" colspan="2" align="right"></td>
	</tr>

</cfform>
</table>

<table class="tbl" width="740">
<colgroup>
    <col width="148">
    <col width="148">
    <col width="148">
    <col width="148">
    <col width="148">
</colgroup>
</tr>


<cfset row=0>
<cfset missingRows=5>

<cfloop query="extensions">
	<cfset uid=createId(extensions.provider,extensions.id)>
		<cfset existing[uid]=true>
        
        <cftry>
        	<cfset prov=getProviderData(extensions.provider)>
            <cfset provTitle=prov.info.title>
            <cfcatch>
            	<cfset provTitle="">
            </cfcatch>
        </cftry>
        
        <cfif
			doFilter(session.extFilter.filter,extensions.label,false)
			or
			doFilter(session.extFilter.filter,extensions.category,false)
			or
			doFilter(session.extFilter.filter,provTitle,false)
			>
     	
        
        <cfset row++>
			<cfset missingRows=5-(row mod 5)>
			<cfset link="#request.self#?action=#url.action#&action2=detail&uid=#uid#">
            <cfset dn=getDumpNail(extensions.image,130,50)>
			<cfset hasUpdate=updateAvailable(extensions)>
            <td height="80" class="tblContent" nowrap align="center" <cfif hasUpdate>style="border-color:red"</cfif>><cfif len(dn)><a href="#link#"><img src="#dn#" border="0"/></a><br /></cfif>
            <a href="#link#" style="text-decoration:none;"><b>#cut(extensions.label,20)#</b><br />
            #cut(extensions.category,20)#</a><cfif hasUpdate><br /><span class="CheckError">Update Available</span></cfif>
            </td>
			<cfif row mod 5 EQ 0></tr><tr></cfif>
        </cfif>
	
</cfloop>		
	
	
	<cfif missingRows LT 5><cfloop from="1" to="#missingRows#" index="i"><td height="80" class="tblHead"><cfmodule template="tp.cfm"  width="100" height="50"></td></cfloop></cfif>


</tr>
</table>
<br><br>
</cfif>


<!---
<cfsilent>
<cfset appRecCount=0>
<cfloop from="1" to="#arrayLen(apps)#" index="i">
	<cfset app=apps[i]>
    <cfloop query="app">
            <cfif StructKeyExists(existing,createId(urls[i],app.id))><cfcontinue></cfif>  
            <cfif (app.type EQ "all" or app.type EQ request.adminType or ( app.type EQ "" and "web" EQ request.adminType))>
                <cfset appRecCount++>
            </cfif>
    </cfloop>
</cfloop>
</cfsilent>--->

<cfif true>
<!--- 
Not Installed Applications --->
<table class="tbl" width="740">
<tr>
    <td colspan="2"><h2>#stText.ext.notInstalled#</h2>
#stText.ext.notInstalleddesc#</td>
</tr>

<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">

<tr>
		<td class="tblHead" nowrap><input type="text" name="filter2" style="width:100%" value="#session.extFilter.filter2#" /></td>
		<td class="tblHead" width="50" nowrap><input type="submit" class="submit" name="mainAction" value="filter"></td>
	</tr>
	<tr>
		<td width="380" colspan="2" align="right"></td>
	</tr>

</cfform>
</table>



<table class="tbl" width="740">
<colgroup>
    <col width="148">
    <col width="148">
    <col width="148">
    <col width="148">
    <col width="148">
</colgroup>
    
<tr>
<cfset row=0>
<cfset missingRows=5>
<cfif isQuery(data)><cfoutput query="data" group="uid">
	    <cfset info=data.info>
        <cfif 
			!StructKeyExists(existing,data.uid)
			and
			
			(data.type EQ "all" or data.type EQ request.adminType or ( data.type EQ "" and "web" EQ request.adminType)) 
			and
			
			(doFilter(session.extFilter.filter2,data.label,false)
			or
			doFilter(session.extFilter.filter2,data.category,false)
			or
			doFilter(session.extFilter.filter2,info.title,false))
			>
        	<cfset row++>
			<cfset missingRows=5-(row mod 5)>
			<cfset link="#request.self#?action=#url.action#&action2=detail&uid=#data.uid#">
            <cfset dn=getDumpNail(data.image,130,50)>
			<td height="80" class="tblContent" nowrap align="center"><cfif len(dn)><a href="#link#"><img src="#dn#" border="0"/></a><br /></cfif>
            <a href="#link#" style="text-decoration:none;"><b>#cut(data.label,20)#</b><br />
            #cut(data.category,20)#</a>
            </td>
			<cfif row mod 5 EQ 0></tr><tr></cfif>
        </cfif>

</cfoutput></cfif>
	<cfif missingRows LT 5><cfloop from="1" to="#missingRows#" index="i"><td height="80" class="tblHead"><cfmodule template="tp.cfm"  width="100" height="50"></td></cfloop></cfif>
</tr>
</table>































<!---

<table class="tbl" width="740">
<tr>
    <td colspan="5"><h2>#stText.ext.notInstalled#</h2>
#stText.ext.notInstalleddesc#</td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">


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

    
    

<cfoutput query="data" group="uid">
	    <cfset info=data.info>
        <cfif 
			!StructKeyExists(existing,data.uid)
			and
			
			(data.type EQ "all" or data.type EQ request.adminType or ( data.type EQ "" and "web" EQ request.adminType)) 
			and
			doFilter(session.extFilter2.name,data.label,false)
			and
			doFilter(session.extFilter2.category,data.category,false)
			and
			doFilter(session.extFilter2.provider,info.title,false)
			>
        
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
				<input type="radio" class="radio" name="row" value="#data.currentrow#">
                <input type="hidden" name="id_#data.currentrow#" value="#data.id#">
                <input type="hidden" name="uid_#data.currentrow#" value="#data.uid#">
                <input type="hidden" name="hashProvider_#data.currentrow#" value="#hash(data.provider)#">
				</td>
			</tr>
			</table>
			</td>
            <cfset dn=getDumpNail(data.image)>
			<td class="tblContent" nowrap>
            <table border="0" cellpadding="0" cellspacing="0">
            <tr>
            	<td width="80" height="40" align="center"><cfif len(dn)><img src="#dn#" border="0"/><cfelse>&nbsp;</cfif></td>
            	<td >&nbsp;&nbsp;&nbsp;</td>
            	<td><a href="#request.self#?action=#url.action#&action2=detail&hashProvider=#hash(data.provider)#&id=#data.id#&uid=#data.uid#">#data.label#</a></td>
            </tr>
            </table>
            
            
            
            </td>
			<td class="tblContent" nowrap>#data.category#</td>
			<td class="tblContent" colspan="2" nowrap><a href="#request.self#?action=#url.action#&action2=provider&hashProvider=#hash(data.provider)#">#data.info[data.currentrow].title#</a></td>
		</tr>
        </cfif>
</cfoutput>	


<tr>
    <td colspan="4">
     <table border="0" cellpadding="0" cellspacing="0">
     <tr>
        <td><cfmodule template="tp.cfm"  width="7" height="1"></td>		
        <td><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="10"/></td>
        <td></td>
     </tr>
     <tr>
        <td></td>
        <td valign="top"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="36" height="1" /></td>
        <td>&nbsp;
        <input type="submit" class="submit" name="mainAction" value="#stText.Buttons.install#">
        </td>	
    </tr>
     </table>
     </td>
</tr></cfform>
</table>
--->




</cfif>
</cfoutput>
