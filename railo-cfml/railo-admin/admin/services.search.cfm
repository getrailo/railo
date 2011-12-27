<cfif request.admintype EQ "server"><cflocation url="#request.self#" addtoken="no"></cfif>

<cfparam name="form.run" default="none">
<cfparam name="error" default="#struct(message:"",detail:"")#">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="search"
	secValue="yes">

<cfif request.adminType EQ "web">
	<cftry>
	<cfswitch expression="#form.run#">
	<!--- Index --->
		<cfcase value="index">
			<cfsetting requesttimeout="300">
			<cfadmin 
				action="index"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
								
				indexAction="update" 
				indexType="path" 
				collection="#url.collection#" 
				key="#form.path#" 
				urlpath="#form.url#" 
				extensions="#form.extensions#"
				recurse="#structKeyExists(form,"recurse") and form.recurse#"
				language="#form.language#"
				remoteClients="#request.getRemoteClients()#">
		</cfcase>
	
	<!--- Create --->
		<cfcase value="#stText.Buttons.Create#">
			<cfadmin 
				action="collection" 
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				collectionAction="create" 
				collection="#form.collName#" 
				path="#form.collPath#" 
				language="#form.collLanguage#"
				remoteClients="#request.getRemoteClients()#">
		</cfcase>
	
	<!--- Action --->
		<cfcase value="action">
			<cfif StructKeyExists(form,"name")>
				<cfloop collection="#form.name#" item="key">
					<cfswitch expression="#form.action#">
						<cfcase value="#stText.Buttons.Purge#">
							<cfadmin 
								action="index" 
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								
								indexAction="purge" 
								collection="#form.name[key]#"
								remoteClients="#request.getRemoteClients()#">
						</cfcase>
						<cfcase value="#stText.Buttons.Repair#">
							<cfadmin 
								action="collection" 
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								
								collectionAction="repair" 
								collection="#form.name[key]#"
								remoteClients="#request.getRemoteClients()#">
						</cfcase>
						<cfcase value="#stText.Buttons.Optimize#">
							<cfadmin 
								action="collection" 
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								
								collectionAction="optimize" 
								collection="#form.name[key]#"
								remoteClients="#request.getRemoteClients()#">
						</cfcase>	
						<cfcase value="#stText.Buttons.Delete#">
							<cfadmin 
								action="collection" 
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								
								collectionAction="delete" 
								collection="#form.name[key]#"
								remoteClients="#request.getRemoteClients()#">
							
						</cfcase>
					</cfswitch>
				</cfloop>
			</cfif>
		</cfcase>
	</cfswitch>
		<cfcatch>
			<cfset error.message=cfcatch.message>
			<cfset error.detail=cfcatch.Detail>
		</cfcatch>
	</cftry>
	<cfif not isDefined("url.search")>
		<!--- 
		Redirect to entry --->
		<cfif cgi.request_method EQ "POST" and error.message EQ "">
			<cflocation url="#request.self#?action=#url.action#" addtoken="no">
		</cfif>
	</cfif>	
	
	<!--- 
	Error Output--->
	<cfif error.message NEQ "">
		<cfoutput><span class="CheckError">
		#error.message#<br>
		#error.detail#
		</span><br><br></cfoutput>
	</cfif>
	<script language="javascript">
	
function selectAll(field) {
	var form=field.form;
	for(var key in form.elements){
		if((form.elements[key] && ""+form.elements[key].name).indexOf("name[]")==0){
			form.elements[key].checked=field.checked;
		}
	}
	
}
	</script>
	<cfoutput><div style="width:740px">#stText.Search.Description#</div><br><br></cfoutput>
	
	<cfcollection action="list" name="collections">
	<cfif not StructKeyExists(url,"collection")>
		<!--- 
		@to setting for SearchEngine Class
		 --->
<cfif collections.recordcount>
		<!--- 
		Existing Collection --->
		
		<table class="tbl" width="740">
		<tr>
			<td colspan="8"><h2><cfoutput>#stText.Search.Collections#</cfoutput></h2></td>
		</tr>
		<tr>
			<td colspan="8"><cfmodule template="tp.cfm"  width="1" height="1"></td>
		</tr>
		<cfoutput><form action="#request.self#?action=#url.action#" method="post" enctype="multipart/form-data"></cfoutput>
		<tr>
			<td><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></td>
			<td class="tblHead" nowrap><cfoutput>#stText.Search.Name#</cfoutput></td>
			<td class="tblHead" nowrap><cfoutput>#stText.Search.Mapped#</cfoutput></td>
			<td class="tblHead" nowrap><cfoutput>#stText.Search.Online#</cfoutput></td>
			<td class="tblHead" nowrap><cfoutput>#stText.Search.External#</cfoutput></td>
			<td class="tblHead" nowrap><cfoutput>#stText.Search.Language#</cfoutput></td>
			<td class="tblHead" nowrap><cfoutput>#stText.Search.Last_Update#</cfoutput></td>
			<td class="tblHead" nowrap><cfoutput>#stText.Search.Path#</cfoutput></td>
		</tr>
		<cfoutput query="collections">
			<tr>
				<td>
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td><input type="checkbox" class="checkbox" name="name[]" value="#collections.name#"></td>
					<td><a href="#request.self#?action=#url.action#&collection=#collections.name#"><cfmodule template="img.cfm" src="edit.png" hspace="2" border="0"></a></td>
				</tr>
				</table>
				
				</td>
				<td class="tblContent" title="#collections.name#" nowrap>#cut(collections.name,16)#</td>
				<td class="tblContent" nowrap>#collections.mapped#</td>
				<td class="tblContent" nowrap>#collections.online#</td>
				<td class="tblContent" nowrap>#collections.external#</td>
				<td class="tblContent" nowrap>#collections.language#</td>
				<td class="tblContent" nowrap>#DateFormat(collections.LastModified,"yyyy-mm-dd")# #TimeFormat(collections.LastModified,"HH:mm")#</td>
				<td class="tblContent" nowrap<cfif len(collections.path) GT 40> title="#collections.path#"</cfif>>#cut(collections.path,40)#</td>
			</tr>
		</cfoutput>
<cfmodule template="remoteclients.cfm" colspan="8" line=true>
			<tr>
				<td colspan="8">
				<cfoutput> <table border="0" cellpadding="0" cellspacing="0">
				 <tr>
					<td><cfmodule template="tp.cfm"  width="10" height="1"></td>		
					<td><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="20"></td>
					<td></td>
				 </tr>
				 
				 <tr>
				 
					<td></td>
					<td valign="top"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="36" height="1"></td>
					<td>&nbsp;
					
					<input type="hidden" name="run" value="action">
					<input type="submit" class="submit" name="action" value="#stText.Buttons.Repair#">
					<input type="submit" class="submit" name="action" value="#stText.Buttons.Optimize#">
					<input type="submit" class="submit" name="action" value="#stText.Buttons.Purge#">
					<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
					<input type="submit" class="submit" name="action" value="#stText.Buttons.Delete#">
					</cfoutput>
					</td>	
				</tr>
				 </table>
				 </td>
			</tr>
		  </form>
		</table><br><br>
 </cfif>
        
        
        
		<!--- 
		Create Collection --->
		
		<table class="tbl" width="350">
		<tr>
			<td colspan="2"><h2><cfoutput>#stText.Search.CreateCol#</cfoutput></h2></td>
		</tr>
		<cfform action="#request.self#?action=#url.action#" method="post">
		<cfoutput>
		<tr>
			<td class="tblHead" width="50">#stText.Search.Name#</td>
			<td class="tblContent" width="300"><cfinput type="text" name="collName" value="" style="width:300px" required="yes" message="#stText.Search.Missing_Name#"></td>
		</tr>
		<tr>
			<td class="tblHead" width="50">#stText.Search.Path#</td>
			<td class="tblContent" width="300"><cfinput type="text" name="collPath" value="" style="width:300px" required="yes" message="#stText.Search.Missing_Path#"></td>
		</tr>
		</cfoutput>
		<tr>
			<td class="tblHead" width="50"><cfoutput>#stText.Search.Language#</cfoutput></td>
			<td class="tblContent" width="300"><select name="collLanguage">
				<cfset aLangs = StructKeyArray(stText.SearchLng)>
				<cfset ArraySort(aLangs, "text")>
				<cfoutput>
					<cfloop from="1" to="25" index="iLng">
						<option value="#aLangs[iLng]#" <cfif aLangs[iLng] eq "english">selected</cfif>>#stText.SearchLng[aLangs[iLng]]#</option>
					</cfloop>
				</cfoutput>
			</select></td>
		</tr>
		<cfmodule template="remoteclients.cfm" colspan="2">
		<tr>
			<cfoutput><td colspan="2">
				<input type="submit" class="submit" name="run" value="#stText.Buttons.Create#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			</td></cfoutput>
		</tr>
		</cfform>
		</table>
		<br>
	<cfelse>
		<cfset collection=struct()>
		<cfoutput query="collections">
			<cfif collections.name EQ url.collection>
				<cfloop index="item" list="#collections.columnlist#">
					<cfset collection[item]=collections[item]>
				</cfloop>
			</cfif>
		</cfoutput>
		
	 	<cfif not StructIsEmpty(collection)>
			<cfoutput><h2>#stText.Search.Collection# #url.collection#</h2>
			<table class="tbl">
			<tr>
				<td class="tblHead"><cfoutput>#stText.Search.Name#</cfoutput></td>
				<td class="tblContent" nowrap>#collection.name#</td>
			</tr>
			<tr>
				<td class="tblHead"><cfoutput>#stText.Search.Mapped#</cfoutput></td>
				<td class="tblContent" nowrap>#collection.mapped#</td>
			</tr>
			<tr>
				<td class="tblHead"><cfoutput>#stText.Search.Online#</cfoutput></td>
				<td class="tblContent" nowrap>#collection.online#</td>
			</tr>
			<tr>
				<td class="tblHead"><cfoutput>#stText.Search.External#</cfoutput></td>
				<td class="tblContent" nowrap>#collection.external#</td>			
			</tr>
			<tr>
				<td class="tblHead"><cfoutput>#stText.Search.Language#</cfoutput></td>
				<td class="tblContent" nowrap>#collection.language#</td>			
			</tr>
			<tr>
				<td class="tblHead"><cfoutput>#stText.Search.Last_Update#</cfoutput></td>
				<td class="tblContent" nowrap>#DateFormat(collection.LastModified,"yyyy-mm-dd")# #TimeFormat(collection.LastModified,"HH:mm")#</td>
			</tr>
			<tr>
				<td class="tblHead"><cfoutput>#stText.Search.Path#</cfoutput></td>
				<td class="tblContent" nowrap>#collection.path#</td>
			</tr>
			</table><br><br>
			</cfoutput>
			<!--- 
				@todo list index and allow delete
				@todo add/update file index
				@todo add/update url index
			---><!--- 
			Create Index --->
			<h2><cfoutput>#stText.Search.PathAction#</cfoutput></h2>
			<table class="tbl" width="450">
			<cfform action="#request.self#?action=#url.action#&collection=#collection.name#" method="post">
			<tr>
				<td class="tblHead" width="150" nowrap><cfoutput>#stText.Search.FileExtensions#</cfoutput></td>
				<td class="tblContent" width="300"><cfinput type="text" name="extensions" value=".html, .htm, .cfm, .cfml" style="width:300px" required="yes" message="#stText.Search.FileExtensionsMissing#"></td>
			</tr>
			<tr>
				<td class="tblHead" width="150" nowrap><cfoutput>#stText.Search.DirectoryPath#</cfoutput></td>
				<td class="tblContent" width="300"><cfinput type="text" name="path" value="" style="width:300px" required="yes" message="#stText.Search.DirectoryPathMissing#"></td>
			</tr>
			<tr>
				<td class="tblHead" width="150" nowrap><cfoutput>#stText.Search.IndexSubdirs#</cfoutput></td>
				<td class="tblContent" width="300"><input type="checkbox" class="checkbox" name="recurse" value="yes"></td>
			</tr>
			<tr>
				<td class="tblHead" width="150" nowrap><cfoutput>#stText.Search.URL#</cfoutput></td>
				<td class="tblContent" width="300"><cfinput type="text" name="url" value="" style="width:300px" required="no"></td>
			</tr>
			<tr>
				<td class="tblHead" width="50"><cfoutput>#stText.Search.Language#</cfoutput></td>
				<td class="tblContent" width="300"><select name="language">
						<cfoutput>
							<cfloop collection="#stText.SearchLng#" item="key">
								<option value="#key#" <cfif key eq "english">selected</cfif>>#stText.SearchLng[key]#</option>
							</cfloop>
						</cfoutput>
					</select></td>
			</tr>
<cfmodule template="remoteclients.cfm" colspan="2">
			<tr>
				<td colspan="2"><cfoutput>
					<!--- 
					@todo kein funktioneller javascript
					 --->
					<input onClick="window.location='#request.self#?action=#url.action#';" 
						type="button" class="button" name="canel" value="#stText.Buttons.Cancel#">
					<input type="hidden" name="run" value="index">
					<input type="submit" class="submit" name="_run" value="#stText.Buttons.Update#">
				</cfoutput></td>
			</tr>
			</cfform>
			</table>
			<br><br>
			<table class="tbl" width="450">
				<cfform action="#request.self#?action=#url.action#&collection=#collection.name#&search=1" method="post">
				<tr>
					<td colspan="2"><cfoutput><h2>#stText.Search.SearchTheCollection#</h2></cfoutput></td>
				</tr>
				<tr>
					<td class="tblHead" width="150" nowrap><cfoutput>#stText.Search.SearchTerm#</cfoutput></td>
					<td class="tblContent" width="300">
					<cfif StructKeyExists(form,"searchterm")><cfset session.searchterm=form.searchterm></cfif>
					<cfparam name="session.searchterm" default="">
					<cfinput type="text" 
					name="searchterm" value="#session.searchterm#" style="width:300px" required="yes" 
					message="#stText.Search.SearchTermMissing#"></td>
				</tr>
				<!--- <tr>
					<td class="tblHead" width="50"><cfoutput>#stText.Search.Language#</cfoutput></td>
					<td class="tblContent" width="300"><select name="language">
							<cfoutput>
								<cfloop from="1" to="25" index="iLng">
									<option value="#stText.SearchLng[iLng][1]#" <cfif stText.SearchLng[iLng][1] eq "english">selected</cfif>>#stText.SearchLng[iLng][2]#</option>
								</cfloop>
							</cfoutput>
						</select></td>
				</tr> --->
				<tr><td colspan="2">
					<cfoutput>
					<input type="submit" class="submit" name="search" value="#stText.Buttons.Search#">
					</cfoutput>
				</td></tr>
				</cfform>
			</table>
			<cfif StructKeyExists(form,'searchterm')>
				<cfsearch 
					collection="#url.collection#" 
					name="result" type="SIMPLE" 
					criteria="#form.searchterm#">
				<cfset session.result=variables.result>
			<cfelseif StructKeyExists(session,'result')>
				<cfset result=session.result>
			</cfif>
			
			<cfif StructKeyExists(url,'search') and StructKeyExists(variables,'result')>
				<cfparam name="url.startrow" default="1">
				<br><br><h2><cfoutput>#stText.Search.ResultOfTheSearch#</cfoutput></h2>
				<cfif result.recordCount EQ 0>
				<cfoutput><br><br><span class="CheckError">#stText.Search.noresult#</span></cfoutput>
				<cfelse>
					<cfset endrow=iif(result.recordCount GT url.startrow+9,de(url.startrow+9),de(result.recordCount))>
					
					<table class="tbl" width="650" border="0">
					<cfoutput>
					<cfsavecontent variable="header">
					<tr>
						<td class="tblHead" width="30" align="center"><cfif url.startrow GT 10><a class="tblHead" style="text-decoration:none" href="#request.self#?action=#url.action#&collection=#collection.name#&startrow=#url.startrow-10#&search=1">&lt;&lt;</a><cfelse>&nbsp;</cfif></td>
						<td class="tblHead" width="590" align="center"><cfscript>
							stResult=replace(stText.Search.result,'{startrow}',url.startrow);
							stResult=replace(stResult,'{endrow}',endrow);
							stResult=replace(stResult,'{recordcount}',result.recordCount);
							stResult=replace(stResult,'{recordssearched}',result.recordssearched);
						</cfscript>#stResult#</td>
						<td class="tblHead" width="30" align="center"><cfif url.startrow+10 LTE result.recordcount><a class="tblHead" style="text-decoration:none" href="#request.self#?action=#url.action#&collection=#collection.name#&startrow=#url.startrow+10#&search=1">&gt;&gt;</a><cfelse>&nbsp;</cfif></td>
					</tr>
					</cfsavecontent>
					#header#
					</cfoutput>
					<cfoutput query="result" startrow="#url.startrow#" maxrows="10">
					<tr>
						<td class="tblContent" colspan="3">
						<b><cfif len(trim(result.title)) EQ 0>{no title}<cfelse>#result.title#</cfif></b><br>
						<span class="comment">#result.summary#</span>
						</td>
					</tr>
					</cfoutput>
					<cfoutput>
					#header#
					</cfoutput>
					</table>
				</cfif>
			</cfif>

		</cfif>	
	</cfif>
</cfif>
