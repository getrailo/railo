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
 ---><cfsavecontent variable="c">R0lGODlhEQAOAJECADMzM9LS0v///wAAACH5BAEAAAIALAAAAAARAA4AAAIuVI6pFuIPH3Kq0lYPAPMwtW3eCIYdloTc+WliwnzrCpPBa5F43rTZ1YsIbZlFAQA7</cfsavecontent><cfoutput><cfif getBaseTemplatePath() EQ getCurrentTemplatePath()><cfcontent type="image/gif" variable="#toBinary(c)#"><cfsetting showdebugoutput="no"><cfelse>data:image/gif;base64,#c#</cfif></cfoutput>