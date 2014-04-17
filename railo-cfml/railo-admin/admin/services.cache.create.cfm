<cffunction name="addZero">
	<cfargument name="str">
 <!---   <while len(str) LT 2>
    	<cfset str="0"&str>
    </while>--->
    <cfreturn arguments.str>
</cffunction>
<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.submit#">
			<cfset custom=struct()>
		
		
			<!--- custom --->
			<cfloop collection="#form#" item="key">
				<cfif left(key,13) EQ "custompart_d_">
					<cfset name=mid(key,14,10000)>
					<cfset custom[name]=(form["custompart_d_"&name]*86400)+(form["custompart_h_"&name]*3600)+(form["custompart_m_"&name]*60)+form["custompart_s_"&name]>
				</cfif>
			</cfloop>	   
			<cfloop collection="#form#" item="key">
				<cfif left(key,7) EQ "custom_">
					<cfset custom[mid(key,8,10000)]=form[key]>
				</cfif>
			</cfloop>
			<cfadmin 
				action="updateCacheConnection"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				
				name="#trim(form.name)#" class="#trim(form.class)#" 
				storage="#isDefined('form.storage') and form.storage#"
				default="#StructKeyExists(form,'default')?form.default:""#" 
				custom="#custom#"
				
				remoteClients="#request.getRemoteClients()#">
					
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq "none">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<cfset isNew=false>
<cfif StructKeyExists(url,'name')>
	<cfloop query="connections" >
		<cfif hash(connections.name) EQ url.name>
			<cfset connection=querySlice(connections,connections.currentrow,1)>
			<cfset driver=drivers[connections.class]>
		</cfif> 
	</cfloop>
<cfelse>
	<cfset isNew=true>
	<cfset connection=struct()>
	<cfset connection.class=form.class>
	<cfset connection.storage=false>
	<cfset connection.default=false>
	<cfset connection.name=form._name>
	<cfset connection.custom=struct()>
	<cfset driver=drivers[form.class]>
</cfif>
<cfoutput>
	<!--- 
	Error Output --->
	<cfset printError(error)>

	<h2>#driver.getLabel()# (#connection.class#)</h2>
	<div class="pageintro">#driver.getDescription()#</div>
	<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=create#iif(isDefined('url.name'),de('&name=##url.name##'),de(''))#" method="post">
		<cfinput type="hidden" name="class" value="#connection.class#">
		<cfinput type="hidden" name="name" value="#connection.name#" >
		<cfinput type="hidden" name="_name" value="#connection.name#" >
		<table class="maintbl">
			<tbody>
				<tr>
					<th scope="row">#stText.Settings.cache.Name#</th>
					<td>#connection.name#</td>
				</tr>
				<tr>
					<th scope="row">#stText.Settings.cache.storage#</th>
					<td>
						<cfinput type="checkbox" class="checkbox" name="storage" value="yes" checked="#connection.storage#">
						<div class="comment">#stText.Settings.cache.storageDesc#</div>
					</td>
				</tr>
			</tbody>
		</table>
		<br />
		<table class="maintbl">
			<tbody>
				<cfset custom=connection.custom>
				<cfloop array="#driver.getCustomFields()#" index="field">
					<cfif isInstanceOf(field,"Group")>
							</tbody>
						</table>
						<h#field.getLevel()#>#field.getDisplayName()#</h#field.getLevel()#>
						<div class="itemintro">#field.getDescription()#</div>
						<table class="maintbl">
							<tbody>
						<cfcontinue>
					</cfif>

					<cfset doBR=true>
					<cfif StructKeyExists(custom,field.getName())>
						<cfset default=custom[field.getName()]>
					<cfelseif isNew>
						<cfset default=field.getDefaultValue()>
					<cfelse>
						<cfset default="">
					</cfif>
					<cfset type=field.getType()>
					<tr>
						<th scope="row">#field.getDisplayName()#</th>
						<td>
							<cfif type EQ "text" or type EQ "password">
								<cfinput type="#type#" 
									name="custom_#field.getName()#" 
									value="#default#" class="large" required="#field.getRequired()#" 
									message="Missing value for field #field.getDisplayName()#">
							<cfelseif type EQ "textarea">
								<textarea class="large" style="height:70px;" name="custom_#field.getName()#">#default#</textarea>
							<cfelseif type EQ "time">
								<cfsilent>
									<cfset doBR=false>
									<cfif len(default) EQ 0>
										<cfset default=0>
									<cfelse>
										<cfset default=default+0>
									</cfif> 
									
									<cfset s=default>
									<cfset m=0>
									<cfset h=0>
									<cfset d=0>
									
									<cfif s GT 0>
										<cfset m=int(s/60)>
										<cfset s-=m*60>
									</cfif>
									<cfif m GT 0>
										<cfset h=int(m/60)>
										<cfset m-=h*60>
									</cfif>
									<cfif h GT 0>
										<cfset d=int(h/24)>
										<cfset h-=d*24>
									</cfif>
								</cfsilent>
								<table class="maintbl autowidth">
									<thead>
										<tr>
											<th>#stText.General.Days#</td>
											<th>#stText.General.Hours#</td>
											<th>#stText.General.Minutes#</td>
											<th>#stText.General.Seconds#</td>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td><cfinput type="text" 
												name="custompart_d_#field.getName()#" 
												value="#addZero(d)#" class="number" required="#field.getRequired()#"   validate="integer"
												message="Missing value for field #field.getDisplayName()#"></td>
											<td><cfinput type="text" 
												name="custompart_h_#field.getName()#" 
												value="#addZero(h)#" class="number" required="#field.getRequired()#"  maxlength="2"  validate="integer"
												message="Missing value for field #field.getDisplayName()#"></td>
											<td><cfinput type="text" 
												name="custompart_m_#field.getName()#" 
												value="#addZero(m)#" class="number" required="#field.getRequired()#"  maxlength="2" validate="integer" 
												message="Missing value for field #field.getDisplayName()#"></td>
											<td><cfinput type="text" 
												name="custompart_s_#field.getName()#" 
												value="#addZero(s)#" class="number" required="#field.getRequired()#"  maxlength="2"  validate="integer"
												message="Missing value for field #field.getDisplayName()#"></td>
										</tr>
									</tbody>
								</table>
							<cfelseif type EQ "select">
								<cfif default EQ field.getDefaultValue() and field.getRequired()>
									<cfset default=listFirst(default)>
								</cfif>
								<select name="custom_#field.getName()#">
									<cfif not field.getRequired()><option value=""> ---------- </option></cfif>
									<cfif len(trim(default))>
										<cfloop index="item" list="#field.getDefaultValue()#">
											<option <cfif item EQ default>selected="selected"</cfif> >#item#</option>
										</cfloop>
									</cfif>
								</select>
							<cfelseif type EQ "radio" or type EQ "checkbox">
								<cfset desc=field.getDescription()>
								<cfif isStruct(desc) and StructKeyExists(desc,'_top')>
									<div class="comment">#desc._top#</div>
								</cfif>
								<cfif listLen(field.getValues()) GT 1>
									<ul class="radiolist">
										<cfloop index="item" list="#field.getValues()#">
											<li>
												<label>
													<cfinput type="#type#" class="#type#" name="custom_#field.getName()#" value="#item#" checked="#item EQ default#">
													<b>#item#</b>
												</label>
												<cfif isStruct(desc) and StructKeyExists(desc,item)>
													<div class="comment" style="padding-bottom:4px">#desc[item]#</div>
												</cfif>
											</li>
										</cfloop>
									</ul>
								<cfelse>
									<cfset item = field.getValues() />
									<cfinput type="#type#" class="#type#" name="custom_#field.getName()#" value="#item#" checked="#item EQ default#">
								</cfif>
								<cfif isStruct(desc) and StructKeyExists(desc,'_bottom')>
									<div class="comment">#desc._bottom#</div>
								</cfif>
							</cfif>
							<cfif isSimpleValue(field.getDescription()) and len(trim(field.getDescription()))>
								<div class="comment">#field.getDescription()#</div>
							</cfif>
						</td>
					</tr>
				</cfloop>
				<tr>
					<th scope="row">#stText.Settings.cache.default#</th>
					<td>
						<select name="default">
							<option value="">------</option>
							<cfloop index="type" list="function,object,template,query,resource,include">
								<option <cfif connection.default EQ type>selected="selected"</cfif> value="#type#">#stText.Settings.cache['defaultType'& type]#</option>
							</cfloop>
						</select>
						<div class="comment">#stText.Settings.cache.defaultDesc#</div>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.submit#">
					</td>
				</tr>
			</tfoot>
		</table>
	</cfform>
</cfoutput>

<!---
<cfoutput>
<form action="#action('update')#" method="post">
	<table border="0" cellpadding="0" cellspacing="0" bgcolor="##FFCC00"
		style="background-color:##FFCC00;border-style:solid;border-color:##000000;border-width:1px;padding:10px;">
	<tr>
		<td valign="top" >
			<textarea style="background-color:##FFCC00;border-style:solid;border-color:##000000;border-width:0px;" name="note" cols="40" rows="10">#req.note#</textarea>
		</td>
	</tr>
	</table>
	<br />
	<input class="button submit" type="submit" name="submit" value="#lang.btnSubmit#" />
</form>
	
</cfoutput>
--->