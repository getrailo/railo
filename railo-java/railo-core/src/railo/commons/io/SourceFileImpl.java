package railo.commons.io;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.SourceFile;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

/**
 * represent a cfml file on the runtime system
 */
public final class SourceFileImpl implements SourceFile {

	private final Resource root;
	private Resource file;
	private String realpath;
	private boolean isOutSide=false;
	private String name;
	private String className;
	private String packageName;
	private String javaName;
    private boolean trusted;
    private boolean hasArchive;
	
	
	/**
	 * constructor of the class
	 * @param root rott directory of the file
	 * @param realPath relative path to root directory
	 */
	public SourceFileImpl(Resource root, String realPath) {
		
		this.root=root;
		
		this.realpath=realPath.replace('\\','/');
		
		if(realPath.indexOf('/')==0) {
			
		}
		else if(realPath.startsWith("../")) {
			isOutSide=true;
		}
		else if(realPath.startsWith("./")) {
			this.realpath=this.realpath.substring(1);
		}
		else {
			this.realpath="/"+this.realpath;
		}
	}
	
	/**
	 * constructor of the class
	 * @param parent parent sourceFile
	 * @param realPath relapath to parent sourc file (not root)
	 */
	public SourceFileImpl(SourceFileImpl parent, String realPath) {
		realpath=realPath.replace('\\','/');
		if(realPath.equals(".") || realPath.equals(".."))realPath+="/";
		this.root=parent.root;

		name=parent.name;
		trusted=parent.trusted;
		hasArchive=parent.hasArchive;
		isOutSide=parent.isOutSide;

		if(realPath.indexOf('/')==0) {
			isOutSide=false;
			this.realpath=realPath;
		}
		else if(realPath.indexOf("./")==0) {
			this.realpath=mergeRealPathes(parent.getRealpath(), realPath.substring(2));
		}
		else {
			this.realpath=mergeRealPathes(parent.getRealpath(), realPath);
		}
	}
	
	/**
	 * merge to realpath to one
	 * @param parentRealPath 
	 * @param newRealPath
	 * @return merged realpath
	 */
	private String mergeRealPathes(String parentRealPath, String newRealPath) {
		// remove file from parent
		parentRealPath=pathRemoveLast(parentRealPath);
		
		while(newRealPath.startsWith("../")) {
			parentRealPath=pathRemoveLast(parentRealPath);
			newRealPath=newRealPath.substring(3);
		}
		// check if come back
		String path=parentRealPath+"/"+newRealPath;
		
		if(path.startsWith("../")) {
			int count=0;
			while(path.startsWith("../")) {
				count++;
				path=path.substring(3);
			}
			
			String strRoot=root.getAbsolutePath().replace('\\','/');
			if(strRoot.lastIndexOf('/')!=strRoot.length()-1) {
				strRoot+='/';
			}
			int rootLen=strRoot.length();
			String[] arr=path.split("/");
			for(int i=count;i>0;i--) {
				if(arr.length>i) {
					String tmp="/"+list(arr,0,i);
					if(strRoot.lastIndexOf(tmp)==rootLen-tmp.length()) {
						StringBuilder rtn=new StringBuilder();
						while(i<count-i) {
							count--;
							rtn.append("../");
						}
						isOutSide=rtn.length()!=0;
						return rtn.toString()+(rtn.length()==0?"/":"")+list(arr,i,arr.length);
					}
				}
			}
			//System.out.println(strRoot+" - "+path);
		}
		
		return parentRealPath+"/"+newRealPath;
	}

	/**
	 * convert a String array to a string list, but only part of it 
	 * @param arr String Array
	 * @param from start from here
	 * @param len how many element
	 * @return String list
	 */
	private String list(String[] arr,int from, int len) {
		StringBuilder sb=new StringBuilder();
		for(int i=from;i<len;i++) {
			sb.append(arr[i]);
			if(i+1!=arr.length)sb.append('/');
		}
		return sb.toString();
	}

	
	
	/**
	 * remove the last elemtn of a path
	 * @param path path to remove last element from it
	 * @return path with removed element
	 */
	private String pathRemoveLast(String path) {
		if(path.length()==0) {
			isOutSide=true;
			return "..";
		}
		else if(path.lastIndexOf("..")==path.length()-2){
			isOutSide=true;
			return path+"/..";
		}
		return path.substring(0,path.lastIndexOf('/'));
	}
	
	public Resource getFile() {
		return getResource();
	}
	
	@Override
	public Resource getResource() {
		if(file==null) {
			if(isOutSide) {
				try {
					file=root.getRealResource(realpath).getCanonicalResource();
				} catch (IOException e) {
				}
			}
			file=root.getRealResource(realpath);
		}
		return file;
	}
	
	/**
	 * @return Returns the realpath.
	 */
	public String getRealpath() {
		return realpath;
	}
	/**
	 * @return Returns the root.
	 */
	public Resource getRoot() {
		return root;
	}
	
	/**
	 * @return returns a variable string based on realpath and return it
	 */
	public String getRealPathAsVariableString() {
		return StringUtil.toIdentityVariableName(getRealpath());
		//return StringUtil.toClassName(getRealpath());
	}
	
	/**
	 * @return returns the a classname matching to filename
	 */
	public String getClassName() {
		if(className==null) createClassAndPackage();
		return className;
	}
	
	/**
	 * @return returns the java name
	 */
	public String getJavaName() {
		if(javaName==null) createClassAndPackage();
		return javaName;
	}
	
	/**
	 * @return returns the a package matching to file
	 */
	public String getPackageName() {
		if(packageName==null) createClassAndPackage();
		return packageName;
	}
	
	private void createClassAndPackage() {
		String str=realpath;
		StringBuilder packageName=new StringBuilder();
		StringBuilder javaName=new StringBuilder();
		while(str.indexOf('/')==0)str=str.substring(1);
		while(str.lastIndexOf('/')==str.length()-1)str=str.substring(0,str.length()-1);
		
		//String[] arr=str.split("/");
		Array arr = ListUtil.listToArray(str, '/');
		int len=arr.size();
		String value;
		for(int i=1;i<=len;i++) {
			value=(String) arr.get(i,"");
			String varName=StringUtil.toVariableName(value);
			javaName.append("/"+varName);
			if(i==len) {
				className=varName.toLowerCase();
			}
			else {
				if(i!=0) packageName.append('.');
				packageName.append(varName);
			}
		}
		this.packageName=packageName.toString().toLowerCase();
		this.javaName=javaName.toString().toLowerCase();
	}
	
	

	/**
	 * @return returns a variable string based on root and return it
	 */
	public String getRootPathAsVariableString() {
		return StringUtil.toIdentityVariableName(root.getAbsolutePath());
	}
	
    /**
     * @return has context a archive or not
     */
    public boolean hasArchive() {
        return hasArchive;
    }
    
    /**
     * @return returns if is trusted or not
     */
    public boolean isTrusted() {
        return trusted;
    }

    @Override
    public Resource getPhyscalFile() {
        return getFile();
    }

    @Override
    public String getDisplayPath() {
        return getFile().getAbsolutePath();
    }

	@Override
	public String getFullClassName() {
		String p=getPackageName();
		if(p.length()==0) return getClassName();
		return p.concat(".").concat(getClassName());
	}
}