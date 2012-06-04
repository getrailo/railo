<cfsilent>
<cfparam name="url.id" default="">
<cfparam name="url.action2" default="">
<cfset index=1>
<cfset context="">
<cfset row="">


<cfadmin 
	action="getContexts"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="contextes">
	


	<cfset size=0>
	<cfset QueryAddColumn(contextes,"text",array())>
	<cfloop query="contextes">
			<cfif len(contextes.label)>
				<cfset _path=contextes.label&" ("&contextes.path&")">
			<cfelse>
				<cfset _path=contextes.path>
			</cfif>
			<cfset contextes.text=_path>
			<cfif size LT len(_path)>
				<cfset size=len(_path)>
			</cfif>
			<cfif url.id EQ contextes.id>
				<cfset row=contextes.currentrow>
			</cfif>
	</cfloop>
</cfsilent>

<!--- 
Detail
 --->
<cfif url.action2 EQ "edit">
	
	<cfadmin 
		action="getDefaultSecurityManager"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		returnVariable="daccess">
	<cfadmin 
		action="getSecurityManager"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		
		id="#url.id#"
		returnVariable="access">
	<cfinclude template="security.access.form.cfm">

<!--- 
Overview
 --->
<cfelse>
<cfset count=0>
<cfloop query="contextes"><cfif contextes.hasOwnSecContext><cfset count++></cfif></cfloop>
<cfoutput>
	
	<table class="tbl" width="650">
	<tr>
		<td colspan="3"><cfmodule template="tp.cfm"  width="1" height="1"></td>
	</tr>
	<tr>
		<td colspan="3"><h2>#stText.Security.specListTitle#</h2>
		#stText.Security.specListText#</td> 
	</tr>
	<tr>
		<td colspan="3"><cfmodule template="tp.cfm"  width="1" height="1"></td>
	</tr>
	<cfform onerror="customError" action="#go(url.action,"removeSecurityManager")#" method="post">
		<tr>
			<td width="20"></td>
			<td width="205" class="tblHead" nowrap>#stText.Security.specListHost#</td>
			<td width="365" class="tblHead" nowrap>#stText.Security.specListPath#</td>
		</tr>
		<cfset hasNoneIndividual=false>
		<cfloop query="contextes"><cfif contextes.hasOwnSecContext >
			<!--- and now display --->
		<tr>
			<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><input type="checkbox" class="checkbox" name="ids_#contextes.currentrow#" value="#contextes.id#">
				</td>
				<td><a href="#go(url.action,"edit",struct(id:contextes.id))#">
			<img src="resources/img/edit.png.cfm" hspace="2" border="0"></a></td>
			</tr>
			</table>
			</td>
			<td nowrap>#contextes.label#&nbsp;</td>
			<td nowrap>#contextes.path#</td>
		</tr>
		<cfelse>
		<cfset hasNoneIndividual=true>
		</cfif></cfloop>
		<tr>
			<td colspan="4">
			 <table border="0" cellpadding="0" cellspacing="0">
			 <tr>
				<td><cfmodule template="tp.cfm"  width="10" height="1"></td>		
				<td><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="20"></td>
				<td></td>
			 </tr>
			 <tr>
				<td></td>
				<td valign="top"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="14"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="36" height="1"></td>
				<td>&nbsp;
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
				<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Delete#">
				</td>	
			</tr>
			 </table>
			 </td>
		</tr>
	</cfform>
	</cfoutput>
	</table>
    
	<br><br>


	<cfif hasNoneIndividual>
		<cfoutput>
		<!--- 
		Create new Indicvidual sec --->
		<h2>#stText.Security.specListNewTitle#</h2>
		<table class="tbl" width="350">
		<tr>
			<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
		</tr>
		<cfform onerror="customError" action="#go(url.action,'createSecurityManager')#" method="post">
			<td class="tblHead"  nowrap>#stText.Security.specListWebContext#</td>
			<td><select name="id">
						<cfoutput><cfloop query="contextes"><cfif not contextes.hasOwnSecContext>
							<option value="#contextes.id#">#contextes.text#</option>
						</cfif></cfloop></cfoutput>
					</select></td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" class="submit" name="run" value="#stText.Buttons.Create#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			</td>
		</tr>
		</cfform>
		</table>   
		<br><br>
		</cfoutput>
	</cfif>
	
	
</cfif>











