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
 ---><cfif listFind("addfavorite,removefavorite", url.action2) and structKeyExists(url, "favorite")>
	<cfset application.adminfunctions[url.action2](url.favorite) />
	<cflocation url="?action=#url.favorite#" addtoken="no" />
<cfelseif listFind("setdata,adddata", url.action2) and structKeyExists(url, "key")>
	<cfset application.adminfunctions[url.action2](url.key, url.data) />
	<cfabort />
</cfif>

<cflocation url="#cgi.SCRIPT_NAME#" addtoken="no" />