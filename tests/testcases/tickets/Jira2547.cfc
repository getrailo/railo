

component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		
	}
	
	
	public void function test(){
	}

	
	public void function testThreads(){
	
		thread name="l1" {
			thread name="l2" {
				thread name="l3" {
					thread name="l4" {
						thread name="l5" {
						
						}
						thread action="join" names="l5";
					}
					thread action="join" names="l4";
				}
				thread action="join" names="l3";
			}
			thread action="join" names="l2";
		}
		thread action="join" names="l1";
		//dump(cfthread)
		//abort;
		assertEquals("COMPLETED",l1.STATUS);
		
		
		
	}
	
} 