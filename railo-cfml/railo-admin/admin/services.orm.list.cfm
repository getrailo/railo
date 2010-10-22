<cfif request.adminType EQ "web">
	<cfset resetLabel=stText.Buttons.resetServerAdmin>
<cfelse>
	<cfset resetLabel=stText.Buttons.reset>
</cfif>
    
<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.update#">
        	
            <cfadmin 
                action="updateORMSetting"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                
                autogenmap="#structKeyExists(form,'autogenmap') and form.autogenmap#"
                eventHandling="#structKeyExists(form,'eventHandling') and form.eventHandling#"
                flushatrequestend="#structKeyExists(form,'flushatrequestend') and form.flushatrequestend#"
                logSQL="#structKeyExists(form,'logSQL') and form.logSQL#"
                savemapping="#structKeyExists(form,'savemapping') and form.savemapping#"
                useDBForMapping="#structKeyExists(form,'useDBForMapping') and form.useDBForMapping#"
                 
                catalog="#form.catalog#"
                cfclocation="#form.cfclocation#"
                dbcreate="#form.dbcreate#"
                schema="#form.schema#"
                
				
                sqlscript="#settings.sqlscript#"
				cacheconfig="#settings.cacheconfig#"
				cacheProvider="#settings.cacheProvider#"
				ormConfig="#settings.ormConfig#"
				secondarycacheenabled="#settings.secondarycacheenabled#"
                
                
                remoteClients="#request.getRemoteClients()#">	
		</cfcase>
	<!--- RESET --->
		<cfcase value="#resetLabel#">
        	ddd
            <cfadmin 
                action="resetORMSetting"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                
                remoteClients="#request.getRemoteClients()#">	
		</cfcase>
    
	</cfswitch>
	<cfcatch><cfrethrow>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!---
Redirtect to entry  --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq stText.Buttons.verify>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>





<cfoutput>
<h2>#stText.Settings.orm.title#</h2>


<table class="tbl" width="600">
<tr>
	<td colspan="2">#stText.Settings.orm.desc#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">


<!--- autogenmap --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.autogenmap#</td>
	<td class="tblContent">
			<input type="checkbox" name="autogenmap" value="true"<cfif settings.autogenmap>  checked="checked"</cfif>>
			<span class="comment">#stText.Settings.orm.autogenmapDesc#</span>
		
		
	</td>
</tr>

<!--- catalog --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.catalog#</td>
	<td class="tblContent">
			<input type="text" name="catalog" size="80" value="#settings.catalog#" /><br />
			<span class="comment">#stText.Settings.orm.catalogDesc#</span>
		
		
	</td>
</tr>


<!--- schema --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.schema#</td>
	<td class="tblContent">
			<input type="text" name="schema" size="80" value="#settings.schema#" /><br />
			<span class="comment">#stText.Settings.orm.schemaDesc#</span>
		
		
	</td>
</tr>

<!--- cfc location --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.cfclocation#</td>
	<td class="tblContent">
			<input type="text" name="cfclocation" size="80" value="#settings.isDefaultCfclocation?"":arrayToList(settings.cfclocation)#" /><br />
			<span class="comment">#stText.Settings.orm.cfclocationDesc#</span>
		
		
	</td>
</tr>


<!--- dbcreate --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.dbcreate#</td>
	<td class="tblContent">
    	<span class="comment">#stText.Settings.orm.autogenmapDesc#<br /><br /></span>
		<cfloop index="item" list="none,update,dropcreate">
        	<input type="radio" class="radio" name="dbcreate" value="#item#" <cfif settings.dbcreate EQ item>checked="checked"</cfif>> #item#<br />
            <span class="comment">#stText.Settings.orm['dbcreate'& item]#</span><BR />
        </cfloop>
	</td>
</tr>

<!--- dialect

makes no sense to define this here
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.dialect#</td>
	<td class="tblContent">
			<input type="text" name="dialect" size="80" value="#settings.dialect#" /><br />
			<span class="comment">#stText.Settings.orm.dialectDesc#</span>
		
		
	</td>
</tr>
 --->

<!--- eventHandling --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.eventHandling#</td>
	<td class="tblContent">
			<input type="checkbox" name="eventHandling" value="true"<cfif settings.eventHandling>  checked="checked"</cfif>>
			<span class="comment">#stText.Settings.orm.eventHandlingDesc#</span>
		
		
	</td>
</tr>

<!--- flushatrequestend --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.flushatrequestend#</td>
	<td class="tblContent">
			<input type="checkbox" name="flushatrequestend" value="true"<cfif settings.flushatrequestend>  checked="checked"</cfif>>
			<span class="comment">#stText.Settings.orm.flushatrequestendDesc#</span>
		
		
	</td>
</tr>

<!--- logSQL --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.logSQL#</td>
	<td class="tblContent">
			<input type="checkbox" name="logSQL" value="true"<cfif settings.logSQL>  checked="checked"</cfif>>
			<span class="comment">#stText.Settings.orm.logSQLDesc#</span>
		
		
	</td>
</tr>

<!--- savemapping --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.savemapping#</td>
	<td class="tblContent">
			<input type="checkbox" name="savemapping" value="true"<cfif settings.savemapping>  checked="checked"</cfif>>
			<span class="comment">#stText.Settings.orm.savemappingDesc#</span>
		
		
	</td>
</tr>

<!--- useDBForMapping --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.useDBForMapping#</td>
	<td class="tblContent">
			<input type="checkbox" name="useDBForMapping" value="true"<cfif settings.useDBForMapping>  checked="checked"</cfif>>
			<span class="comment">#stText.Settings.orm.useDBForMappingDesc#</span>
		
		
	</td>
</tr>

<!--- sqlscript
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.sqlscript#</td>
	<td class="tblContent">
			<input type="text" name="sqlscript" size="80" value="#settings.sqlscript#" /><br />
			<span class="comment">#stText.Settings.orm.sqlscriptDesc#</span>
		
		
	</td>
</tr>
  --->
<!--- 
	public static final Collection.Key SECONDARY_CACHE_ENABLED = KeyImpl.getInstance("secondarycacheenabled");
	public static final Collection.Key CACHE_CONFIG = KeyImpl.getInstance("cacheconfig");
	public static final Collection.Key CACHE_PROVIDER = KeyImpl.getInstance("cacheProvider");
	public static final Collection.Key ORM_CONFIG = KeyImpl.getInstance("ormConfig");

--->





<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<input class="submit" type="submit" class="submit" name="mainAction" value="#resetLabel#">
	</td>
</tr>
</cfif>

</cfform>
</table>
<br /><br />
</cfoutput>