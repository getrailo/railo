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