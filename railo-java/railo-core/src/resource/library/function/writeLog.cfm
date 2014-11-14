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
 ---><cffunction name="writeLog" output="no" returntype="void" hint="Writes a message to a log file."><cfargument 
	name="text" type="string" required="yes" hint="Message text to log."><cfargument 
    name="type" type="string" required="no" default="Information" hint="Type (severity) of the message:
- information (default)
- warning
- error
- fatal"><cfargument 
	
    name="application" type="boolean" required="no" hint="log application name, if it is specified in a cfapplication tag."><cfargument 
    name="file" type="string" required="no" hint="Message file. Specify only the main part of the filename. For example, to log to the Testing.log file, specify ""Testing"". The file must be located in the default log directory. You cannot specify a directory path. 
If the file does not exist, it is created automatically, with the suffix .log."><cfargument 
	
    name="log" type="string" required="no" hint="If you omit the file argument, writes messages to standard log file. Ignored, if you specify file argument.
- application: writes to Application.log, normally used for application-specific messages.
- scheduler: writes to Scheduler.log, normally used to log the execution of scheduled tasks."><!---
    
	---><cflog attributeCollection="#arguments#"><!---
---></cffunction>