package railo.commons.io.res.type.ftp;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import railo.commons.collection.MapFactory;
import railo.commons.lang.SerializableObject;
import railo.commons.lang.StringUtil;

public final class FTPResourceClient extends FTPClient {

	private String workingDirectory=null;
	
	
	private final FTPConnectionData ftpConnectionData;
	private long lastAccess;
	private final Object token=new SerializableObject();
	private final Map<String,FTPFileWrap> files=MapFactory.<String,FTPFileWrap>getConcurrentMap();
	private final int cacheTimeout;

	public FTPResourceClient(FTPConnectionData ftpConnectionData,int cacheTimeout) {
		this.ftpConnectionData=ftpConnectionData;
		this.cacheTimeout=cacheTimeout;
	}

	/**
	 * @return the ftpConnectionData
	 */
	public FTPConnectionData getFtpConnectionData() {
		return ftpConnectionData;
	}

	public void touch() {
		this.lastAccess=System.currentTimeMillis();
	}

	/**
	 * @return the lastAccess
	 */
	public long getLastAccess() {
		return lastAccess;
	}

	public Object getToken() {
		return token;
	}
	
	@Override
	public boolean changeWorkingDirectory(String pathname) throws IOException {
		if(StringUtil.endsWith(pathname,'/') && pathname.length()!=1)pathname=pathname.substring(0,pathname.length()-1);
		
		if(pathname.equals(workingDirectory)) return true;
		workingDirectory=pathname;
		return super.changeWorkingDirectory(pathname);
	}

	public String id() {
		return ftpConnectionData.key();
	}

	@Override
	public boolean equals(Object obj) {
		
		return ((FTPResourceClient)obj).id().equals(id());
	}

	public FTPFile getFTPFile(FTPResource res) throws IOException {
		String path=res.getInnerPath();
		FTPFileWrap fw = files.get(path);
		
		if(fw==null) {
			return createFTPFile(res);
		}
		if(fw.time+cacheTimeout<System.currentTimeMillis()) {
			files.remove(path);
			return createFTPFile(res);
		}
		return fw.file;
	}
	public void registerFTPFile(FTPResource res,FTPFile file) {
		files.put(res.getInnerPath(),new FTPFileWrap(file));	
	}

	public void unregisterFTPFile(FTPResource res) {
		files.remove(res.getInnerPath());
	}
	
	
	private FTPFile createFTPFile(FTPResource res) throws IOException {
		FTPFile[] children=null;
		boolean isRoot=res.isRoot();
		String path=isRoot?res.getInnerPath():res.getInnerParent();
		
		synchronized(getToken()){ 
			changeWorkingDirectory(path);
			children = listFiles();
		}
		
		if(children.length>0) {
			for(int i=0;i<children.length;i++) {
				if(isRoot){
					if(children[i].getName().equals(".")) {
						registerFTPFile(res, children[i]);
						return children[i];
					}
				}
				else{
					if(children[i].getName().equals(res.getName())) {
						registerFTPFile(res, children[i]);
						return children[i];
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean deleteFile(String pathname) throws IOException {
		files.remove(pathname);
		return super.deleteFile(pathname);
	}

	class FTPFileWrap {

		private FTPFile file;
		private long time;

		public FTPFileWrap(FTPFile file) {
			this.file=file;
			this.time=System.currentTimeMillis();
		}
		
	}
}
