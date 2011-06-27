package railo.cli;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class MainEntryPoint {

	public static void main(String[] args) throws Throwable {
		File libDir=new File("./").getCanonicalFile();
		System.out.println(libDir);
		
		// Fix for tomcat
        if(libDir.getName().equals(".") || libDir.getName().equals(".."))
        	libDir=libDir.getParentFile();
		
        if(libDir.toString().equals("/Users/mic/Projects/Railo/Source2/railo/railo-java/railo-loader"))
			libDir=new File("/Users/mic/temp/ext");
        
        File[] children = libDir.listFiles(new ExtFilter());
        if(children.length<2) {
        	libDir=new File(libDir,"lib");
        	 children = libDir.listFiles(new ExtFilter());
        }
        
        URL[] urls = new URL[children.length];
        System.out.println("Loading Jars");
        for(int i=0;i<children.length;i++){
        	urls[i]=new URL ("jar:file://" + children[i] + "!/");
        	System.out.println("- "+urls[i]);
        }
        System.out.println();
        URLClassLoader cl = new URLClassLoader(urls,ClassLoader.getSystemClassLoader());
        Class cli = cl.loadClass("railo.cli.CLI");
        Method main = cli.getMethod("main",new Class[]{String[].class});
        main.invoke(null, new Object[]{args});
        
        
	}
	

	public static class ExtFilter implements FilenameFilter {
		
		private String ext=".jar";
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(ext);
		}

	}
}
