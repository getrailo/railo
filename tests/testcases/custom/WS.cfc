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
		ws = CreateObject("webservice","http://fleurop-svc1.exigo.ch/WsSimple6.svc?singleWsdl&nc=#getTickCount()#");
		
	}

	
	public void function testServerDateTime(){
		assertEquals(
			dateTimeFormat(now(),'yyyy.MM.dd HH:nn'),
			dateTimeFormat(ws.ServerDateTime(),'yyyy.MM.dd HH:nn'));
	}

	public void function testConcatWithMessage(){
		assertEquals(
			'{">ConcatWithMessage>returnMsg":"123 123",">ConcatWithMessageResponse>ConcatWithMessageResult":null}',
			serializeJson(ws.ConcatWithMessage('123','123','a')));
	}

	public void function testConcat(){
		assertEquals("123 123",ws.Concat('123','123'));
	}

	public void function testGetObject(){
		obj=ws.getObject();
		// TODO add assertEquals();
	}

	public void function testSetObject(){
		obj=ws.getObject();
		ws.setObject(obj);
		// TODO add assertEquals();
	}
} 
</cfscript>