<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testarrayDelete(){
		var arr=[1,2,3,4];
		assertEquals("1,2,3,4",arrayToList(arr));
		arrayDelete(arr,2)
		assertEquals("1,3,4",arrayToList(arr));
		assertEquals(,);
		assertEquals(,);
		assertEquals(,);
		assertEquals(,);
		assertEquals(,);

<cf_valueEquals left="##" right=>
<cfset arrayDelete(arr,1)>
<cf_valueEquals left="#arrayToList(arr)#" right="3,4">

<cf_valueEquals left="#arrayDelete(arr,1)#" right="#false#">
<cf_valueEquals left="#arrayDelete(arr,3)#" right="#true#">

<cfset arr=['SUSI']>
<cf_valueEquals left="#arrayToList(arr)#" right="SUSI">
<cfset arrayDelete(arr,'SUSI')>
<cf_valueEquals left="#arrayToList(arr)#" right="">

<cfset arr=[1,1,1]>
<cf_valueEquals left="#arrayToList(arr)#" right="1,1,1">
<cfset arrayDelete(arr,1)>
<cf_valueEquals left="#arrayToList(arr)#" right="1,1">


<cfset arr=['SUSI','susi']>
<cf_valueEquals left="#arrayToList(arr)#" right="SUSI,susi">
<cfset arrayDelete(arr,'SUSI')>
<cf_valueEquals left="#arrayToList(arr)#" right="susi">

<cfset n=now()>
<cfset arr=[now()]>
<cfset arrayDelete(arr,n)>
<cf_valueEquals left="#arrayToList(arr)#" right="">
		
		
		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
} 
</cfscript>