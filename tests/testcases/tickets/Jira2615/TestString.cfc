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
component {

	remote function echoAny(data) {
		return arguments.data;
	}

	remote string function echoString(string sct) {
		return arguments.sct;
	}

	remote array function echoArray(array arr) {
		return arguments.arr;
	}
 
	remote string[] function echoStringArray(string[] data) {
		return arguments.data;
	} 

	remote string[][] function echoStringStringArray(string[][] data) {
		return arguments.data;
	}

	remote string[][][] function echoStringStringStringArray(string[][][] data) { 
		return arguments.data;
	}

}