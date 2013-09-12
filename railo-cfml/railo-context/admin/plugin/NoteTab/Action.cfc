<cfcomponent hint="Note" extends="railo-context.admin.plugin.Plugin">
	
	<cffunction name="init"
		hint="this function will be called to initalize">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfset app.note=load()>
		
	</cffunction>

	<cffunction name="overview" output="yes"
		hint="load data for a single note">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
		<cfset req.note=app.note>
	</cffunction>
	
	<cffunction name="update" output="no"
		hint="update note">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
		<cfset app.note=req.note>
		<cfset save(app.note)>
		
		<cfreturn "redirect:overview">
	</cffunction>
	
	<cffunction name="_display">
		<cfargument name="template" type="string">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
		
		<cfimport prefix="admin" taglib="../../">
		<cfset tabs=structNew("linked")>
		<cfset tabs.susi    = "susi">
		<cfset tabs.peter    = "peter">
		
		<admin:tabbedPane name="note" tabs="#tabs#" default="peter">
			<admin:tab name="susi">
				<cfinclude template="#template#">
			</admin:tab>
			<admin:tab name="peter">
				<cfinclude template="#template#">
			</admin:tab>
		</admin:tabbedPane>
		
		
	</cffunction>
	
</cfcomponent>