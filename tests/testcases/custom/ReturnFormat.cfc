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
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		
	}

	public void function testNoFormatNoServerSetting(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=undefined")#" addtoken="false";
		
		assertEquals(
			"<wddxPacket version='1.0'><header/><data><array length='0'></array></data></wddxPacket>",
			trim(result.filecontent));
	}
	
	public void function testURLFormatNoServerSetting(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=undefined&returnFormat=json")#" addtoken="false";
		assertEquals(
			"[]",
			trim(result.filecontent));
	}
	
	public void function testURLandHeaderFormatNoServerSetting(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=undefined&returnFormat=wddx")#" addtoken="false"{
			httpparam type="header" name="accept" value="application/json";
		}
		assertEquals(
			"<wddxPacket version='1.0'><header/><data><array length='0'></array></data></wddxPacket>",
			trim(result.filecontent));
	}
	
	public void function testHeaderFormatNoServerSetting(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=undefined")#" addtoken="false"{
			httpparam type="header" name="accept" value="application/json";
		}
		assertEquals(
			"[]",
			trim(result.filecontent));
	}
	
	
	
	
	public void function testNoFormatServerJson(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=returnformat")#" addtoken="false";
		
		assertEquals(
			'{"columns":["a"],"data":[]}',
			trim(result.filecontent));
	}
	
	public void function testURLFormatServerJson(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=returnformat&returnFormat=wddx")#" addtoken="false";
		assertEquals(
			"<wddxPacket version='1.0'><header/><data><recordset rowCount='0' fieldNames='a' type='coldfusion.sql.QueryTable'><field name='a'></field></recordset></data></wddxPacket>",
			trim(result.filecontent));
	}
	
	public void function testURLandHeaderFormatServerJson(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=returnformat&returnFormat=wddx")#" addtoken="false"{
			httpparam type="header" name="accept" value="application/cfml";
		}
		assertEquals(
			"<wddxPacket version='1.0'><header/><data><recordset rowCount='0' fieldNames='a' type='coldfusion.sql.QueryTable'><field name='a'></field></recordset></data></wddxPacket>",
			trim(result.filecontent));
	}

	public void function testHeaderFormatServerJson(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=returnformat")#" addtoken="false"{
			httpparam type="header" name="accept" value="application/cfml";
		}
		assertEquals(
			'{"columns":["a"],"data":[]}',
			trim(result.filecontent));
	}

	public void function testHeaderFormatServerWDDX(){
		http method="get" result="result" url="#createURL("ReturnFormat/Test.cfc?method=returnformat2")#" addtoken="false"{
			httpparam type="header" name="accept" value="application/cfml";
		}
		assertEquals(
			"<wddxPacket version='1.0'><header/><data><recordset rowCount='0' fieldNames='a' type='coldfusion.sql.QueryTable'><field name='a'></field></recordset></data></wddxPacket>",
			trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>