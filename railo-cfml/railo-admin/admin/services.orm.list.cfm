


<cfset stText.Settings.orm.title="Settings">
<cfset stText.Settings.orm.desc="Here you can define the default settings for the ORM Configuration, this settings can be overwritten in the Application.cfc with struct ""ormsettings""">

<cfset stText.Settings.orm.autogenmap="Automatically generate mapping">
<cfset stText.Settings.orm.autogenmapDesc="Specifies whether Railo should automatically generate mapping for the persistent CFCs. If disabled, mapping should be provided in the form of .HBMXML files. 
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.autogenmap=true]">

<cfset stText.Settings.orm.catalog="Catalog">
<cfset stText.Settings.orm.catalogDesc="Specifies the default Catalog that should be used by ORM. 
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.catalog='db']">

<cfset stText.Settings.orm.cfclocation="Persistent Component Location">
<cfset stText.Settings.orm.cfclocationDesc="Specifies the directory that should be used to search for persistent CFCs to generate the mapping. If cfclocation is set, Railo looks at only the paths specified in it. If it is not set, Railo looks at the application directory, its sub-directories, and its mapped directories to search for persistent Comonents.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.cfclocation='...']">

<cfset stText.Settings.orm.dbcreate="Creating Tables behavior">
<cfset stText.Settings.orm.dbcreateDesc="Railo ORM can automatically create the tables for your application in the database when ORM is initialized for the application.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.dbcreate='none']">
<cfset stText.Settings.orm.dbcreateNone="Setting this value does not change anything in the database schema.">
<cfset stText.Settings.orm.dbcreateUpdate="Setting this value creates the table if it does not exist or update the table if it exists.">
<cfset stText.Settings.orm.dbcreateDropcreate=" Setting this value drops the table if it exists and then creates it.">


<cfset stText.Settings.orm.eventHandling="Event Handling">
<cfset stText.Settings.orm.eventHandlingDesc="Specifies whether ORM Event callbacks should be given. 
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.eventHandling=false]">

<cfset stText.Settings.orm.flushatrequestend="Flush at request end">
<cfset stText.Settings.orm.flushatrequestendDesc="Specifies whether ormflush should be called automatically at request end.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.flushatrequestend=false]">

<cfset stText.Settings.orm.logSQL="Log SQL">
<cfset stText.Settings.orm.logSQLDesc="Specifies whether the SQL queries that are executed by ORM will be logged.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.logSQL=false]">

<cfset stText.Settings.orm.savemapping="Save Mapping">
<cfset stText.Settings.orm.savemappingDesc="Specifies whether the generated Hibernate mapping file has to be saved to file system. If enabled, the Hibernate mapping XML file is saved with the filename ""CFC name"".hbm.xml in the same directory as the CFC.
If any value of savemapping is specified in CFC, it will override the value specified in the ormsetting.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.savemapping=false]">

<cfset stText.Settings.orm.schema="Schema">
<cfset stText.Settings.orm.schemaDesc="Specifies the default Schema that should be used by ORM.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.schema='sch']">

<cfset stText.Settings.orm.useDBForMapping="Use DB for mapping">
<cfset stText.Settings.orm.useDBForMappingDesc="Specifies whether the database has to be inspected to identify the missing information required to generate the Hibernate mapping. The database is inspected to get the column data type, primary key and foreign key information.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.useDBForMapping=true]">


<cfset stText.Settings.orm.sqlscript="SQL Script">
<cfset stText.Settings.orm.sqlscriptDesc="Path to the SQL script file that gets executed after ORM is initialized. This applies if dbcreate is set to dropcreate. This must be the absolute file path or the path relative to the application.The SQL script file lets you populate the tables before the application is accessed.
<br>This setting can be overwritten in Application.cfc as follows [this.ormsettings.sqlscript='...']">





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
			<input type="text" name="cfclocation" size="80" value="#settings.cfclocation#" /><br />
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

<!--- sqlscript --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.orm.sqlscript#</td>
	<td class="tblContent">
			<input type="text" name="sqlscript" size="80" value="#settings.sqlscript#" /><br />
			<span class="comment">#stText.Settings.orm.sqlscriptDesc#</span>
		
		
	</td>
</tr>
 
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
		<input type="submit" class="submit" name="mainAction1" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction1" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>

</cfform>
</table>
<br /><br />

<h2>#stText.application.listener#</h2>
#stText.application.listenerDescription#

<table class="tbl" width="600">

<cfform action="#request.self#?action=#url.action#" method="post">

<!--- listener type 
<tr>
	<td class="tblHead">#stText.application.listenerType#</td>
	<td class="tblContent">
	<cfif hasAccess>
		<span class="comment">#stText.application.listenerTypeDescription#</span><br />
		<table class="tbl" width="600">
		<cfloop index="key" list="none,classic,modern,mixed">
		<tr>
			<td width="200" class="tblHead" nowrap="nowrap">#stText.application['listenerType_' & key]#</td>
			<td width="400" class="tblContent"><input type="radio" name="type" value="#key#" <cfif listener.type EQ key>checked="checked"</cfif>>
			<span class="comment">#stText.application['listenerTypeDescription_' & key]#</span></td>
		</tr>
		</cfloop>
		</table>
	<cfelse>
		<input type="hidden" name="type" value="#listener.type#">
		<b>#listener.type#</b><br />
		<span class="comment">#stText.application['listenerTypeDescription_' & listener.type]#</span>
	</cfif>
	</td>
</tr>
--->

<!--- listener mode
<tr>
	<td class="tblHead">#stText.application.listenerMode#</td>
	<td class="tblContent">
	<cfif hasAccess>
		<span class="comment">#stText.application.listenerModeDescription#</span><br />
		<table class="tbl" width="600">
		<cfloop index="key" list="curr,root,curr2root">
		<tr>
			<td width="200" class="tblHead" nowrap="nowrap">#stText.application['listenerMode_' & key]#</td>
			<td width="400" class="tblContent"><input type="radio" name="mode" value="#key#" <cfif listener.mode EQ key>checked="checked"</cfif>>
			<span class="comment">#stText.application['listenerModeDescription_' & key]#</span></td>
		</tr>
		</cfloop>
		</table>
	<cfelse>
		<input type="hidden" name="type" value="#listener.mode#">
		<b>#listener.mode#</b><br />
		<span class="comment">#stText.application['listenerModeDescription_' & listener.mode]#</span>
	</cfif>
	</td>
</tr>



 --->
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="3">
<tr>
	<td colspan="3">
		<input type="submit" class="submit" name="mainAction2" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction2" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>
<br><br>