<cfparam name="error" default="#struct(message:"",detail:"")#">

<!--- 
ACTIONS --->
<cftry>
	<cfif StructKeyExists(form,"run") and form.run EQ "create2">
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
		
		<cfadmin 
			action="updateDatasource"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			
			classname="#driver.getClass()#"
			dsn="#driver.getDSN()#"
						
			name="#form.name#"
			host="#form.host#"
			database="#form.database#"
			port="#form.port#"
			dbusername="#form.username#"
			dbpassword="#form.password#"
			
			connectionLimit="#form.connectionLimit#"
			connectionTimeout="#form.connectionTimeout#"
			blob="#getForm('blob',false)#"
			clob="#getForm('clob',false)#"
			
			
			allowed_select="#getForm('allowed_select',false)#"
			allowed_insert="#getForm('allowed_insert',false)#"
			allowed_update="#getForm('allowed_update',false)#"
			allowed_delete="#getForm('allowed_delete',false)#"
			allowed_alter="#getForm('allowed_alter',false)#"
			allowed_drop="#getForm('allowed_drop',false)#"
			allowed_revoke="#getForm('allowed_revoke',false)#"
			allowed_create="#getForm('allowed_create',false)#"
			allowed_grant="#getForm('allowed_grant',false)#"
			custom="#custom#"
			remoteClients="#request.getRemoteClients()#">
		<cflocation url="#request.self#?action=#url.action#" addtoken="no">
	</cfif>
	<cfcatch>
		<cfset driver.onBeforeError(cfcatch)>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- 
Error Output--->
<cfset printError(error)>
<cfsilent>
<cfif structKeyExists(url,'name')>
	<cfset actionType="update">
	<cfadmin 
	action="getDatasource"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	name="#url.name#"
	returnVariable="datasource">
	<cfset datasource.password="****************">
	<cfset datasource.type=getType(datasource.classname,datasource.dsn)>
<cfelse>
	<cfset actionType="create">
	<cfset datasource=struct()>
	<cfset datasource.type=form.type>
	<cfset datasource.name=form.name>
</cfif>


<cfset driver=createObject("component","dbdriver."&datasource.type)>


<cfif not structKeyExists(url,'name')>
	<cfset datasource.host=driver.getValue('host')>
	<cfset datasource.database=driver.getValue('database')>
	<cfset datasource.port=driver.getValue('port')>
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
	
</cfif>
<!--- overwrite values, with values from form scope --->
<cfloop collection="#form#" item="key">
	<cfif structKeyExists(datasource,key)>
		<cfset datasource[key]=form[key]>
	</cfif>
</cfloop>
<cfset driver.init(datasource)>
<cfset fields=driver.getFields()>


</cfsilent>

<cfoutput>
<h2>
	<cfif actionType EQ "update">
	#stText.Settings.DatasourceDescriptionUpdate#
	<cfelse>
	#stText.Settings.DatasourceDescriptionCreate#
	</cfif> #driver.getName()#</h2>

<table class="tbl" width="450">
<tr>
	<td colspan="2">#driver.getDescription()#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#&action2=create#iif(isDefined('url.name'),de('&name=##url.name##'),de(''))#" method="post">
<input type="hidden" name="name" value="#datasource.name#">
<input type="hidden" name="type" value="#datasource.type#">

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

<tr>
	<td class="tblHead" width="150">Name</td>
	<td class="tblContent" width="300">#datasource.name#</td>
</tr>
<!--- 

Host --->
<cfif typeHost NEQ TYPE_HIDDEN>
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbHost#</td>
	<td class="tblContent" width="300">
		<span class="comment">#stText.Settings.dbHostDesc#</span><br>
		<cfinput type="text" name="host" 
		value="#datasource.host#" style="width:300px" required="#typeHost EQ TYPE_REQUIRED#"></td>
</tr>
</cfif>
<!--- 

DataBase --->
<cfif typeDataBase NEQ TYPE_HIDDEN>
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbDatabase#</td>
	<td class="tblContent" width="300">
		<span class="comment">#stText.Settings.dbDatabaseDesc#</span><br>
		<cfinput type="text" name="database" 
		value="#datasource.database#" style="width:300px" required="#typeDataBase EQ TYPE_REQUIRED#"></td>
</tr>
</cfif>
<!--- 

Port --->
<cfif typePort NEQ TYPE_HIDDEN>
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbPort#</td>
	<td class="tblContent" width="300">
		<span class="comment">#stText.Settings.dbPortDesc#</span><br>
		<cfinput type="text" name="port" validate="integer" 
		value="#datasource.port#" style="width:60px" required="#typePort EQ TYPE_REQUIRED#"></td>
</tr>
</cfif>
<!--- 

Username --->
<cfif typeUsername NEQ TYPE_HIDDEN>
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbUser#</td>
	<td class="tblContent" width="300">
		<span class="comment">#stText.Settings.dbUserDesc#</span><br>
		<cfinput type="text" name="username" 
		value="#datasource.username#" style="width:300px" required="#typeUsername EQ TYPE_REQUIRED#"></td>
</tr>
</cfif>
<!--- 

Password --->
<cfif typePassword NEQ TYPE_HIDDEN>
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbPass#</td>
	<td class="tblContent" width="300">
		<span class="comment">#stText.Settings.dbPassDesc#</span><br>
		<cfinput type="password" name="Password"  passthrough='autocomplete="off"'
		value="#datasource.password#" style="width:300px" onClick="this.value='';" required="#typePassword EQ TYPE_REQUIRED#"></td>
</tr>
</cfif>
<tr>
	<td width="150" colspan="2">&nbsp;</td>
</tr>
<!--- 

Connection Limit --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbConnLimit#</td>
	<td class="tblContent" width="300">
		<select name="ConnectionLimit" class="select">
			<option value="-1" <cfif datasource.ConnectionLimit EQ -1>selected</cfif>>#stText.Settings.dbConnLimitInf#</option>
			<cfloop index="idx" from="1" to="10"><option  <cfif datasource.ConnectionLimit EQ idx>selected</cfif>>#idx#</option></cfloop>
			<cfloop index="idx" from="20" to="100" step="10"><option  <cfif datasource.ConnectionLimit EQ idx>selected</cfif>>#idx#</option></cfloop>
			<cfloop index="idx" from="200" to="1000" step="100"><option  <cfif datasource.ConnectionLimit EQ idx>selected</cfif>>#idx#</option></cfloop>
		</select>
		<span class="comment">#stText.Settings.dbConnLimitDesc#</span>
	</td>
</tr>
<!--- 

Connection Timeout --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbConnTimeout#</td>
	<td class="tblContent" width="300">
		<select name="ConnectionTimeout" class="select">
			<cfloop index="idx" from="0" to="20"><option  <cfif datasource.ConnectionTimeout EQ idx>selected</cfif>>#idx#</option></cfloop>
		</select>
		<!--- <cfinput type="text" name="ConnectionTimeout" 
		validate="integer" value="#datasource.ConnectionTimeout#" style="width:60px"> --->
		<span class="comment">#stText.Settings.dbConnTimeoutDesc#</span>
	</td>
</tr>
<!--- 

Blob --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbBlob#</td>
	<td class="tblContent" width="300">
		<cfinput type="checkbox" class="checkbox" name="blob" value="yes" checked="#datasource.blob#">
		<span class="comment">#stText.Settings.dbBlobDesc#</span>
	</td>
</tr>
<!--- 

Clob --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbClob#</td>
	<td class="tblContent" width="300">
		<cfinput type="checkbox" class="checkbox" name="clob" value="yes" checked="#datasource.clob#">
		<span class="comment">#stText.Settings.dbClobDesc#</span>
	</td>
</tr>
<!--- 

Allow --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.dbAllowed#</td>
	<td class="tblContent" width="300">
		<table width="100%">
		<tr>
			<td class="darker" align="right">Select:<cfinput type="checkbox" class="checkbox" name="allowed_select" value="yes" 
			checked="#datasource.select#"></td>
			<td class="darker" align="right">Insert:<cfinput type="checkbox" class="checkbox" name="allowed_insert" value="yes" 
			checked="#datasource.insert#"></td>
			<td class="darker" align="right">Update:<cfinput type="checkbox" class="checkbox" name="allowed_update" value="yes" 
			checked="#datasource.update#"></td>
			<td class="darker" align="right">Delete:<cfinput type="checkbox" class="checkbox" name="allowed_delete" value="yes" 
			checked="#datasource.delete#"></td>
		</tr>
		<tr>
			<td class="darker" align="right">Create:<cfinput type="checkbox" class="checkbox" name="allowed_create" value="yes" 
			checked="#datasource.create#"></td>
			<td class="darker" align="right">Drop:<cfinput type="checkbox" class="checkbox" name="allowed_drop" value="yes" 
			checked="#datasource.drop#"></td>
			<td class="darker" align="right">Revoke:<cfinput type="checkbox" class="checkbox" name="allowed_revoke" value="yes" 
			checked="#datasource.revoke#"></td>
			<td class="darker" align="right">Alter:<cfinput type="checkbox" class="checkbox" name="allowed_alter" value="yes" 
			checked="#datasource.alter#"></td>
		</tr>
		<tr>
			<td class="darker" align="right">Grant:<cfinput type="checkbox" class="checkbox" name="allowed_grant" value="yes" 
			checked="#datasource.grant#"></td>
			<td class="darker" align="right">&nbsp;</td>
			<td class="darker" align="right">&nbsp;</td>
			<td class="darker" align="right">&nbsp;</td>
		</tr>
		</table>
		
	</td>
</tr>
<cfif arrayLen(fields)>
<tr>
	<td width="150" colspan="2">&nbsp;</td>
</tr>
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
	<td class="tblHead" width="150">#field.getDisplayName()#</td>
	<td class="tblContent" width="300"><span class="comment">#field.getDescription()#</span>
	<cfif type EQ "text" or type EQ "password">
	<cfinput type="#type#" 
		name="custom_#field.getName()#" 
		value="#default#" style="width:300px" required="#field.getRequired()#" 
		message="Missing value for field #field.getDisplayName()#">
	<cfelseif type EQ "select">
		<cfoutput><br />
		<cfif default EQ field.getDefaultValue() and field.getRequired()><cfset default=listFirst(default)></cfif>
        <select name="custom_#field.getName()#">
			<cfif not field.getRequired()><option value=""> ---------- </option></cfif>
			<cfif len(trim(default))>
			<cfloop index="item" list="#field.getDefaultValue()#">
			<option <cfif item EQ default>selected="selected"</cfif> >#item#</option>
			</cfloop>
			</cfif>
		</select>
		</cfoutput>
	<!--- @todo type checkbox --->
	<cfelseif type EQ "radio">
		<cfoutput><br />
		<cfif default EQ field.getDefaultValue() and field.getRequired()><cfset default=listFirst(default)></cfif>
		<cfloop index="item" list="#field.getDefaultValue()#">
			<cfinput type="radio" name="custom_#field.getName()#" value="#item#" checked="#item EQ default#">
			#item#
		</cfloop>
		
		</cfoutput>
	<!--- @todo type checkbox,radio --->
	</cfif></td>
</tr>
</cfloop>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
	<input type="hidden" name="run" value="create2">
	<input type="submit" class="submit" name="_run" value="#stText.Buttons[actionType]#">
	<input onClick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="cancel" value="#stText.Buttons.Cancel#"></td>
</tr>
</cfform>
</table>
</cfoutput>