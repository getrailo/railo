package railo.runtime.security;

import railo.runtime.coder.Base64Coder;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.List;

/**
 * User Password Information
 */
public final class CredentialImpl implements Credential {
    String username;
    String password;
    String[] roles;
    private static final char ONE=(char)1;

    /**
     * credential constructor
     * @param username
     */
    public CredentialImpl(String username) {
        this(username,null,new String[0]);
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     */
    public CredentialImpl(String username,String password) {
        this(username,password,new String[0]);
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     * @param roles
     * @throws PageException
     */
    public CredentialImpl(String username,String password, String roles) throws PageException {
        this(username,password,toRole(roles));
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     * @param roles
     * @throws PageException
     */
    public CredentialImpl(String username,String password, Array roles) throws PageException {
        this(username,password,toRole(roles));
    }
    
    /**
     * credential constructor
     * @param username
     * @param password
     * @param roles
     */
    public CredentialImpl(String username,String password,String[] roles) {
        this.username=username;
        this.password=password;
        this.roles=roles;
    }

    /**
     * @see railo.runtime.security.Credential#getPassword()
     */
    public String getPassword() {
        return password;
    }
    /**
     * @see railo.runtime.security.Credential#getRoles()
     */
    public String[] getRoles() {
        return roles;
    }
    /**
     * @see railo.runtime.security.Credential#getUsername()
     */
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
            oRoles=List.listToArrayRemoveEmpty(oRoles.toString(),",");
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

    /**
     * @see railo.runtime.converter.ScriptConvertable#serialize()
     */
    public String serialize() {
        return "createObject('java','railo.runtime.security.Credential').init('"+username+"','"+password+"','"+List.arrayToList(roles,",")+"')";
    } 
    
    
    /**
     * @see railo.runtime.security.Credential#encode()
     */
    public String encode() throws PageException {
        return Caster.toBase64(username+ONE+password+ONE+List.arrayToList(roles,","));
    } 
    
    /**
     * decode the Credential form a Base64 String value
     * @param encoded
     * @return Credential from decoded string
     * @throws PageException
     */
    public static Credential decode(Object encoded) throws PageException {
        
        /*StringBuffer sb=new StringBuffer();
        byte[] bytes = Caster.toBinary(encoded);
		for(int i=0;i<bytes.length;i++) {
			sb.append((char)bytes[i]);
		}*/
        
        Array arr=List.listToArray(Base64Coder.decodeBase64(encoded),""+ONE);
        int len=arr.size();
        if(len==3) return new CredentialImpl(Caster.toString(arr.get(1,"")),Caster.toString(arr.get(2,"")),Caster.toString(arr.get(3,"")));
        if(len==2) return new CredentialImpl(Caster.toString(arr.get(1,"")),Caster.toString(arr.get(2,"")));
        if(len==1) return new CredentialImpl(Caster.toString(arr.get(1,"")));
        
        return null;
    } 
    
    /*public static void main(String[] args) throws PageException {
        Credential c=new Credential("mic","mouse","aaa,bbb,ccc,ddd");
        print.ln(c.serialize());
        
    }*/
}