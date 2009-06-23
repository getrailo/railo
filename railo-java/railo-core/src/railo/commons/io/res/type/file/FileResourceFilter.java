

package railo.commons.io.res.type.file;

import java.io.FileFilter;

import railo.commons.io.res.filter.ResourceFilter;


/**
 * combine ResourceFilter and FileFilter
 */
public interface FileResourceFilter extends ResourceFilter, FileFilter {

}
