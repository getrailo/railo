<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		short=queryNew("a,b");
		qry=queryNew("a,b");
		for(i=1;i<=35;i++){
			row=queryAddrow(qry);
			querySetcell(qry,'a',"a"&i,row);
			querySetcell(qry,'b',"b"&i,row);
		}	
	}
	
	
	public void function testValidRanges(){
		//writedump(qry);
		sct=QueryConvertForGrid(qry,1,10);
		assertEquals('a1,a2,a3,a4,a5,a6,a7,a8,a9,a10',valueList(sct.query.a));
		sct=QueryConvertForGrid(qry,2,10);
		assertEquals('a11,a12,a13,a14,a15,a16,a17,a18,a19,a20',valueList(sct.query.a));
		sct=QueryConvertForGrid(qry,3,10);
		assertEquals('a21,a22,a23,a24,a25,a26,a27,a28,a29,a30',valueList(sct.query.a));
		sct=QueryConvertForGrid(qry,4,10);
		assertEquals('a31,a32,a33,a34,a35',valueList(sct.query.a));
	}
	
	
	public void function testValidRanges(){
		
		sct=QueryConvertForGrid(qry,1,-10);
		assertEquals('',valueList(sct.query.a));
		
		sct=QueryConvertForGrid(qry,5,10);
		assertEquals('',valueList(sct.query.a));
	}
} 
</cfscript>