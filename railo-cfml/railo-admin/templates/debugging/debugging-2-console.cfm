<cfif findNoCase("/railo-context/templates/display/", cgi.script_name) eq 0>
	<cfsilent>
		<cffunction name="getDebugOptions" returntype="struct">
			<cfset var stRet = structNew()>
			<cfif structKeyExists(server, "stDebugOptions")>
				<cfset stRet = server.stDebugOptions>
				<cfelse>
				<cfset stRet.iTime           = 0>
				<cfset stRet.bExclude        = 0>
				<cfset stRet.lstDebugFilters = "">
				</cfif>
			<cfreturn stRet>
		</cffunction>
		<cftry>
			<cfset bFound    = false>
			<cfset bFiltered = false>
			<cfset sScript = cgi.script_name & "?" & cgi.query_string>
			<!--- Read stuff from a function --->
			<cfset stDebugOptions = getDebugOptions()>
			<!--- definiert ein Array mit Filtern und ein Flag ob negativer Filter oder nicht --->
			<cfloop list="#stDebugOptions.lstDebugFilters#" index="lst">
				<cfif FindNoCase(trim(lst),  sScript)>
					<cfset bFound = true>
					<cfbreak>
				</cfif>
			</cfloop>
			<cfif bFound>
				<cfset bFiltered = not stDebugOptions.bExclude>
			<cfelse>
				<cfset bFiltered = stDebugOptions.bExclude>
			</cfif>
			<cfif not bFiltered>
				<cfset sWeb_ID = getPageContext().getConfig().getId()>
				<cfif not findNoCase("debugOutput", getBaseTemplatePath())>
					<cfset time=getTickCount()>
					<cfadmin action="getDebugData" returnVariable="debugging">
					<cfset bDebugMe = true>
					<cfset iTotal = 0>
					<cfloop query="debugging.pages">
						<cfset iTotal = iTotal + debugging.pages.total>
					</cfloop>
					<cfif stDebugOptions.iTime gt 0>
						<cfif iTotal lt stDebugOptions.iTime>
							<cfset bDebugMe = false>
						</cfif>
					</cfif>
					<cfif bDebugMe>
						<cfif not StructKeyExists(server, sWeb_ID)>
							<cfset server[sWeb_ID]                 = StructNew()>
							<cfset server[sWeb_ID].debugEntries    = ArrayNew(1)>
							<cfset server[sWeb_ID].debugEntryCount = 0>
						</cfif>
						<cfset server[sWeb_ID].debugEntryCount = server[sWeb_ID].debugEntryCount + 1>
						<cfif server[sWeb_ID].debugEntryCount gt 10><cfset server[sWeb_ID].debugEntryCount = 1></cfif>
						<cfset server[sWeb_ID].debugEntries[server[sWeb_ID].debugEntryCount] = StructNew()>
						<cfset server[sWeb_ID].debugEntries[server[sWeb_ID].debugEntryCount].recorded  = now()>
						<cfset server[sWeb_ID].debugEntries[server[sWeb_ID].debugEntryCount].iTotal    = iTotal>
						<cfset server[sWeb_ID].debugEntries[server[sWeb_ID].debugEntryCount].ipAddress = cgi.remote_addr>
						<cfset server[sWeb_ID].debugEntries[server[sWeb_ID].debugEntryCount].calledUrl = sScript>
						<cfset server[sWeb_ID].debugEntries[server[sWeb_ID].debugEntryCount].debugInfo = debugging>
					</cfif>
				</cfif>
			</cfif>
	<cfcatch></cfcatch>
	</cftry>
	</cfsilent>
</cfif>