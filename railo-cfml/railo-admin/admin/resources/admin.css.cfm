<cfoutput>
<!--- 	<cfset yellowColor=iif(request.adminType EQ "web",de('FFFF66'),de('FFFF66'))> --->
<style>
			
	body {background-image:url(<cfmodule type="css" template="img.cfm" src="#ad#-back.png" />);background-repeat:repeat-x;background-color:##f7f7f7;margin-top:0px;margin-left:0px;}
	body, tr, td,div {font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 9pt;color:##3c3e40;}
	h1 {font-weight:normal;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 20pt;color:##568bc1;}
	h2 {height:6pt;font-size : 14pt;font-weight:normal;color:##568bc1;}
	
	div.navtop		{margin-top:8px;margin-bottom:3px;color:##333333;font-weight:bold;font-size : 9pt;}
	  	a.navtop		{text-decoration:none;font-weight:bold;font-size : 9pt;}
	
	  	a.navsub		{text-decoration:none;color:##568bc1;font-size : 8pt;}
	  	a.navsub_active	{text-decoration:none;color:##568bc1;font-size : 8pt;font-weight:bold;}
	
	.comment{font-size : 10px;color:##787a7d;}
	.commentHead{font-size : 10px;color:##DFE9F6;}
	.copy { font-size : 8pt;color:##666666;}
	
	div.hr{border-color:red;border-style:solid;border-color:##e0e0e0;border-width:0px 0px 1px 0px;margin:0px 16px 4px 0px;}
	.tbl{empty-cells:show;}
	.tblHead{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##f2f2f2;color:##3c3e40}
	.tblContent			{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;}
	.tblContentRed		{padding-left:5px;padding-right:5px;border:1px solid ##cc0000;background-color:##f9e0e0;}
	.tblContentGreen	{padding-left:5px;padding-right:5px;border:1px solid ##009933;background-color:##e0f3e6;}
	.tblContentYellow	{padding-left:5px;padding-right:5px;border:1px solid ##ccad00;background-color:##fff9da;}
	
	td.inactivTab{border-style:solid;border-color:##e0e0e0;padding: 0px 5px 0px 5px;background-color:white;}
	a.inactivTab{color:##3c3e40;text-decoration:none;}
	
	td.activTab{border-style:solid;border-color:##e0e0e0;border-width:1px 1px 0px 1px ;padding: 2px 10px 2px 10px;background-color:##e0e0e0;}
	a.activTab{font-weight:bold;color:##3c3e40;text-decoration:none;}
	
	td.tab {border-color:##e0e0e0;border-width:1px;border-style:solid;border-top:0px;padding:10px;background-color:white;}
	td.tabtop {border-style:solid;border-color:##e0e0e0;border-width:0px 0px 1px 0px ;padding: 0px 1px 0px 0px;}
	
	
	.CheckOk{font-weight:bold;color:##009933;font-size : 12px;}
	.CheckError{font-weight:bold;color:##cc0000;font-size : 12px;}
	
	input{
		background: url(<cfmodule type="css" template="img.cfm" src="input-shadow.png" />) repeat-x 0 0;background-color:white;
		padding-left:3px;padding-right:2px;padding-top:3px;padding-bottom:3px;margin:3px 1px 3px 1px;color:##3c3e40;border-style:solid;border-width:1px;border-color:##e0e0e0;}
	
	.button,.submit,.reset {
		background: url(<cfmodule type="css" template="img.cfm" src="input-button.png" />) repeat-x 0 0;
		background-color:##f2f2f2;color:##3c3e40;font-weight:bold;padding-left:10px;padding-right:10px;margin:0px;}
	
	select {font-size : 11px;color:##3c3e40;margin:3px 0px 3px 0px;}
	.checkbox,.radio {border:0px;}
	
	a{color:##568bc1;}
	
	<!---/*
	.darker{background-color:##e0e0e0;}
	.brigther{background-color:###bgBrightColor#;}
	
	*/--->
	
	##mask { position:absolute; left:0; top:0; z-index:9000; background-color:##000; display:none; }
	##boxes .window { position:absolute; left:0; top:0; width:440px; height:200px; display:none; z-index:9999; padding:40px; }
	##boxes ##movie { width:720px; height:450px; padding:10px; border:0px solid ##666666; }
	##boxes ##global { width:720px; height:526px; padding:10px; border:0px solid ##666666;}
	##boxes ##localHelp { width:720px; height:526px; padding:10px; border:0px solid ##666666;}

	##boxes ##movie ##innerHelp { width:680px; height:400px; background-color:##ffffff; padding: 10 10 10 20; border:0px solid red; overflow:auto;}
	##boxes ##global ##innerHelp { width:680px; height:476px; background-color:##ffffff; padding: 10 10 10 20; border:0px solid red; overflow:auto;}
	##boxes ##localHelp ##innerHelp { width:680px; height:476px; background-color:##ffffff; padding: 10 10 10 20; border:0px solid red; overflow:auto;}

	##boxes .headline { float:left;margin-left:-10px;width:670px;border-bottom:1px solid ##666666; }
	##boxes .headline_2 {margin-top:5px;padding-left:30px}
	
</style>
</cfoutput>