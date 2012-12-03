<!--- Action --->
<cfinclude template="extension.functions.cfm">

<cfset stVeritfyMessages=struct()>
<cfparam name="error" default="#struct(message:"",detail:"")#">
<cfparam name="form.mainAction" default="none">
<cfset error.message="">
<cftry>
	<cfswitch expression="#form.mainAction#">
		<cfcase value="#stText.Buttons.verify#">
			<cfset data.urls=toArrayFromForm("url")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop from="1" to="#arrayLen(data.urls)#" index="idx">
				<cfif arrayIndexExists(data.rows,idx)>
					<cftry>
						<cfadmin 
							action="verifyExtensionProvider"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							
							url="#trim(data.urls[idx])#">
							<cfset stVeritfyMessages["#data.urls[idx]#"].Label = "OK">
						<cfcatch>
							<cfset stVeritfyMessages["#data.urls[idx]#"].Label = "Error">
							<cfset stVeritfyMessages["#data.urls[idx]#"].message = cfcatch.message>
							<cfset stVeritfyMessages["#data.urls[idx]#"].detail = cfcatch.detail>
							
						</cfcatch>
					</cftry> 
				</cfif>
			</cfloop>
			
		</cfcase>
		<cfcase value="#stText.Buttons.save#">
			<cfset data.urls=toArrayFromForm("url")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop from="1" to="#arrayLen(data.urls)#" index="idx">
				<cfif arrayIndexExists(data.rows,idx)>
					<cfadmin 
						action="updateExtensionProvider"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						url="#trim(data.urls[idx])#">
				</cfif>
			</cfloop>
		</cfcase>
		<cfcase value="#stText.Buttons.delete#">
			<cfset data.urls=toArrayFromForm("url")>
			<cfset data.rows=toArrayFromForm("row")>
			
			<cfloop from="1" to="#arrayLen(data.urls)#" index="idx">
				<cfif arrayIndexExists(data.rows,idx)>
					<cfadmin 
						action="removeExtensionProvider"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						url="#trim(data.urls[idx])#">
				</cfif>
			</cfloop>
		</cfcase>
		<cfcase value="#stText.Buttons.install#">
			<cfif StructKeyExists(form,"row") and StructKeyExists(data,"ids") and ArrayIndexExists(data.ids,row)>
				<cflocation url="#request.self#?action=#url.action#&action2=install1&provider=#data.hashProviders[row]#&app=#data.ids[row]#" addtoken="no">
			</cfif>
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<!--- Redirect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq stText.Buttons.verify>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>


<!--- Error Output --->
<cfset printError(error)>

<cfadmin 
	action="getExtensionProviders"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="providers">

<cfset hasAccess=true>

<cfset infos={}>
<cfloop query="providers">
	<cftry>
		<cfset provider = loadCFC(providers.url)>
		<cfset infos[providers.url] = provider.getInfo()>
		<cfcatch></cfcatch>
	</cftry>
</cfloop>



<!--- 
list all mappings and display necessary edit fields --->

<cfoutput>
	<cfset stText.ext.prov.title="Title">
	<cfset stText.ext.prov.mode="Mode">
	
	<cfset doMode=false>
	<cfloop query="providers">
		<cfif StructKeyExists(infos,providers.url) and StructKeyExists(infos[providers.url],"mode") and trim(infos[providers.url].mode) EQ "develop">
			<cfset doMode=true>
		</cfif>
	</cfloop>
	
	<cfset columns=doMode?5:4>

	<div class="itemintro">#stText.ext.prov.IntroText#</div>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl checkboxtbl">
			<thead>
				<tr>
					<th><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></th>
					<th>#stText.ext.prov.url#</th>
					<th>#stText.ext.prov.title#</th>
					<cfif doMode>
						<th>#stText.ext.prov.mode#</th>
					</cfif>
					<th>#stText.Settings.DBCheck#</th>
				</tr>
			</thead>
			<tbody id="extproviderlist">
				<cfloop query="providers">
					<tr>
						<!--- checkbox ---->
						<td>
							<cfif not providers.isReadOnly>
								<input type="checkbox" class="checkbox" name="row_#providers.currentrow#" value="#providers.currentrow#">
							</cfif>
						</td>
						<!--- url --->
						<td>
							<input type="hidden" name="url_#providers.currentrow#" value="#providers.url#">
							#providers.url#
						</td>
						<cfset hasData = StructKeyExists(infos,providers.url) />
						 
						<!--- title --->
						<td>
							<cfif hasData and StructKeyExists(infos[providers.url],"image")>
								<cfset dn=getDumpNail(infos[providers.url].image,100,30)>
								<cfif len(dn)>
									<img src="#dn#" border="0"/> &nbsp;
								</cfif>
							</cfif>
							<cfif hasData and StructKeyExists(infos[providers.url],"title") and len(trim(infos[providers.url].title))>
								#infos[providers.url].title#
							</cfif>
						</td>
						<!--- mode --->
						<cfif doMode>
							<td>
								<cfif hasData>
									<cfif StructKeyExists(infos[providers.url],"mode") and len(trim(infos[providers.url].mode))>
										#infos[providers.url].mode#
									<cfelse>
										production
									</cfif>
								</cfif>
							</td>
						</cfif>
						<!--- check --->
						<td>
							<cfif StructKeyExists(stVeritfyMessages, providers.url)>
								#stVeritfyMessages[providers.url].label#
							</cfif>
						</td>
					</tr>
				</cfloop>
			</tbody>
			<cfif hasAccess>
				<tfoot>
					 <tr>
						<td colspan="#columns#">
							<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.verify#">
							<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Delete#">
							<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
						</td>	
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>
	
	<cfif hasAccess>
		<h2>New Extension Provider</h2>
		<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
			<input type="hidden" name="row_1" value="1">
			<table class="maintbl" style="width:75%">
				<tbody>
					<tr>
						<th scope="row">
							Provider URL
						</th>
						<td>
							<cfinput onKeyDown="checkTheBox(this)" type="text" 
							name="url_1" value="" required="no" class="xlarge">
							<div class="comment">#stText.ext.prov.urlDesc#</div>
						</td>
					</tr>
				</tbody>
				<tfoot>
					 <tr>
						<td colspan="2">
							<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.save#">
						</td>	
					</tr>
				</tfoot>
			</table>
		</cfform>
	</cfif>
</cfoutput>
