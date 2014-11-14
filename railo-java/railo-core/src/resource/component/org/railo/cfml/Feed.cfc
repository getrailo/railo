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
component extends="Base" accessors="true"{
	
	// tagname	
	variables.tagname = "feed";
	variables.properties = {};
	
	/* 
	read the feed
	The query,name,xmlvar and properties attributes are optional and overwritten.
	Result is a struct like:
	result = {query = query:Query, name = name:Struct, properties = properties:Struct, xmlvar = xmlVar:XML};
	*/					
	public Struct function read(){
		this.setAttributes(argumentCollection=arguments);
		this.setAction('read');
		return super.invokeTag();
	}

	/* 
	create the feed
	The xmlvar attributes is optional and represent the result of the create function.
	*/					

	public Struct function create(){
		this.setAttributes(argumentCollection=arguments);
		this.setAction('create');
		return super.invokeTag();
	}
	
	
	public Struct function getFeedProperties(){
		return variables.properties;
	}

	public void function setFeedProperties(Struct properties){
		variables.properties = properties;
	}
						
}
