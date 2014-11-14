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
 ---><cffunction name="ajaxOnLoad" output="true" hint="Causes the specified JavaScript function to run when the page loads."><cfargument 
	name="functionname" required="no" hint="The name of the function to run when the page loads."/><!--- 
	
	---><cfif len(arguments.functionname)><!--- 
		
		load js lib if required 
		---><cfajaximport /><!--- 
		
		subscribe to the onload event
		 ---><cfoutput><script type="text/javascript">Railo.Events.subscribe(#arguments.functionname#,'onLoad');</script></cfoutput></cfif><!--- 
---></cffunction>