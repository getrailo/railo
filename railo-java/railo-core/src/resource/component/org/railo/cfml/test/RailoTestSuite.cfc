<cfscript>
component extends="mxunit.framework.TestSuite" {
	/**
	* adding n testcases by defining a package that hold testcases 
	* for example: "org.railo.cfml.test"
	* @packageName name of the package that holds testcases
	*/
	public void function addPackage(required string packageName){
		var cfcs=getTestcasesFromPackage(packageName,true);
		loop collection="#cfcs#" index="local.name" item="local.cfc" {
			addAll(name, cfc);
        }
	}
	
	private struct function getTestcasesFromPackage(required string packageName, boolean loaded=true){
		var results={};
        var names=componentListPackage(packageName);
		var cfc='';
		loop array="#names#" item="name" {
			// check if it is a Testcase
			cfc=createObject('component',packageName&"."&name);
			if(isTestCase(cfc)) {
                if(loaded) results[packageName&"."&name]=cfc;
				//if(loaded) results[packageName&"."&name&"("&meta.path&")"]=cfc;
				else results[packageName&"."&	name]=packageName&"."&name;
            }
        }
		return results;
    }
    
    private boolean function isTestCase(required component cfc) {
    	return isInstanceof(cfc,'mxunit.framework.TestCase');
    }

}
</cfscript>