package railo.runtime.schedule;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

import railo.commons.io.IOUtil;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.HttpClientUtil;
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
        HttpClient client = new HttpClient();
        client.setStrictMode(false);
        HttpState state = client.getState();
        
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
        
        HttpMethod method = new GetMethod(url);
        HostConfiguration hostConfiguration = client.getHostConfiguration();

		method.setRequestHeader("User-Agent","CFSCHEDULE");
        
        
       // Userame / Password
        Credentials credentials = task.getCredentials();
        if(credentials!=null) {
            state.setCredentials(null,null,credentials);
            method.setDoAuthentication( true );
            //get.addRequestHeader("Authorization","Basic admin:spwwn1p");
        }
        
        // Proxy
        String proxyHost = task.getProxyHost();
        int proxyPort = task.getProxyPort();
        Credentials proxyCredentials =task.getProxyCredentials();
        
        if(StringUtil.isEmpty(proxyHost) && config.isProxyEnableFor(task.getUrl().getHost())) {
        	proxyHost=config.getProxyServer();
        	proxyPort=config.getProxyPort();
        	
        	if(!StringUtil.isEmpty(config.getProxyUsername())) {
        		proxyCredentials=new UsernamePasswordCredentials(
        				config.getProxyUsername(),
        				config.getProxyPassword());
        	}
        	else proxyCredentials=null;
        }
        
        if(!StringUtil.isEmpty(proxyHost)) {
            if(proxyPort>0)hostConfiguration.setProxy(proxyHost,proxyPort);
            else hostConfiguration.setProxy(proxyHost,80);
            //Credentials proxyCredentials =task.getProxyCredentials();
            if(proxyCredentials!=null) {
                state.setProxyCredentials(null,null,credentials);
            }
        }        
        if(task.getTimeout()>0)
        	//client.setConnectionTimeout((int)task.getTimeout());
        	client.getParams().setConnectionManagerTimeout(task.getTimeout());
        
        
        // execute
        try {
            method=HttpClientUtil.execute(client,method,true);
        } catch (Exception e) {
            if(log!=null)log.error(logName,e.getMessage());
            hasError=true;
        }
        
        // write file
        Resource file = task.getResource();
        if(!hasError && file!=null && task.isPublish()) {
	        if(isText(method) && task.isResolveURL()) {
	        	
        	    String str;
                try {
                    InputStream stream = method.getResponseBodyAsStream();
                    str = stream==null?"":IOUtil.toString(stream,null);
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
                            method.getResponseBodyAsStream(),
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
	
    private static boolean isText(HttpMethod get) {
        Header header = get.getResponseHeader("Content-Type");
        if(header==null)return true;
        String mimetype = header.getValue();
        return
        	mimetype == null ||  mimetype.startsWith("text") || mimetype.startsWith("application/octet-stream");
        
    }
	
}
