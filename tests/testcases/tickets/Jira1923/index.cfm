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
 ---><cfsetting showdebugoutput="no">
<cfoutput>
	<cfif isDefined("form.test")>form:#serialize(form.test)#->#serialize(getApplicationSettings().sameformfieldsasarray)#;</cfif><cfif isDefined("url.test")>url:#serialize(url.test)#->#serialize(getApplicationSettings().sameurlfieldsasarray)#;</cfif><!---

--->
</cfoutput>