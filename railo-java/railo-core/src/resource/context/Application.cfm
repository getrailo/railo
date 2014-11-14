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
 ---><cfapplication 
	name="railo_context" 
    clientmanagement="no" 
    clientstorage="file" 
    scriptprotect="none" 
    sessionmanagement="yes"
    sessiontimeout="#createTimeSpan(0,0,30,0)#"
    setclientcookies="yes" 
    setdomaincookies="no" 
    applicationtimeout="#createTimeSpan(1,0,0,0)#"
    localmode="update">