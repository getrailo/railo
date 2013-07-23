package railo.runtime.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import railo.commons.io.IOUtil;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.res.ContentType;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.commons.net.http.HTTPEngine;
import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.Header;
import railo.commons.security.Credentials;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.functions.other.CreateUUID;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.util.URLResolver;

class ExecutionThread extends Thread {

	private Config config;
	private LogAndSource log;
	private ScheduleTask task;
	private String charset;

	public ExecutionThread(Config config, LogAndSource log, ScheduleTask task, String charset) {
		this.config=config;
		this.log=log;
		this.task=task;
		this.charset=charset;
	}

	public void run() {
		execute(config, log, task, charset);
	}
	public static void execute(Config config, LogAndSource log, ScheduleTask task, String charset) {
		boolean hasError=false;
        String logName="schedule task:"+task.getTask();
       // init
        //HttpClient client = new HttpClient();
        //client.setStrictMode(false);
        //HttpState state = client.getState();
        
        String url;
        if(task.getUrl().getQuery()==null)
        	url=task.getUrl().toExternalForm()+"?RequestTimeout="+(task.getTimeout()/1000);
        else if(StringUtil.isEmpty(task.getUrl().getQuery()))
        	url=task.getUrl().toExternalForm()+"RequestTimeout="+(task.getTimeout()/1000);
        else {
        	if(StringUtil.indexOfIgnoreCase(task.getUrl().getQuery()+"", "RequestTimeout")!=-1)
        		url=task.getUrl().toExternalForm();
        	else
        		url=task.getUrl().toExternalForm()+"&RequestTimeout="+(task.getTimeout()/1000);
        }
        
        //HttpMethod method = new GetMethod(url);
        //HostConfiguration hostConfiguration = client.getHostConfiguration();
        
        Header[] headers=new Header[]{
        	HTTPEngine.header("User-Agent","CFSCHEDULE")
        };
		//method.setRequestHeader("User-Agent","CFSCHEDULE");
        
       // Userame / Password
        Credentials credentials = task.getCredentials();
        String user=null,pass=null;
        if(credentials!=null) {
        	user=credentials.getUsername();
        	pass=credentials.getPassword();
            //get.addRequestHeader("Authorization","Basic admin:spwwn1p");
        }
        
        // Proxy
        ProxyData proxy=task.getProxyData();
        if(!ProxyDataImpl.isValid(proxy) && config.isProxyEnableFor(task.getUrl().getHost())) {
        	proxy=config.getProxyData();
        }
        
        HTTPResponse rsp=null;
        
        // execute
        try {
        	rsp = HTTPEngine.get(new URL(url), user, pass, task.getTimeout(),HTTPEngine.MAX_REDIRECT, charset, null, proxy, headers);
        } catch (Exception e) {
            if(log!=null)log.error(logName,e.getMessage());
            hasError=true;
        }
        
        // write file
        Resource file = task.getResource();
        if(!hasError && file!=null && task.isPublish()) {
        	String n=file.getName();
        	if(n.indexOf("{id}")!=-1){
        		n=StringUtil.replace(n, "{id}",CreateUUID.invoke(), false);	
        		file=file.getParentResource().getRealResource(n);
        	}
        	
	        if(isText(rsp) && task.isResolveURL()) {
	        	
        	    String str;
                try {
                    InputStream stream = rsp.getContentAsStream();
                    str = stream==null?"":IOUtil.toString(stream,(Charset)null);
                    if(str==null)str="";
                } 
                catch (IOException e) {
                	str=e.getMessage();
                }
        	    
        	    try {
                    str=new URLResolver().transform(str,task.getUrl(),false);
                } catch (PageException e) {
                    if(log!=null)log.error(logName,e.getMessage());
                    hasError=true;
                }
        	    try {
                    IOUtil.write(file,str,charset,false);
                } 
                catch (IOException e) {
                    if(log!=null)log.error(logName,e.getMessage());
                    hasError=true;
                }
	        }
	        else {
	        	//print.out("1111111111111111111111111111111");
	            try {
                    IOUtil.copy(
                            rsp.getContentAsStream(),
                            file,
                            true
                    );
                    //new File(file.getAbsolutePath()).write(method.getResponseBodyAsStream());
                } 
                catch (IOException e) {
                    if(log!=null)log.error(logName,e.getMessage());
                    hasError=true;
                }
	        }
        }
        if(!hasError && log!=null)log.info(logName,"executed");
	}
	
    private static boolean isText(HTTPResponse rsp) {
    	ContentType ct = rsp.getContentType();
        if(ct==null)return true;
        String mimetype = ct.getMimeType();
        return
        	mimetype == null ||  mimetype.startsWith("text") || mimetype.startsWith("application/octet-stream");
        
    }
	
}
