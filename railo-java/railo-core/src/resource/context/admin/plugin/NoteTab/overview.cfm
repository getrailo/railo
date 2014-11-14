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
<cfoutput>
<form action="#action('update')#" method="post">
	<table border="0" cellpadding="0" cellspacing="0" bgcolor="##FFCC00"
		style="background-color:##FFCC00;border-style:solid;border-color:##000000;border-width:1px;padding:10px;">
	<tr>
		<td valign="top" >
			<textarea style="background-color:##FFCC00;border-style:solid;border-color:##000000;border-width:0px;" name="note" cols="40" rows="10">#req.note#</textarea>
		</td>
	</tr>
	</table>
	<br />
	<input class="submit" type="submit" name="submit" value="#lang.btnSubmit#" />
</form>
	
</cfoutput>
