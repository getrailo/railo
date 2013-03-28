package railo.runtime.security;

import java.io.IOException;
import java.util.Set;

import railo.commons.digest.MD5;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.coder.Base64Coder;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

/**
 * User Password Information
 */
public final class CredentialImpl implements Credential {
    String username;
    String password;
    String[] roles;
	private Resource rolesDir;
    private static final char ONE=(char)1;

    /**
     * credential constructor
     * @param username
     */
    public CredentialImpl(String username,Resource rolesDir) {
        this(username,null,new String[0],rolesDir);
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     */
    public CredentialImpl(String username,String password,Resource rolesDir) {
        this(username,password,new String[0],rolesDir);
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     * @param roles
     * @throws PageException
     */
    public CredentialImpl(String username,String password, String roles,Resource rolesDir) throws PageException {
        this(username,password,toRole(roles),rolesDir);
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     * @param roles
     * @throws PageException
     */
    public CredentialImpl(String username,String password, Array roles,Resource rolesDir) throws PageException {
        this(username,password,toRole(roles),rolesDir);
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     * @param roles
     */
    public CredentialImpl(String username,String password,String[] roles,Resource rolesDir) {
        this.username=username;
        this.password=password;
        this.roles=roles;
        this.rolesDir=rolesDir;
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String[] getRoles() {
        return roles;
    }
    @Override
    public String getUsername() {
        return username;
    }
    
    /**
     * convert a Object to a String Array of Roles
     * @param oRoles
     * @return roles
     * @throws PageException
     */
    public static String[] toRole(Object oRoles) throws PageException {
        if(oRoles instanceof String) {
            oRoles=ListUtil.listToArrayRemoveEmpty(oRoles.toString(),",");
        }
        
        if(oRoles instanceof Array) {
            Array arrRoles = (Array) oRoles;
            String[] roles=new String[arrRoles.size()];
            for(int i=0;i<roles.length;i++) {
                roles[i]=Caster.toString(arrRoles.get(i+1,""));
            }
            return roles;
        }
        throw new ApplicationException("invalid roles definition for tag loginuser");
    }

    public String serialize() {
    	return serialize(null);
    }

    public String serialize(Set<Object> done) {
        return "createObject('java','railo.runtime.security.Credential').init('"+username+"','"+password+"','"+ListUtil.arrayToList(roles,",")+"')";
    } 
    
    
    @Override
    public String encode() throws PageException{
    	String raw=ListUtil.arrayToList(roles,",");
		if(raw.length()>100){
	    	try {
	    		if(!rolesDir.exists())rolesDir.mkdirs();
	    		String md5 = MD5.getDigestAsString(raw);
				IOUtil.write(rolesDir.getRealResource(md5), raw, "utf-8", false);
				return Caster.toB64(username+ONE+password+ONE+"md5:"+md5,"UTF-8");
			} 
	    	catch (IOException e) {}
		}
    	try {
			return Caster.toB64(username+ONE+password+ONE+raw,"UTF-8");
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
    } 
    
    /**
     * decode the Credential form a Base64 String value
     * @param encoded
     * @return Credential from decoded string
     * @throws PageException
     */
    public static Credential decode(Object encoded,Resource rolesDir) throws PageException {
    	String dec;
    	try {
			dec=Base64Coder.decodeToString(Caster.toString(encoded),"UTF-8");
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
    	
    	Array arr=ListUtil.listToArray(dec,""+ONE);
        int len=arr.size();
        if(len==3) {
        	String str=Caster.toString(arr.get(3,""));
        	if(str.startsWith("md5:")){
	    		if(!rolesDir.exists())rolesDir.mkdirs();
        		str=str.substring(4);
        		Resource md5 = rolesDir.getRealResource(str);
        		try {
					str=IOUtil.toString(md5, "utf-8");
				} catch (IOException e) {
					str="";
				}
        	}
        	
        	return new CredentialImpl(Caster.toString(arr.get(1,"")),Caster.toString(arr.get(2,"")),str,rolesDir);
        }
        if(len==2) return new CredentialImpl(Caster.toString(arr.get(1,"")),Caster.toString(arr.get(2,"")),rolesDir);
        if(len==1) return new CredentialImpl(Caster.toString(arr.get(1,"")),rolesDir);
        
        return null;
    }

	@Override
	public String toString() {
		return "username:"+username+";password:"+password+";roles:"+roles;
	} 
    
}