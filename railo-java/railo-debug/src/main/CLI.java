package main;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import railo.print;

public class CLI {
	public static void main(String[] args) throws Throwable {
		args=new String[]{
				"-jar", "lib/ext/railo-cli.jar", "-webroot=./web", "uri=index.cfm?param1=5&param2=susi"
		};
		
		
		File libDir=new File("./").getCanonicalFile();
		System.out.println(libDir);
		
		long start=System.currentTimeMillis();
		
		// Fix for tomcat
        if(libDir.getName().equals(".") || libDir.getName().equals(".."))
        	libDir=libDir.getParentFile();

        //if(libDir.toString().equals("/Users/mic/Projects/Railo/Source2/railo/railo-java/railo-loader"))
		//	libDir=new File("/Users/mic/temp/ext");

        //if(libDir.toString().equals("/Users/mic/Projects/Railo/Source2/Railo-CLI"))
		//	libDir=new File("/Users/mic/temp/ext");
        
        boolean isDebugMode=isDebugMode(args);
        
        if(isDebugMode)System.out.println("Loading Jars from "+libDir);
        List<URL> urls=new ArrayList<URL>();
        loadJars(libDir,urls,isDebugMode);
        if(isDebugMode)System.out.println();
        
        
        
        
        URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[urls.size()]),ClassLoader.getSystemClassLoader());
        Class cli = cl.loadClass("railo.cli.CLI");
        Method main = cli.getMethod("main",new Class[]{String[].class});
        main.invoke(null, new Object[]{args});
	}
	

	private static boolean isDebugMode(String[] args) {
		String arg;
		for(int i=0;i<args.length;i++){
			arg=args[i];
			if(arg!=null && (arg.equalsIgnoreCase("debug") || arg.equalsIgnoreCase("-debug")))
				return true;
		}
		return false;
	}


	private static void loadJars(File libDir, List<URL> urls, boolean isDebugMode) throws MalformedURLException {
		
        File[] children = libDir.listFiles(new ExtFilter());
        URL url;
        for(int i=0;i<children.length;i++){
        	if(children[i].isDirectory()) loadJars(children[i], urls,isDebugMode);
        	else {
        		urls.add(url=children[i].toURI().toURL());
            	if(isDebugMode)System.out.println("- "+url);
        	}
        }
        
	}


	public static class ExtFilter implements FileFilter {
		
		private String ext=".jar";
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(ext);
		}

	}
}
