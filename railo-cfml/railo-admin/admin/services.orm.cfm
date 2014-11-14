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
 ---><cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="orm"
	secValue="yes">
	
<cfadmin 
	action="getORMSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="settings">
<cfadmin 
	action="getORMEngine"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="engine">
	
<!---
<div class="CheckError" style="width:740px">
The ORM Implementation is currently in Beta State. Its functionality can change before it's final release.
If you have any problems while using the ORM Implementation, please post the bugs and errors in our <a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
<br /><br />
</div>--->

<cfinclude template="services.orm.list.cfm"/>