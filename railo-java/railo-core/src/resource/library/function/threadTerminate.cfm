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
 ---><cffunction name="ThreadTerminate" output="no" returntype="void" hint="Stops processing of the thread specified in the name attribute.
If you terminate a thread, the thread scope includes an ERROR metadata structure with information about the termination. (optional, default=run)"><cfargument 
	name="name" type="string" required="yes" hint="The name of the thread to stop."><!--- 
   
    ---><cfthread action="terminate" name="#arguments.name#"/><!---
---></cffunction>