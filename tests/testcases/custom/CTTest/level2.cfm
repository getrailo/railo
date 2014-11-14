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
 --->

<cfif thistag.EXECUTIONMODE EQ "start">
	<cfset caller.fromLevel2="caller.2">
	
	<cfset caller.from.Level2="caller_2">
	
	
	
	<cfset caller.caller.fromLevel2="caller.caller.2">
	<cfset c="caller">
	<cfset "#c#.#c#.fromLevel2Eval"="caller.caller.2.eval">
	
	<cfset caller.caller.from.Level2="caller_caller_2">
	<cfset "#c#.#c#.fro.mLevel2.Eval"="caller-caller.2.eval">
	
</cfif>