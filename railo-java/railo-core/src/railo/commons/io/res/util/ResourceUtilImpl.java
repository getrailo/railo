package railo.commons.io.res.util;

import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.system.ContractPath;

public class ResourceUtilImpl implements railo.runtime.util.ResourceUtil {

	private ResourceUtilImpl(){}
	private static ResourceUtilImpl impl=new ResourceUtilImpl();
	

	public static ResourceUtilImpl getInstance() {
		return impl;
	}
	
	/**
	 * @see railo.runtime.util.ResourceUtil#checkCopyToOK(railo.commons.io.res.Resource, railo.commons.io.res.Resource)
	 */
	public void checkCopyToOK(Resource source, Resource target) throws IOException {
		ResourceUtil.checkCopyToOK(source, target);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#checkCreateDirectoryOK(railo.commons.io.res.Resource, boolean)
	 */
	public void checkCreateDirectoryOK(Resource resource,boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateDirectoryOK(resource, createParentWhenNotExists);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#checkCreateFileOK(railo.commons.io.res.Resource, boolean)
	 */
	public void checkCreateFileOK(Resource resource,boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateFileOK(resource, createParentWhenNotExists);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#checkGetInputStreamOK(railo.commons.io.res.Resource)
	 */
	public void checkGetInputStreamOK(Resource resource) throws IOException {
		ResourceUtil.checkGetInputStreamOK(resource);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#checkGetOutputStreamOK(railo.commons.io.res.Resource)
	 */
	public void checkGetOutputStreamOK(Resource resource) throws IOException {
		ResourceUtil.checkGetOutputStreamOK(resource);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#checkMoveToOK(railo.commons.io.res.Resource, railo.commons.io.res.Resource)
	 */
	public void checkMoveToOK(Resource source, Resource target)throws IOException {
		ResourceUtil.checkMoveToOK(source, target);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#checkRemoveOK(railo.commons.io.res.Resource)
	 */
	public void checkRemoveOK(Resource resource) throws IOException {
		ResourceUtil.checkRemoveOK(resource);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#copyRecursive(railo.commons.io.res.Resource, railo.commons.io.res.Resource)
	 */
	public void copyRecursive(Resource src, Resource trg) throws IOException {
		ResourceUtil.copyRecursive(src, trg);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#copyRecursive(railo.commons.io.res.Resource, railo.commons.io.res.Resource, railo.commons.io.res.filter.ResourceFilter)
	 */
	public void copyRecursive(Resource src, Resource trg, ResourceFilter filter) throws IOException {
		ResourceUtil.copyRecursive(src, trg,filter);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#createResource(railo.commons.io.res.Resource, short, short)
	 */
	public Resource createResource(Resource res, short level, short type) {
		return ResourceUtil.createResource(res, level, type);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#getExtension(railo.commons.io.res.Resource)
	 */
	public String getExtension(Resource res) {
		return ResourceUtil.getExtension(res,null);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#getExtension(railo.commons.io.res.Resource, java.lang.String)
	 */
	public String getExtension(Resource res, String defaultValue) {
		return ResourceUtil.getExtension(res,defaultValue);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#getExtension(java.lang.String)
	 */
	public String getExtension(String strFile) {
		return ResourceUtil.getExtension(strFile,null);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#getExtension(java.lang.String, java.lang.String)
	 */
	public String getExtension(String strFile, String defaultValue) {
		return ResourceUtil.getExtension(strFile,defaultValue);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#getMymeType(railo.commons.io.res.Resource, java.lang.String)
	 */
	public String getMymeType(Resource res, String defaultValue) {
		return ResourceUtil.getMymeType(res, defaultValue);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#getMymeType(byte[], java.lang.String)
	 */
	public String getMymeType(byte[] barr, String defaultValue) {
		return ResourceUtil.getMymeType(barr, defaultValue);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#getPathToChild(railo.commons.io.res.Resource, railo.commons.io.res.Resource)
	 */
	public String getPathToChild(Resource file, Resource dir) {
		return ResourceUtil.getPathToChild(file, dir);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#isChildOf(railo.commons.io.res.Resource, railo.commons.io.res.Resource)
	 */
	public boolean isChildOf(Resource file, Resource dir) {
		return ResourceUtil.isChildOf(file, dir);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#isEmpty(railo.commons.io.res.Resource)
	 */
	public boolean isEmpty(Resource res) {
		return ResourceUtil.isEmpty(res);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#isEmptyDirectory(railo.commons.io.res.Resource)
	 */
	public boolean isEmptyDirectory(Resource res) {
		return ResourceUtil.isEmptyDirectory(res);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#isEmptyFile(railo.commons.io.res.Resource)
	 */
	public boolean isEmptyFile(Resource res) {
		return ResourceUtil.isEmptyFile(res);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#merge(java.lang.String, java.lang.String)
	 */
	public String merge(String parent, String child) {
		return ResourceUtil.merge(parent, child);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#moveTo(railo.commons.io.res.Resource, railo.commons.io.res.Resource)
	 */
	public void moveTo(Resource src, Resource dest) throws IOException {
		ResourceUtil.moveTo(src, dest);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#removeChildren(railo.commons.io.res.Resource)
	 */
	public void removeChildren(Resource res) throws IOException {
		ResourceUtil.removeChildren(res);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#removeChildren(railo.commons.io.res.Resource, railo.commons.io.res.filter.ResourceNameFilter)
	 */
	public void removeChildren(Resource res, ResourceNameFilter filter)throws IOException {
		ResourceUtil.removeChildren(res, filter);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#removeChildren(railo.commons.io.res.Resource, railo.commons.io.res.filter.ResourceFilter)
	 */
	public void removeChildren(Resource res, ResourceFilter filter) throws IOException {
		ResourceUtil.removeChildren(res, filter);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#removeScheme(java.lang.String, java.lang.String)
	 */
	public String removeScheme(String scheme, String path) {
		return ResourceUtil.removeScheme(scheme, path);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#setAttribute(railo.commons.io.res.Resource, java.lang.String)
	 */
	public void setAttribute(Resource res, String attributes) throws IOException {
		ResourceUtil.setAttribute(res, attributes);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#toResourceExisting(railo.runtime.PageContext, java.lang.String)
	 */
	public Resource toResourceExisting(PageContext pc, String path) throws PageException {
		return ResourceUtil.toResourceExisting(pc, path);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#toResourceExistingParent(railo.runtime.PageContext, java.lang.String)
	 */
	public Resource toResourceExistingParent(PageContext pc, String destination)throws PageException {
		return ResourceUtil.toResourceExistingParent(pc, destination);
	}

	/**
	 * @see railo.runtime.util.ResourceUtil#toResourceNotExisting(railo.runtime.PageContext, java.lang.String)
	 */
	public Resource toResourceNotExisting(PageContext pc, String destination) {
		return ResourceUtil.toResourceNotExisting(pc, destination);
	}

	/**
	 * @see railo.commons.io.res.util.ResourceUtil#translatePath(java.lang.String, boolean, boolean)
	 */
	public String translatePath(String path, boolean slashAdBegin,boolean slashAddEnd) {
		return ResourceUtil.translatePath(path, slashAdBegin, slashAddEnd);
	}

	/**
	 * @see railo.commons.io.res.util.ResourceUtil#translatePathName(java.lang.String)
	 */
	public String[] translatePathName(String path) {
		return ResourceUtil.translatePathName(path);
	}

	public String toString(Resource r, String charset) throws IOException {
		return IOUtil.toString(r, charset);
	}

	@Override
	public String contractPath(PageContext pc, String path) {
		return ContractPath.call(pc, path);
	}
}
