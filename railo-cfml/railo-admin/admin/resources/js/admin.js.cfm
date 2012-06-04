<cfsetting showdebugoutput="no">
<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- create a string to be used as an Etag - in the response header --->
	<cfset filepath = getCurrentTemplatePath() />
	<cfset lastModified = application.oHTTPCaching.getFileDateLastModified(filepath) />
	<cfset etag = lastModified & '-' & hash(filepath) />
	<cfset mimetype = "text/javascript" />
	
	<!--- check if the content was cached on the browser, and set the ETag header.
	No expires header is set, because this file might get updated after a Railo update. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype)>
		<cfexit method="exittemplate" />
	</cfif>
	
	<!--- file was not cached; send the data --->
	<cfcontent reset="yes" type="#mimetype#" />
	
	<!--- PK: this tag is here, so my editor color-codes the content underneath. (it won't get outputted) --->
	<script type="text/ecmascript">
//</cfsilent>
/* init functions */
$(function(){
	scrollToEl('div.error,div.warning');
});

function scrollToEl(selector)
{
	var intoview = $(selector).eq(0).css('opacity',.1);
	if (intoview.length)
	{
		$('html,body').animate({scrollTop: intoview.offset().top-50}, 500, function(){ intoview.animate({opacity:1}, 1000) });
	}
}

function initMenu() {
	$('#menu ul').show();
	$('#menu li a').click(
		function() {
			$(this).next().slideToggle('normal');
		}
	);
}

function initMenu2() {
  $('#menu ul').hide();
  $('#menu ul:first').show();
  $('#menu li a').click(
	function() {
	  var checkElement = $(this).next();
	  if((checkElement.is('ul')) && (checkElement.is(':visible'))) {
		return false;
		}
	  if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
		$('#menu ul:visible').slideUp('normal');
		checkElement.slideDown('normal');
		return false;
		}
	  }
	);
}

var disableBlockUI=false;
// {form:_form,name:_input.name,value:v,error:err.error};
function customError(errors){ 
	if(!errors || errors.length==0) return;
	var err;
	var form=errors[0].form;
	var el;
	var clazz;
	var input;

	// remove error from last round
	try{
		for(var i=0;i<form.elements.length;i++){
			input=form.elements[i];
			el=$(input);
			clazz=el.attr("class");
			if(clazz && clazz=="InputError") {
				el.removeClass();
				el=$("#msg_"+input.name);
				el.remove();
			}
		}
	}
	catch(err){
		alert(err)
	}

	// create new error
	for(var i=0;i<errors.length;i++){
		err=errors[i];
		var input=form[err.name];
		var _input=$(input);
		if(i==0) _input.focus();
		_input.addClass("InputError");
		_input.after('<span id="msg_'+err.name+'" class="commentError"><br/>'+err.error+'</span>');
	}
	disableBlockUI=true;
}

function createWaitBlockUI(msg){
  var _blockUI=function() { 
	  if(!disableBlockUI)
	  $.blockUI(
		{ 
		  message:msg,
		  css: { 
			  border: 'none', 
			  padding: '15px', 
			  backgroundColor: '#000', 
			  '-webkit-border-radius': '10px', 
			  '-moz-border-radius': '10px', 
			  opacity: .5, 
			  color: '#fff' ,
			  fontSize : "18pt"
			},
		  fadeIn: 1000 
		}
	  ); 
	}
  return _blockUI;
}

function selectAll(field) {
	var form=field.form;
	for(var key in form.elements){
		if(form.elements[key] && (""+form.elements[key].name).indexOf("row_")==0){
			form.elements[key].checked=field.checked;
		}
	}
}

function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}

function enableBtnsWhenChecked(btns, checkboxes)
{
	checkboxes.change(function(){
		var chkd = checkboxes.filter(':checked').length > 0;
		btns.prop('disabled', chkd ? '':'disabled').css('opacity', (chkd ? 1:.5));
	})
	.filter(':first').triggerHandler('change');
}
