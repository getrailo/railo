<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ---><cfsilent>
	<cfif not directoryExists(dataDir)>
		<cfdirectory action="create" directory="#dataDir#" mode="777" recurse="true" />
	</cfif>

	<cfdirectory action="list" name="qlangs" directory="#expandPath('{railo-web}/context/admin/resources/language/')#" filter="*.xml" />

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
		<cfset x = xmlParse(fileread('resources/language/#currfile#', 'utf-8')) />
		<cfloop array="#x.language.data#" index="item">
			<cfset temp[item.xmlattributes.key] = item.xmlText />
		</cfloop>
		<cfset translations[listfirst(currfile, '.')] = temp />
		<cfset pageContents[listfirst(currfile, '.')] = {} />
	</cfloop>

	<cfset searchresults = {} />
	<cfdirectory action="list" directory="#railoArchiveZipPath#/admin" filter="*.*.cfm" name="qFiles" sort="name" />

	<cfloop query="qFiles">
		<cfset currFile = qFiles.directory & "/" & qFiles.name />
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
				<!--- make sure we will also find this page when searching for the file name--->
				<cfset pageContents[lng][currAction] = "#replace(curraction, '.', ' ')# " />
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

	<!--- remember the Railo version which is now in use --->
	<cffile action="write" file="#datadir#indexed-railo-version.cfm" output="#server.railo.version##server.railo['release-date']#" mode="644" addnewline="no" />

	<!--- store the searchresults --->
	<cffile action="write" file="#datadir#searchindex.cfm" charset="utf-8" output="#serialize(searchresults)#" mode="644" />
</cfsilent>