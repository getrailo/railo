<cfsetting showdebugoutput="false"><cfscript>
url.number=1; // avoid exception thrown by param itself 
param numeric url.number;  
param url.number; 
param numberx=36; 
param url.numbery=45; 
param url.numbery= 45; 
param url.numbery =45; 
param url.numbery = 45; 
param name="url.number";
param name="url.number" type="numeric";

echo(numberx);
echo(numbery);
</cfscript>