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
 ---><!---pre>
security
- file (rechte vergeben
	- none (kein Zugriff auf FS)
	- local (nur lokalen Zugriff auf FS; also alles oberhalb webroot dir ?was ist wenn mapping ausserhalb)
	- all (volles schreibrecht)
- java
	- none (kann java reflexion nicht nutzen Bsp: now().getTime() )
	- all darf ohne einschraenkung
settings
- regional (Standarteinstellung fuer die lokaladmins)
- component  (Standarteinstellung fuer die lokaladmins)
- scope (Standarteinstellung fuer die lokaladmins)

global services
- datasource (DS die allen zur verfuegung stehen)
- Mail (Standarteinstellung fuer die lokaladmins)

trace
	(was geht ab, uberischt laufender request, memory usage, 
	hier sollten auch logger eingeschaltet werden koennen)
</pre>--->

<cfset contextes=admin.contextes>

<cfoutput>
<cfloop collection="#contextes#" item="key">
	<cfset con=contextes[key]>
	<cfset _config=contextes[key].config>
	key:#key#<br>
	<cfdump var="#con.engineInfo.specificationVersion#">
	<cfdump var="#_config.servletContext#">
	<cfdump var="#_config#">
	<cfdump var="#con#">
	<br>
	<cfbreak>
</cfloop>
</cfoutput>
