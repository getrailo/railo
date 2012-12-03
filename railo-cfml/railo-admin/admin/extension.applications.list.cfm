<cfset hasAccess=true />

<cfset existing=struct() />

<!--- if user declined the agreement, show a msg --->
<cfif structKeyExists(session, "extremoved")>
	<cfoutput>
		<div class="warning">
			#stText.ext.msgafternotagreed#
		</div>
	</cfoutput>
	<cfset structDelete(session, "extremoved", false) />
</cfif>

<!--- upload own extension --->
<cfoutput>
	<h2>#stText.ext.uploadExtension#</h2>
	<div class="itemintro">#stText.ext.uploadExtensionDesc#</div>
	<cfif structKeyExists(url, 'noextfile')>
		<div class="error">
			#stText.ext.nofileuploaded#
		</div>
	</cfif>
	<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=upload" method="post" enctype="multipart/form-data">
		<input type="hidden" name="mainAction" value="uploadExt" />
		<table class="tbl maintbl">
			<tbody>
				<tr>
					<th scope="row">#stText.ext.extzipfile#</th>
					<td><input type="file" class="txt file" name="extfile" id="extfile" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td>&nbsp;</td>
					<td>
						<input type="submit" class="button submit" value="#stText.ext.upload#" />
					</td>
				</tr>
			</tfoot>
		</table>
	</cfform>
</cfoutput>

<cfif extensions.recordcount>
	<cfoutput>
		<!--- Installed Applications --->
		<h2>#stText.ext.installed#</h2>
		<div class="itemintro">#stText.ext.installeddesc#</div>

		<div class="filterform">
			<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
				<ul>
					<li>
						<label for="filter">#stText.search.searchterm#:</label>
						<input type="text" name="filter" id="filter" class="txt" value="#session.extFilter.filter#" />
					</li>
					<li>
						<input type="submit" class="button submit" name="mainAction" value="#stText.buttons.filter#" />
					</li>
				</ul>
				<div class="clear"></div>
			</cfform>
		</div>
		
		<div class="extensionlist">
			<cfloop query="extensions">
				<cfset uid=createId(extensions.provider,extensions.id)>
				<cfset existing[uid]=true>
				
				<cfif session.extFilter.filter neq "">
					<cftry>
						<cfset prov=getProviderData(extensions.provider)>
						<cfset provTitle=prov.info.title>
						<cfcatch>
							<cfset provTitle="">
						</cfcatch>
					</cftry>
				</cfif>
				
				<cfif 
				session.extFilter.filter eq ""
				or doFilter(session.extFilter.filter,extensions.label,false)
				or doFilter(session.extFilter.filter,extensions.category,false)
				or doFilter(session.extFilter.filter,provTitle,false)
				>
					<cfset link="#request.self#?action=#url.action#&action2=detail&uid=#uid#">
					<cfset dn=getDumpNail(extensions.image,130,50)>
					<cfset hasUpdate=updateAvailable(extensions)>
					<div class="extensionthumb">
						<a href="#link#" title="#stText.ext.viewdetails#">
							<div class="extimg">
								<cfif len(dn)>
									<img src="#dn#" alt="#stText.ext.extThumbnail#" />
								</cfif>
							</div>
							<b title="#extensions.label#">#cut(extensions.label,30)#</b><br />
							#cut(extensions.category,30)#
							<cfif hasUpdate>
								<br /><span class="CheckError">#stText.ext.updateavailable#</span>
							</cfif>
						</a>
					</div>
				</cfif>
			</cfloop>
			<div class="clear"></div>
		</div>
	</cfoutput>
</cfif>

<!---  Not Installed Applications --->
<cfoutput>
	<h2>#stText.ext.notInstalled#</h2>
	<div class="itemintro">#stText.ext.notInstalleddesc#</div>

	<div class="filterform">
		<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
			<ul>
				<li>
					<label for="filter2">#stText.search.searchterm#:</label>
					<input type="text" name="filter2" id="filter2" class="txt" value="#session.extFilter.filter2#" />
				</li>
				<li>
					<input type="submit" class="button submit" name="mainAction" value="#stText.buttons.filter#" />
				</li>
			</ul>
			<div class="clear"></div>
		</cfform>
	</div>
</cfoutput>

<cfif isQuery(data)>
	<div class="extensionlist">
		<cfoutput query="data" group="uid">
			<cfset info=data.info>
			<cfif !StructKeyExists(existing,data.uid)
			and (data.type EQ "all" or data.type EQ request.adminType or (data.type EQ "" and "web" EQ request.adminType)) 
			and (
				session.extFilter.filter2 eq ""
				or doFilter(session.extFilter.filter2,data.label,false)
				or doFilter(session.extFilter.filter2,data.category,false)
				or doFilter(session.extFilter.filter2,info.title,false)
			)
			>
				<cfset link="#request.self#?action=#url.action#&action2=detail&uid=#data.uid#">
				<cfset dn=getDumpNail(data.image,130,50)>
				<div class="extensionthumb">
					<a href="#link#" title="#stText.ext.viewdetails#">
						<div class="extimg">
							<cfif len(dn)>
								<img src="#dn#" alt="#stText.ext.extThumbnail#" />
							</cfif>
						</div>
						<b title="#data.label#">#cut(data.label,30)#</b><br />
						#cut(data.category,30)#
					</a>
				</div>
			</cfif>
		</cfoutput>
		<div class="clear"></div>
	</div>
</cfif>