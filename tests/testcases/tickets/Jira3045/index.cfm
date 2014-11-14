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
 ---><cfscript>
setting showdebugoutput="no";
exp=dateAdd("d", 30, now());
COOKIE.test1 ={ domain: ".mydomain.com", path: "/", value: "Hello", expires: exp, httpOnly: true };

cookie name="test2" domain=".mydomain.com" path="/" value="Hallo" expires="#exp#" httpOnly="true";
</cfscript>
