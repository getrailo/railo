/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
