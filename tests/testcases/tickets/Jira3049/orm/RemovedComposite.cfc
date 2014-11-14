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
 ---><cfcomponent name="RemovedComposite"
			 entityName="RemovedComposite"
			 persistent="true"
			 table="removed_composite"
			 output="false"
			 accessors="true" >

	<cfproperty name="UnitId" column="unit_id" fieldtype="id" type="string" />
	<cfproperty name="EntityId" column="entity_id" fieldtype="id" type="string" />
	<cfproperty name="EntityTypeId" column="entity_type_id" type="numeric" />
</cfcomponent>