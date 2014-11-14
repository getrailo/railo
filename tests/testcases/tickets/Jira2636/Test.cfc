/**
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
 **/
<cfscript>
component {
	remote function base(required boolean b,numeric n) {
		return arguments;
	}

 	remote function object(obj) {
		return obj;
	}
	
 	remote boolean function boolean(boolean b) {
		return b;
	}
	
 	remote numeric function number(numeric n) {
		return n;
	}
	
 	remote array function array(array a) {
		return a;
	}
	
 	remote struct function struct(struct s) {
		return s;
	}
	
 	remote query function query(query q) {
		return q;
	}
	
 	remote component function component(component c) {
		return c;
	}
 	remote string function string(string s) {
		return s;
	}
	
} 
</cfscript>