<cfparam name="error" default="#struct(message:"",detail:"")#">

<!--- ACTIONS --->
<cftry>
	<cfif StructKeyExists(form,"_run") and form._run EQ stText.Settings.flushCache>
		<cfset DatasourceFlushMetaCache(form.name)>
		
	<cfelseif StructKeyExists(form,"run") and form.run EQ "create2">
		<cfset driver=createObject("component","dbdriver."&form.type)>
		<cfset driver.onBeforeUpdate()>
		<cfset custom=struct()>
		<cfloop collection="#form#" item="key">
			<cfif findNoCase("custom_",key) EQ 1>
				<cfset l=len(key)>
				<cfset custom[mid(key,8,l-8+1)]=form[key]>
			</cfif>
		</cfloop>
		<cfif form.password EQ "****************">
			<cfadmin 
				action="getDatasource"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				name="#form.name#"
				returnVariable="existing">
			<cfset form.password=existing.password>
		</cfif>
		<cfset verify=getForm('verify',false)>
		<cfparam name="form.metaCacheTimeout" default="60000">
		
		<cfadmin 
			action="updateDatasource"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			
			classname="#driver.getClass()#"
			dsn="#driver.getDSN()#"
						
			name="#form.name#"
			newName="#form.newName#"
			
			host="#form.host#"
			database="#form.database#"
			port="#form.port#"
			timezone="#form.timezone#"
			dbusername="#form.username#"
			dbpassword="#form.password#"
			
			connectionLimit="#form.connectionLimit#"
			connectionTimeout="#form.connectionTimeout#"
			metaCacheTimeout="#form.metaCacheTimeout#"
			blob="#getForm('blob',false)#"
			clob="#getForm('clob',false)#"
			validate="#getForm('validate',false)#"
			storage="#getForm('storage',false)#"
			
			
			allowed_select="#getForm('allowed_select',false)#"
			allowed_insert="#getForm('allowed_insert',false)#"
			allowed_update="#getForm('allowed_update',false)#"
			allowed_delete="#getForm('allowed_delete',false)#"
			allowed_alter="#getForm('allowed_alter',false)#"
			allowed_drop="#getForm('allowed_drop',false)#"
			allowed_revoke="#getForm('allowed_revoke',false)#"
			allowed_create="#getForm('allowed_create',false)#"
			allowed_grant="#getForm('allowed_grant',false)#"
			verify="#verify#"
			custom="#custom#"
			remoteClients="#request.getRemoteClients()#">
			<cfset form.mark="update">
		<cfset v="">
		<cfif verify>
			<cfset v="&verified="&form.name>
		</cfif>
		<cflocation url="#request.self#?action=#url.action##v#" addtoken="no">
	</cfif>
	<cfcatch>
		<cfset driver.onBeforeError(cfcatch)>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<!--- Error Output--->
<cfset printError(error)>

<cfsilent>
	<cfset isInsert=structKeyExists(form,'mark') and form.mark EQ "create">
	
	<cfif isInsert>
		<cfset actionType="create">
		<cfset datasource=struct()>
		<cfset datasource.type=form.type>
		<cfset datasource.name=form.name>
		<cfset datasource.storage=false>
		<cfset datasource.validate=false>
		
	<cfelse>
		<cfset actionType="update">
		
		<cfadmin 
		action="getDatasource"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		name="#structKeyExists(url,'name')?url.name:form.name#"
		returnVariable="datasource">
		
		<cfset datasource._password=datasource.password>
		<cfset datasource.password="****************">
		<cfset datasource.type=getType(datasource.classname,datasource.dsn)>
	</cfif>
	
	<cfset driver=createObject("component","dbdriver."&datasource.type)>

	<cfif isInsert>
		<cfset datasource.host=driver.getValue('host')>
		<cfset datasource.database=driver.getValue('database')>
		<cfset datasource.port=driver.getValue('port')>
		<cfset datasource.timezone="">
		<cfset datasource.username=driver.getValue('username')>
		<cfset datasource.password=driver.getValue('password')>
		<cfset datasource.ConnectionLimit=driver.getValue('ConnectionLimit')>
		<cfset datasource.ConnectionTimeout=driver.getValue('ConnectionTimeout')>
		<cfset datasource.blob=driver.getValue('blob')>
		<cfset datasource.clob=driver.getValue('clob')>
		
		<cfset datasource.select=driver.getValue('allowed_select')>
		<cfset datasource.insert=driver.getValue('allowed_insert')>
		<cfset datasource.update=driver.getValue('allowed_update')>
		<cfset datasource.delete=driver.getValue('allowed_delete')>
		<cfset datasource.create=driver.getValue('allowed_create')>
		<cfset datasource.drop=driver.getValue('allowed_drop')>
		<cfset datasource.revoke=driver.getValue('allowed_revoke')>
		<cfset datasource.alter=driver.getValue('allowed_alter')>
		<cfset datasource.grant=driver.getValue('allowed_grant')>
		<cfset datasource.metaCacheTimeout=60000>
	</cfif>
	
	<cfif not structKeyExists(datasource,'metaCacheTimeout')>
		<cfset datasource.metaCacheTimeout=60000>
	</cfif>

	<!--- overwrite values, with values from form scope --->
	<cfloop collection="#form#" item="key">
		<cfif structKeyExists(datasource,key)>
			<cfset datasource[key]=form[key]>
		</cfif>
	</cfloop>
	<cfset driver.init(datasource)>
	<cfset fields=driver.getFields()>

	<cfadmin 
		action="getTimeZones"
		locale="#stText.locale#"
		returnVariable="timezones">
</cfsilent>

<cfoutput>
	<h2>
		<cfif actionType EQ "update">
			#stText.Settings.DatasourceDescriptionUpdate#
		<cfelse>
			#stText.Settings.DatasourceDescriptionCreate#
		</cfif>
		#driver.getName()#
	</h2>
	<div class="pageintro">#driver.getDescription()#</div>
	
	<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=create" method="post">
		<input type="hidden" name="name" value="#datasource.name#">
		<input type="hidden" name="type" value="#datasource.type#">
		
		<cfif actionType EQ "update">
		
			<h3>JDBC Connection Data</h3>
			Based on the data below.
			<table class="maintbl">
				<tbody>
					<tr>
						<th scope="row">Driver Class</th>
						<td>#datasource.classname#</td>
					</tr>
					<tr>
						<th scope="row">Connection String</th>
						<td>
							<cfif len(datasource._password)>
								#replace(datasource.dsnTranslated,datasource._password,datasource.password,'all')#
							<cfelse>
								#datasource.dsnTranslated#
							</cfif>
						</td>
					</tr>
				</tbody>
			</table>
			<br>
		</cfif>

		<cfsilent>
			<cfset TYPE_HIDDEN=0>
			<cfset TYPE_FREE=1>
			<cfset TYPE_REQUIRED=2>
			
			<cfset typeHost=driver.getType('host')>
			<cfset typeDatabase=driver.getType('database')>
			<cfset typePort=driver.getType('port')>
			<cfset typeUsername=driver.getType('username')>
			<cfset typePassword=driver.getType('password')>
		</cfsilent>
		<cfif typeHost EQ TYPE_HIDDEN><input type="hidden" name="host" value="#datasource.host#"></cfif>
		<cfif typeDatabase EQ TYPE_HIDDEN><input type="hidden" name="database" value="#datasource.database#"></cfif>
		<cfif typePort EQ TYPE_HIDDEN><input type="hidden" name="port" value="#datasource.port#"></cfif>
		<cfif typeUsername EQ TYPE_HIDDEN><input type="hidden" name="username" value="#datasource.username#"></cfif>
		<cfif typePassword EQ TYPE_HIDDEN><input type="hidden" name="password" value="#datasource.password#"></cfif>
		
		
		<h3>Datasource configuration</h3>
		<table class="maintbl">
			<tbody>
				<tr>
					<th scope="row">Name</th>
					<td>
						<cfinput type="text" name="newName" value="#datasource.name#" class="large">
					</td>
				</tr>
				<!--- Host --->
				<cfif typeHost NEQ TYPE_HIDDEN>
					<tr>
						<th scope="row">#stText.Settings.dbHost#</th>
						<td>
							<cfinput type="text" name="host" 
							value="#datasource.host#" class="large" required="#typeHost EQ TYPE_REQUIRED#">
							<div class="comment">#stText.Settings.dbHostDesc#</div>
						</td>
					</tr>
				</cfif>
				<!--- DataBase --->
				<cfif typeDataBase NEQ TYPE_HIDDEN>
					<tr>
						<th scope="row">#stText.Settings.dbDatabase#</th>
						<td>
							<cfinput type="text" name="database" 
							value="#datasource.database#" class="large" required="#typeDataBase EQ TYPE_REQUIRED#">
							<div class="comment">#stText.Settings.dbDatabaseDesc#</div>
						</td>
					</tr>
				</cfif>
				<!--- Port --->
				<cfif typePort NEQ TYPE_HIDDEN>
					<tr>
						<th scope="row">#stText.Settings.dbPort#</th>
						<td>
							<cfinput type="text" name="port" validate="integer" 
							value="#datasource.port#" class="small" required="#typePort EQ TYPE_REQUIRED#">
							<div class="comment">#stText.Settings.dbPortDesc#</div>
						</td>
					</tr>
				</cfif>
				<!--- Timezone --->
				<tr>
					<th scope="row">#stText.Settings.dbtimezone#</th>
					<td>
						<select name="timezone" class="large">
							<option value=""> ---- #stText.Settings.dbtimezoneSame# ---- </option>
							<cfoutput query="timezones">
								<option value="#timezones.id#"
								<cfif timezones.id EQ datasource.timezone>selected</cfif>>
								#timezones.id# - #timezones.display#</option>
							</cfoutput>
						</select>
						<div class="comment">#stText.Settings.dbtimezoneDesc#</div>
						<div class="warning nofocus">
							This feature is currently in Beta State.
							If you have any problems while using this Implementation,
							please post the bugs and errors in our
							<a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank">bugtracking system</a>. 
						</div>
					</td>
				</tr>
				<!--- Username --->
				<cfif typeUsername NEQ TYPE_HIDDEN>
					<tr>
						<th scope="row">#stText.Settings.dbUser#</th>
						<td>
							<cfinput type="text" name="username" 
							value="#datasource.username#" class="medium" required="#typeUsername EQ TYPE_REQUIRED#">
							<div class="comment">#stText.Settings.dbUserDesc#</div>
						</td>
					</tr>
				</cfif>
				<!--- Password --->
				<cfif typePassword NEQ TYPE_HIDDEN>
					<tr>
						<th scope="row">#stText.Settings.dbPass#</th>
						<td>
							<cfinput type="password" name="Password"  passthrough='autocomplete="off"'
							value="#datasource.password#" class="medium" onClick="this.value='';" required="#typePassword EQ TYPE_REQUIRED#">
							<div class="comment">#stText.Settings.dbPassDesc#</div>
						</td>
					</tr>
				</cfif>
			</tbody>
		</table>
		<br />
		<table class="maintbl">
			<tbody>
				<!--- Connection Limit --->
				<tr>
					<th scope="row">#stText.Settings.dbConnLimit#</th>
					<td>
						<select name="ConnectionLimit" class="select small">
							<option value="-1" <cfif datasource.ConnectionLimit EQ -1>selected</cfif>>#stText.Settings.dbConnLimitInf#</option>
							<cfloop index="idx" from="1" to="10"><option  <cfif datasource.ConnectionLimit EQ idx>selected</cfif>>#idx#</option></cfloop>
							<cfloop index="idx" from="20" to="100" step="10"><option  <cfif datasource.ConnectionLimit EQ idx>selected</cfif>>#idx#</option></cfloop>
							<cfloop index="idx" from="200" to="1000" step="100"><option  <cfif datasource.ConnectionLimit EQ idx>selected</cfif>>#idx#</option></cfloop>
						</select>
						<div class="comment">#stText.Settings.dbConnLimitDesc#</div>
					</td>
				</tr>
				<!--- Connection Timeout --->
				<tr>
					<th scope="row">#stText.Settings.dbConnTimeout#</th>
					<td>
						<select name="ConnectionTimeout" class="select small">
							<cfloop index="idx" from="0" to="20"><option  <cfif datasource.ConnectionTimeout EQ idx>selected</cfif>>#idx#</option></cfloop>
						</select>
						<!--- <cfinput type="text" name="ConnectionTimeout" 
						validate="integer" value="#datasource.ConnectionTimeout#" style="width:60px"> --->
						<div class="comment">#stText.Settings.dbConnTimeoutDesc#</div>
					</td>
				</tr>
				<!--- validate --->
				<tr>
					<th scope="row">#stText.Settings.dbValidate#</th>
					<td>
						<cfinput type="checkbox" class="checkbox" name="validate" value="yes" checked="#isDefined('datasource.validate') and datasource.validate#">
						<div class="comment">#stText.Settings.dbValidateDesc#</div>
					</td>
				</tr>

				<!--- Meta Cache--->
				<cfif datasource.type EQ "oracle">
					<tr>
						<th scope="row">#stText.Settings.dbMetaCacheTimeout#</th>
						<td>
							<cfset selected=false>
							<select name="metaCacheTimeout" class="select small">
								<option value="-1" <cfif datasource.metaCacheTimeout EQ -1><cfset selected=true>selected</cfif>>#stText.Settings.dbConnLimitInf#</option>
								
								<optgroup label="#stText.Settings.minutes#">
									<cfloop index="idx" from="1" to="10"><option value="#idx*60000#"  <cfif datasource.metaCacheTimeout EQ idx*60000><cfset selected=true>selected</cfif>>#idx# #stText.Settings.minutes#</option></cfloop>
									<cfloop index="idx" from="20" to="50" step="10"><option value="#idx*60000#"  <cfif datasource.metaCacheTimeout EQ idx*60000><cfset selected=true>selected</cfif>>#idx# #stText.Settings.minutes#</option></cfloop>
								</optgroup>
								<optgroup label="#stText.Settings.hours#">
									<cfloop index="idx" from="1" to="23"><option value="#idx*60000*60#"  <cfif datasource.metaCacheTimeout EQ idx*60000*60><cfset selected=true>selected</cfif>>#idx# #stText.Settings.hours#</option></cfloop>
								</optgroup>
								<optgroup label="#stText.Settings.days#">
									<cfloop index="idx" from="1" to="30"><option value="#idx*60000*60*24#"  <cfif datasource.metaCacheTimeout EQ idx*60000*60*24><cfset selected=true>selected</cfif>>#idx# #stText.Settings.days#</option></cfloop>
								</optgroup>
							</select>
							<div class="comment">#stText.Settings.dbMetaCacheTimeoutDesc#</div>
						</td>
					</tr>
					<cfif actionType EQ "update">
						<tr>
							<th scope="row">#stText.Settings.flushCache#</th>
							<td>
								<input type="submit" class="button submit" name="_run" value="#stText.Settings.flushCache#">
							</td>
						</tr>
					</cfif>
				</cfif>
				<!--- Blob --->
				<tr>
					<th scope="row">#stText.Settings.dbBlob#</th>
					<td>
						<cfinput type="checkbox" class="checkbox" name="blob" value="yes" checked="#datasource.blob#">
						<div class="comment">#stText.Settings.dbBlobDesc#</div>
					</td>
				</tr>
				<!--- Clob --->
				<tr>
					<th scope="row">#stText.Settings.dbClob#</th>
					<td>
						<cfinput type="checkbox" class="checkbox" name="clob" value="yes" checked="#datasource.clob#">
						<div class="comment">#stText.Settings.dbClobDesc#</div>
					</td>
				</tr>
				<!--- Allow --->
				<tr>
					<th scope="row">#stText.Settings.dbAllowed#</th>
					<td>
						<ul class="radiolist float">
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_select" value="yes" checked="#datasource.select#"> <b>Select</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_insert" value="yes" checked="#datasource.insert#"> <b>Insert</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_update" value="yes" checked="#datasource.update#"> <b>Update</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_delete" value="yes" checked="#datasource.delete#"> <b>Delete</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_create" value="yes" checked="#datasource.create#"> <b>Create</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_drop" value="yes" checked="#datasource.drop#"> <b>Drop</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_revoke" value="yes" checked="#datasource.revoke#"> <b>Revoke</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_alter" value="yes" checked="#datasource.alter#"> <b>Alter</b></label></li>
							<li class="small"><label><cfinput type="checkbox" class="checkbox" name="allowed_grant" value="yes" checked="#datasource.grant#"> <b>Grant</b></label></li>
						</ul>
					</td>
				</tr>
				<!--- storage --->
				<tr>
					<th scope="row">#stText.Settings.dbStorage#</th>
					<td>
						<cfinput type="checkbox" class="checkbox" name="storage" value="yes" checked="#isDefined('datasource.storage') and datasource.storage#">
						<div class="comment">#stText.Settings.dbStorageDesc#</div>
					</td>
				</tr>

				<cfif arrayLen(fields)>
						</tbody>
					</table>
					<br />
					<table class="maintbl">
						<tbody>
				</cfif>
				<cfloop collection="#fields#" item="idx">
					<cfset field=fields[idx]>
					<cfif StructKeyExists(datasource,"custom") and StructKeyExists(datasource.custom,field.getName())>
						<cfset default=datasource.custom[field.getName()]>
					<cfelse>
						<cfset default=field.getDefaultValue()>
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
							<cfelseif type EQ "select">
								<cfif default EQ field.getDefaultValue() and field.getRequired()><cfset default=listFirst(default)></cfif>
								<select name="custom_#field.getName()#" class="large">
									<cfif not field.getRequired()><option value=""> ---------- </option></cfif>
									<cfif len(trim(default))>
										<cfloop index="item" list="#field.getDefaultValue()#">
											<option <cfif item EQ default>selected="selected"</cfif> >#item#</option>
										</cfloop>
									</cfif>
								</select>
							<!--- @todo type checkbox --->
							<cfelseif type EQ "radio">
								<cfif default EQ field.getDefaultValue() and field.getRequired()><cfset default=listFirst(default)></cfif>
								<cfloop index="item" list="#field.getDefaultValue()#">
									<cfinput type="radio" class="radio" name="custom_#field.getName()#" value="#item#" checked="#item EQ default#">
									#item#
								</cfloop>
							<!--- @todo type checkbox,radio --->
							</cfif>
						</td>
					</tr>
				</cfloop>
				<tr>
					<th scope="row">#stText.Settings.verifyConnection#</th>
					<td><input type="checkbox" class="checkbox" checked="checked" name="verify" value="true" /></td>
				</tr>
				<cfmodule template="remoteclients.cfm" colspan="2">
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="hidden" name="mark" value="#structKeyExists(form,'mark')?form.mark:'update'#">
						<input type="hidden" name="run" value="create2">
						<input type="submit" class="button submit" name="_run" value="#stText.Buttons[actionType]#">
						<input onclick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="cancel" value="#stText.Buttons.Cancel#">
					</td>
				</tr>
			</tfoot>
		</table>
	</cfform>
</cfoutput>