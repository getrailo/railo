<!--- Schedule --->
<cfset stText.Schedule.Description         = "Here you can add, modify, run and delete scheduled tasks<br/><br/>">
<cfset stText.Schedule.Detail              = "Defined scheduled tasks">
<cfset stText.Schedule.DetailDescription   = "The tasks displayed in red have expired and will no longer start.">
<cfset stText.Schedule.Name                = "Name">
<cfset stText.Schedule.NameDescription     = "Name of the new task (this name must be unique)">
<cfset stText.Schedule.NameMissing         = "Please enter a value for the name of the task">
<cfset stText.Schedule.NameDescEdit        = "URL that will be invoked by the task">
<cfset stText.Schedule.URL                 = "URL">
<cfset stText.Schedule.URLDescription      = "URL of the new task">
<cfset stText.Schedule.URLMissing          = "Please enter a value for the URL of the task">
<cfset stText.Schedule.Every               = "every">
<cfset stText.Schedule.CreateTask          = "Create scheduled task">
<cfset stText.Schedule.Interval            = "Interval">
<cfset stText.Schedule.IntervalDesc        = "Interval in that the task will be executed">
<cfset stText.Schedule.IntervalType        = "Interval type">
<cfset stText.Schedule.IntervalTypeDesc    = "Execution interval of the new task">
<cfset stText.Schedule.Once                = "once">
<cfset stText.Schedule.Daily               = "daily">
<cfset stText.Schedule.Weekly              = "weekly">
<cfset stText.Schedule.Monthly             = "monthly">
<cfset stText.Schedule.StartDate           = "Start date">
<cfset stText.Schedule.StartTime           = "Start time">
<cfset stText.Schedule.Port                = "Port">
<cfset stText.Schedule.PortDescription     = "Port of the URL to call (HTTP Default: 80)">
<cfset stText.Schedule.Timeout             = "Timeout (in seconds)">
<cfset stText.Schedule.TimeoutDescription  = "Timeout in seconds. Defines how long a task will wait for the response of the server called by the URL">
<cfset stText.Schedule.Username            = "Username">
<cfset stText.Schedule.UserNameDescription = "Username to access the URL if it is protected by authentication">
<cfset stText.Schedule.Password            = "Password">
<cfset stText.Schedule.PasswordDescription = "Password to access the URL if it is protected by authentication">
<cfset stText.Schedule.Proxy               = "Proxy settings">
<cfset stText.Schedule.ProxyDesc           = "When there is a Proxy Server between the Railo Server and the called URL, you can define the Proxy Servers Setting here to access the URL">

<cfset stText.Schedule.Server              = "Server">
<cfset stText.Schedule.ProxyServerDesc		="The address of the proxy server (example: my.proxy.com)">
<cfset stText.Schedule.ProxyPort           = "Port of the proxy server">
<cfset stText.Schedule.ProxyUserName       = "Username to access a proxy if it is  protected by authentication">
<cfset stText.Schedule.ProxyPassword       = "Password to access a proxy if it is  protected by authentication">
<cfset stText.Schedule.Output              = "Logging">
<cfset stText.Schedule.OutputDesc              = "With the following settings you can define, if railo should store the result of the url invocation and where railo should store this result">
<cfset stText.Schedule.Publish             = "Save to file">
<cfset stText.Schedule.StoreResponse       = "Sets, whether the response of server will be stored in a file or not">
<cfset stText.Schedule.File                = "File">
<cfset stText.Schedule.FileDescription     = "File the output is stored to">
<cfset stText.Schedule.Resolve_URL         = "Resolve URL">
<cfset stText.Schedule.ResolveDescription  = "Translate relative URLs into absolute">


<cfset stText.Schedule.ExecutionDescOnce       = "This task is executed only once at a specfic date and time.">
<cfset stText.Schedule.ExecutionDescDaily       = "This task is executed once a day.">
<cfset stText.Schedule.ExecutionDescWeekly      = "This task is executed once a week.">
<cfset stText.Schedule.ExecutionDescMonthly       = "This task is executed once a month.">
<cfset stText.Schedule.ExecutionDescEvery       = "This task is executed in a certain intervall.">


<cfset stText.Schedule.ExecuteAtDesc           = "Defines the date and time when the task should be executed.">
<cfset stText.Schedule.StartsAtDesc           = "Defines the date when the task should be executed the first time.">
<cfset stText.Schedule.EndsAtDescDaily           = "Defines the date when the task should be executed the last time.">
<cfset stText.Schedule.EndsAtDescMonthly           = "Defines a date within the task should be executed the last time.">
<cfset stText.Schedule.EndsAtDescWeekly           = "Defines a date within the task should be executed the last time.">



<cfset stText.Schedule.ExecutionTimeDescDaily       = "Defines the time when the Task is executed daily">
<cfset stText.Schedule.ExecutionTimeDescMonthly       = "Defines the time when the Task is executed monthly">
<cfset stText.Schedule.ExecutionTimeDescWeekly       = "Defines the time when the Task is executed weekly">

<cfset stText.Schedule.pauseDesc="By setting this flag, the execution of the task will be paused.">

<cfset stText.Schedule.StartDateDesc=stText.Schedule.StartsAtDesc>
<cfset stText.Schedule.StartTimeDesc="Defines the time when railo starts to executed the task within every day.">
<cfset stText.Schedule.endDateDesc="Define a date when the task should be executed the last time.">
<cfset stText.Schedule.endTimeDesc="Define the time when railo stop to executed the task within every day.">







<cfset stText.Schedule.ExecutionDate       = "Execution date/time">
<cfset stText.Schedule.ExecuteAt           = "Execute at">
<cfset stText.Schedule.CurrentDateTime     = "Current date/time of this Railo context is: (mm/dd/yyyy hh:mm tt)">
<cfset stText.Schedule.StartsAt            = "Start Date">
<cfset stText.Schedule.ExecutionTime       = "Execution time">
<cfset stText.Schedule.EndsAt              = "End date">
<cfset stText.Schedule.EndDate             = "End date">
<cfset stText.Schedule.EndTime             = "End time">
<cfset stText.Schedule.paused="Paused">
<cfset stText.Schedule.pause="pause">
<cfset stText.Schedule.resume="resume">