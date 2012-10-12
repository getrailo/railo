<cfset dataDir = "../resources/searchdata/" />
<cfdirectory action="list" name="qlangs" directory="../resources/language/" filter="*.xml" />
<cfset translations = {} />
<cfset pageContents = {} />

<!--- clear the data dir --->
<cfdirectory action="list" directory="#datadir#" name="q" type="file" />
<cfloop query="q">
	<cfset filedelete(datadir & q.name) />
</cfloop>


<cfloop list="default,#valuelist(qlangs.name)#" index="currfile">
	<cfif currfile eq 'en.xml'>
		<cfcontinue />
	</cfif>
	<cfif currfile eq 'default'>
		<cfset temp = {} />
		<cfset currfile = "en.xml" />
	<cfelse>
		<cfset temp = duplicate(translations.en) />
	</cfif>
	<cfset x = xmlParse(fileread('../resources/language/#currfile#', 'utf-8')) />
	<cfloop array="#x.language.data#" index="item">
		<cfset temp[item.xmlattributes.key] = item.xmlText />
	</cfloop>
	<cfset translations[listfirst(currfile, '.')] = temp />
	<cfset pageContents[listfirst(currfile, '.')] = {} />
</cfloop>

<cfset searchresults = {} />
<cfdirectory action="list" directory="../" filter="*.*.cfm" name="qFiles" sort="name" />
<cfloop query="qFiles">
	<cfset currFile = qFiles.directory & server.separator.file & qFiles.name />
	<cfset currAction = replace(qFiles.name, '.cfm', '') />

	<cfif listLen(currAction, '.') gt 2>
		<cfset issubpage = true />
		<cfset curraction = listfirst(currAction, '.') & '.' & listgetat(currAction, 2, '.') />
	<cfelse>
		<cfset issubpage = false />
	</cfif>
	
	<!--- remember file contents for each language --->
	<cfloop collection="#translations#" item="lng">
		<cfif fileExists('#datadir##curraction#.#lng#.txt')>
			<cfset pageContents[lng][currAction] = fileRead('#datadir##curraction#.#lng#.txt', 'utf-8') />
		<cfelse>
			<cfset pageContents[lng][currAction] = "" />
		</cfif>
	</cfloop>
	
	<cfset data = fileread(currfile) />
	<cfset finds = rematchNoCase('[''"##]stText\..+?[''"##]', data) />
	<cfloop array="#finds#" index="str">
		<cfset str = rereplace(listRest(str, '.'), '.$', '') />
		<!--- only use it if we have a translation --->
		<cfif structKeyExists(translations.en, str)>
			<!--- remember file contents for each language --->
			<cfloop collection="#translations#" item="lng">
				<cfset pageContents[lng][currAction] &= " " & translations[lng][str] />
			</cfloop>
			<cfif not structKeyExists(searchresults, str)>
				<cfset searchresults[str] = {} />
			</cfif>
			<cfif not structKeyExists(searchresults[str], currAction)>
				<cfset searchresults[str][currAction] = 1 />
			<cfelse>
				<cfset searchresults[str][currAction]++ />
			</cfif>
		</cfif>
	</cfloop>
	
	<!--- save translated file contents to disk --->
	<cfloop collection="#translations#" item="lng">
		<cffile action="write" file="#datadir##curraction#.#lng#.txt" charset="utf-8" output="#rereplace(pageContents[lng][currAction], '<.*?>', '', 'all')#" mode="644" />
	</cfloop>
	
</cfloop>

<!--- store the searchresults --->
<cffile action="write" file="#datadir#searchindex.cfm" charset="utf-8" output="#serialize(searchresults)#" mode="644" />

search data updated!