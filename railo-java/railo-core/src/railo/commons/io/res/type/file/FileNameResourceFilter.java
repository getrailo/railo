package railo.commons.io.res.type.file;

import java.io.FilenameFilter;

import railo.commons.io.res.filter.ResourceNameFilter;

/**
 * combine ResourceNameFilter and FilenameFilter
 */
public interface FileNameResourceFilter extends ResourceNameFilter, FilenameFilter {

}
