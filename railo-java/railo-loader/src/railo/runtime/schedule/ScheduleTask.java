package railo.runtime.schedule;

import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import railo.commons.io.res.Resource;
import railo.runtime.type.dt.Date;
import railo.runtime.type.dt.Time;


/**
 * a single scheduler task
 */
public interface ScheduleTask {

    /**
     * Field <code>OPERATION_HTTP_REQUEST</code>
     */
    public static final short OPERATION_HTTP_REQUEST = 0;

    /**
     * Field <code>INTERVAL_ONCE</code>
     */
    public static final int INTERVAL_ONCE = 0;

    /**
     * Field <code>INTERVAL_DAY</code>
     */
    public static final int INTERVAL_DAY = 1;

    /**
     * Field <code>INTERVAL_WEEK</code>
     */
    public static final int INTERVAL_WEEK = 2;

    /**
     * Field <code>INTERVAL_MONTH</code>
     */
    public static final int INTERVAL_MONTH = 3;

    /**
     * @return Returns the credentials.
     */
    public abstract Credentials getCredentials();

    /**
     * @return Returns has credentials.
     */
    public abstract boolean hasCredentials();

    /**
     * @return Returns the credentials.
     */
    public abstract UsernamePasswordCredentials getUPCredentials();

    /**
     * @return Returns the file.
     */
    public abstract Resource getResource();

    /**
     * @return Returns the interval.
     */
    public abstract int getInterval();

    /**
     * @return Returns the operation.
     */
    public abstract short getOperation();

    /**
     * @return Returns the proxyHost.
     */
    public abstract String getProxyHost();

    /**
     * @return Returns the proxyPort.
     */
    public abstract int getProxyPort();

    /**
     * @return Returns has proxyCredentials.
     */
    public abstract boolean hasProxyCredentials();

    /**
     * @return Returns the proxyCredentials.
     */
    public abstract Credentials getProxyCredentials();

    /**
     * @return Returns the proxyCredentials.
     */
    public abstract UsernamePasswordCredentials getUPProxyCredentials();

    /**
     * @return Returns the resolveURL.
     */
    public abstract boolean isResolveURL();

    /**
     * @return Returns the task.
     */
    public abstract String getTask();

    /**
     * @return Returns the timeout.
     */
    public abstract long getTimeout();

    /**
     * @return Returns the url.
     */
    public abstract URL getUrl();

    /**
     * @param nextExecution
     */
    public abstract void setNextExecution(long nextExecution);

    /**
     * @return Returns the nextExecution.
     */
    public abstract long getNextExecution();

    /**
     * @return Returns the endDate.
     */
    public abstract Date getEndDate();

    /**
     * @return Returns the startDate.
     */
    public abstract Date getStartDate();

    /**
     * @return Returns the endTime.
     */
    public abstract Time getEndTime();

    /**
     * @return Returns the startTime.
     */
    public abstract Time getStartTime();

    /**
     * @return returns interval definition as String
     */
    public abstract String getIntervalAsString();

    /**
     * @return Returns the strInterval.
     */
    public abstract String getStringInterval();

    /**
     * @return Returns the publish.
     */
    public abstract boolean isPublish();

    /**
     * @return Returns the valid.
     */
    public abstract boolean isValid();

    /**
     * @param valid The valid to set.
     */
    public abstract void setValid(boolean valid);

	/**
	 * @return the hidden
	 */
	public boolean isHidden();

	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden);

	public boolean isPaused();
}