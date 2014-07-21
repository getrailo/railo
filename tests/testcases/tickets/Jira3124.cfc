<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	variables.procName="SP3124";
	variables.delay=2;
	variables.delayMS=variables.delay*1000;
	variables.token=createUniqueId();

	public function beforeTests(){
		try{
			query  datasource="test_ms" {
				echo("drop PROC "&variables.procName);
			}
		}
		catch(local.e){}
		

//GRANT EXECUTE ON OBJECT::_test_QueryCache TO [Username]
		
		query  datasource="test_ms" {
			echo("CREATE PROC "&variables.procName&" AS ");
			echo("WAITFOR DELAY '00:00:#variables.delay#'; SELECT 1 as a"); //AdCount = COUNT(*) FROM dbo.[MyTable]
		}
		 /// fill the cache
		_testQuery(true);
		_testProc(true);
		
	}

	public void function testQueryNoCachedWithin() {
		local.start=getTickCount();
		qry=_testQuery(false);
		assertTrue((getTickCount()-local.start)>=variables.delayMS);
	}

	public void function testQueryCachedWithin() {
		local.start=getTickCount();
		qry=_testQuery(true);
        assertTrue((getTickCount()-local.start)<variables.delayMS);
	}


	public void function testProcNoCachedWithin() {
		local.start=getTickCount();
		qry=_testProc(false);
		assertTrue((getTickCount()-local.start)>=variables.delayMS);
	}

	public void function testProcCachedWithin() {
		local.start=getTickCount();
		qry=_testProc(true);
        assertTrue((getTickCount()-local.start)<variables.delayMS);
	}




	private query function _testQuery(boolean cachedWithin=false) {
		local.sql="WAITFOR DELAY '00:00:#variables.delay#'; SELECT '#variables.token#' as a";
		if(cachedWithin) {
			query cachedWithin="#createTimespan(0,0,0,10)#" datasource="test_ms" name="local.qry" {
				echo(sql);
			}
		}
        else {
			query datasource="test_ms" name="local.qry" {
				echo(sql);
			}
		}
        return qry;
	}

	private query function _testProc(boolean cachedWithin=false) {
		local.sql="WAITFOR DELAY '00:00:#variables.delay#'; SELECT '#variables.token#' as a";
		if(cachedWithin) {
			storedproc datasource="test_ms" procedure="#variables.procName#" cachedwithin="#createTimeSpan( 0, 0, 0, 5 )#" {
		        procresult name="local.qry" resultset="1";
			}
		}
        else {
			storedproc datasource="test_ms" procedure="#variables.procName#" {
		        procresult name="local.qry" resultset="1";
			}
		}
        return qry;
	}

} 
</cfscript>