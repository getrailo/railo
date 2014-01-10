package railo.runtime.search.lucene2;



import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.ResourceUtil;

/**
 * FilFilter that only allow filter with given extensions 
 * by constructor or directory, if constructor variable recurse is true
 */
public final class LuceneExtensionFileFilter implements ResourceFilter {

    private String[] extensions;
    private boolean recurse;
    private boolean noExtension;
    private boolean allExtension;

    /**
     * constructor of the class
     * @param extensions
     * @param recurse
     */
    public LuceneExtensionFileFilter(String[] extensions, boolean recurse) {
        
        this.extensions=extensions;
        
        for(int i=0;i<extensions.length;i++) {
            String ext = extensions[i].trim();
            
            if(ext.equals("*."))	{
                noExtension=true;
                continue;
            }
            if(ext.equals(".*") || ext.equals("*.*"))	{
                allExtension = true;
                continue;
            }
            
            // asterix
            int startIndex=ext.indexOf('*');
            if(startIndex==0) ext=ext.substring(1);
            
            // dot
            int startDot=ext.indexOf('.');
            if(startDot==0) ext=ext.substring(1);
            
            if(ext.equals("*"))ext="";
            //print.ln(ext);
            extensions[i]=ext.toLowerCase();
        }
        this.recurse=recurse;
    }

    @Override
    public boolean accept(Resource res) {
        if(res.isDirectory()) return recurse;
        else if(res.isFile()) {
            String ext=ResourceUtil.getExtension(res,null);
            if(ext==null) return noExtension;
            else if(allExtension) return true;
                        
            for(int i=0;i<extensions.length;i++) {
                if(extensions[i].equalsIgnoreCase(ext)) return true;
            }
            return false;
        }
        return false;
    }
}