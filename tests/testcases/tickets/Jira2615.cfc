<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		variables.serviceURL =createURL("Jira2615/TestService.cfc?wsdl");
		variables.service = CreateObject("webservice", serviceURL);
		
		variables.sctURL =createURL("Jira2615/TestStruct.cfc?wsdl");
		variables.sctWS = CreateObject("webservice", sctURL);
		
		variables.strURL =createURL("Jira2615/TestString.cfc?wsdl");
		variables.strWS = CreateObject("webservice", strURL);

		variables.arrURL =createURL("Jira2615/TestArray.cfc?wsdl");
		variables.arrWS = CreateObject("webservice", arrURL);

		variables.qryURL =createURL("Jira2615/TestQuery.cfc?wsdl");
		variables.qryWS = CreateObject("webservice", qryURL);

		variables.myiURL =createURL("Jira2615/TestMyItem.cfc?wsdl");
		variables.myiWS = CreateObject("webservice", myiURL);
		
		variables.serviceAnyURL =createURL("Jira2615/TestServiceAny.cfc?wsdl");
		variables.serviceAny = CreateObject("webservice", serviceAnyURL);
		

	}

/** STRING **/
	public void function testStringEchoAny() localMode="modern" {
		data=strWS.echoAny('Susi');
		assertEquals("Susi",data);
	}

	public void function testStringEchoString() localMode="modern" {
		data=strWS.echoString("Susi");
		assertEquals("Susi",data);
	}

	public void function testStringEchoArray() localMode="modern" {
		data=strWS.echoArray(['Susi']);
		assertEquals("['Susi']",serialize(data));
	}

	public void function testStringEchoStringArray() localMode="modern" {
		data=strWS.echoStringArray(["Susi"]);
		assertEquals("['Susi']",serialize(data));
	}

	public void function testStringEchoStringStringArray() localMode="modern" {
		data=strWS.echoStringStringArray([["Susi"]]);
		assertEquals("[['Susi']]",serialize(data));
	}

	public void function testStringEchoStringStringStringArray() localMode="modern" {
		data=strWS.echoStringStringStringArray([[["Susi"]]]);
		assertEquals("[[['Susi']]]",serialize(data));
	}



/** STRUCT **/
	public void function testStructEchoAny() localMode="modern" {
		data=sctWS.echoAny({'a':12});
		assertEquals("{'a':12}",serialize(data));
		
		data=sctWS.echoAny([{'a':1},{'a':2}]);
		assertEquals("[{'a':1},{'a':2}]",serialize(data));
	}

	public void function testStructEchoStruct() localMode="modern" {
		data=sctWS.echoStruct({'a':23});
		assertEquals("{'a':23}",serialize(data));
	}

	public void function testStructEchoArray() localMode="modern" {
		data=sctWS.echoArray([{'a':45}]);
		assertEquals("[{'a':45}]",serialize(data));
	}

	public void function testStructEchoStructArray() localMode="modern" {
		data=sctWS.echoStructArray([{'a':1},{'b':2}]);
		assertEquals("[{'a':1},{'b':2}]",serialize(data));
	}

	public void function testStructEchoStructStructArray() localMode="modern" {
		data=sctWS.echoStructStructArray([[{'a':1},{'b':2}]]);
		assertEquals("[[{'a':1},{'b':2}]]",serialize(data));
	}

	public void function testStructEchoStructStructStructArray() localMode="modern" {
		data=sctWS.echoStructStructStructArray([[[{'a':1},{'b':2}]]]);
		assertEquals("[[[{'a':1},{'b':2}]]]",serialize(data));
	}


/** ARRAY **/
	public void function testArrayEchoAny() localMode="modern" {
		data=arrWS.echoAny([1,2,3]);
		assertEquals("[1,2,3]",serialize(data));
		
		data=arrWS.echoAny([[1,2,3]]);
		assertEquals("[[1,2,3]]",serialize(data));
	}

	public void function testArrayEchoArrayArray() localMode="modern" {
		data=arrWS.echoArrayArray([[1,2,3]]);
		assertEquals("[[1,2,3]]",serialize(data));
	}

	public void function testArrayEchoArrayArrayArrayArray() localMode="modern" {
		data=arrWS.echoArrayArrayArrayArray([[[[1,2,3]]]]);
		assertEquals("[[[[1,2,3]]]]",serialize(data));
	}

/** QUERY **/
	public void function testQueryEchoAny() localMode="modern" {
		data=qryWS.echoAny(query('a':[1,2,3])  );
		assertEquals("query('a':[1,2,3])",serialize(data));
		
		data=qryWS.echoAny([query('a':[1,2,3])]);
		assertEquals("[query('a':[1,2,3])]",serialize(data));
	}

	public void function testQueryEchoQuery() localMode="modern" {
		data=qryWS.echoQuery(query('a':[1,2,3]));
		assertEquals("query('a':[1,2,3])",serialize(data));
	}

	public void function testQueryEchoArray() localMode="modern" {
		data=qryWS.echoArray([query('a':[1,2,3])]);
		assertEquals("[query('a':[1,2,3])]",serialize(data));
	}

	public void function testQueryEchoQueryArray() localMode="modern" {
		data=qryWS.echoQueryArray([query('a':[1,2,3])]);
		assertEquals("[query('a':[1,2,3])]",serialize(data));
	}

	public void function testQueryEchoQueryQueryArray() localMode="modern" {
		data=qryWS.echoQueryQueryArray([[query('a':[1,2,3])]]);
		assertEquals("[[query('a':[1,2,3])]]",serialize(data));
	}

	public void function testQueryEchoQueryQueryQueryArray() localMode="modern" {
		data=qryWS.echoQueryQueryQueryArray([[[query('a':[1,2,3])]]]);
		assertEquals("[[[query('a':[1,2,3])]]]",serialize(data));
	}


/** MYITEM **/
	public void function testMyItemEchoAny() localMode="modern" {
		//systemOutput("+++ echoAny +++",true,true);
		data=myiWS.echoAny(new Jira2615.MyItem("Test:echoAny"));
		assertEquals("Test:echoAny",data.getItemkey());
	}

	public void function testMyItemEchoMyItem() localMode="modern" {
		//systemOutput("+++ echoMyItem +++",true,true);
		data=myiWS.echoMyItem(new Jira2615.MyItem("Test:echoMyItem"));
		assertEquals("Test:echoMyItem",data.getItemkey());
	}

	public void function testMyItemEchoArray() localMode="modern" {
		//dump(myiWS);abort;
		data=myiWS.echoArray([new Jira2615.MyItem("Test")]);

		assertEquals(true,isArray(data));
		data=data[1];
		assertEquals("Test",data.getItemkey());
	}

	public void function testMyItemEchoMyItemArray() localMode="modern" {
		//systemOutput("+++ echoMyItemArray +++",true,true);
		data=myiWS.echoMyItemArray([new Jira2615.MyItem("Test")]);
		assertEquals(true,isArray(data));
		data=data[1];
		assertEquals("Test",data.getItemkey());
	}

	public void function testMyItemEchoMyItemMyItemArray() localMode="modern" {
		data=myiWS.echoMyItemMyItemArray([[new Jira2615.MyItem("Test")]]);
		assertEquals(true,isArray(data));
		data=data[1];
		assertEquals(true,isArray(data));
		data=data[1];
		assertEquals("Test",data.getItemkey());
	}

	public void function testMyItemEchoMyItemMyItemMyItemArray() localMode="modern" {
		//data=myiWS.callMyItemMyItemMyItemArray([[new Jira2615.MyItem("Test")]]);
		
		data=myiWS.callMyItemMyItemMyItemArray([[[new Jira2615.MyItem("Test")]]]);
		
		data=myiWS.echoMyItemMyItemMyItemArray([[[new Jira2615.MyItem("Test")]]]);
		assertEquals(true,isArray(data));
		data=data[1];
		assertEquals(true,isArray(data));
		data=data[1];
		assertEquals(true,isArray(data));
		data=data[1];
		assertEquals("Test",data.getItemkey());
	}





/** Older tests **/
	public void function testReturnStruct() localMode="modern" {
		sct=service.getStruct();
		assertEquals("{'a':1}",serialize(sct));
	}

	public void function testSetStruct() localMode="modern" {
		sct=service.setStruct({a:1});
	}

	public void function testEchoSetStruct() localMode="modern" {
		sct=service.echoStruct({'b':12});
		assertEquals("{'b':12}",serialize(sct));
	}


	public void function testSetMyItem() localMode="modern" {
		service.setMyItem(new Jira2615.MyItem("Susi"));
		service.setMyItem({ "itemkey": "structvalue" });
		service.setMyItem(new Jira2615.MyItem2("Susi"));
		service.setMyItem({ "ITemkey": "structvalue" });
		//service.setMyItem({ "ITemkey": "structvalue" });
		//assertEquals("from:getMyItem",item.getName());
	}

	public void function testReturnMyItem() localMode="modern" {
		item=service.getMyItem();
		assertEquals("from:getMyItem",item.getItemKey());
	}

	public void function testReturnMyItemArray() localMode="modern" {
		items=service.getMyItemArray();
		assertEquals(true,isArray(items));
		assertEquals(2,arrayLen(items));
		assertEquals("1:getmyitemarray",items[1].getItemKey());
		assertEquals("2:getmyitemarray",items[2].getItemKey());
	}

	public void function testReturnAny() localMode="modern" {
		item=service.getAny();
		assertEquals("getany",item.getItemKey());
	}

	public void function testReturnAny2() localMode="modern" {
		//dump(serviceAny);abort;
		item=serviceAny.getAny();
		assertEquals("getany",item.getItemKey());
	}

	public void function testReturnArray() localMode="modern" {
		items=service.getArray();
		assertEquals(true,isArray(items));
		assertEquals(2,arrayLen(items));
		assertEquals("1:getarray",items[1].getItemKey());
		assertEquals("2:getarray",items[2].getItemKey());
	}

	public void function testReturnAnyArray() localMode="modern" {
		items=service.getAnyArray();
		assertEquals(true,isArray(items));
		assertEquals(2,arrayLen(items));
		assertEquals("1:getAnyArray",items[1].getItemKey());
		assertEquals("2:getAnyArray",items[2].getItemKey());
	}

	private void function testReturnComplexObjectArray() localMode="modern" {
		// dump(serviceURL);abort;
		// keyValuePairs = [{ name: "Item One" },{ name: "Item Two" }];
		// keyValuePairs = [new Jira2615.MyItem()];
		try{
		item=service.getAny();
		}
		catch(e){
			dump(e);abort;
		}
		dump(item);abort;
		items=service.getArray();
		dump(items);abort;
		assertEquals(true,isArray(items));
		assertEquals(2,arrayLen(items));
		assertEquals("1:getmyitemarray",items[1].getName());
		assertEquals("2:getmyitemarray",items[2].getName());
	}

	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}

} 
</cfscript>