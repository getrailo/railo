<cfcomponent extends="Gateway">

	
    <cfset fields=array(
		field("Provider URL","providerURL","tcp://localhost:61616",true,"This is the URL of the JMS provider, that is, the server. You can override a number of configuration parameters using a query string, for example, providerURL=tcp://localhost:61616?jms.redeliveryPolicy.maximumRedeliveries=20","text")
		,field("Initial Context Factory","initialContextFactory","org.apache.activemq.jndi.ActiveMQInitialContextFactory",true,"This is the name of the class used to construct the initial JNDI context that is used to lookup the connection factory and destination.","text")
		,field("Connection Factory","connectionFactory","ConnectionFactory",true,"This is generally the name of the class used to construct the factory object that creates connections. For some providers, it is a c-name identifying the resource within some sort of directory, such as LDAP.","text")
		,field("Destination Name","destinationName","dynamicQueues/TEST.FOO",true,"required for message consumption. This is the full name of a message destination to which the gateway should subscribe. This is not needed for outbound messages because the destination name is provided for each message. If the destination name is a full LDAP-style c-name, the gateway also constructs a short destination name, which is the base c-name converted to lowercase; everything after the first comma in the full c-name is discarded.","text")
		
		
		,field("Debug","debug","yes",true,"If [yes], the gateway generates verbose logging. The default is to only log problems.","radio","yes,no")
		,field("Topic","topic","no",true,"If [yes], the gateway expects the destination name to refer to a topic. If [no], the gateway expects the destination name to refer to a queue.","radio","yes,no")
		
		
	)>
<!---
#outboundOnly=no
debug=yes
topic=no
# uncomment the next four lines to test transacted message consumption:
#transacted=yes
#poolSize=5
#transactionTimeout=5
#actionOnTimeout=commit
#cachable=no
#username=
#password=
# uncomment the next line to test message selector filtering:
#selector=MessageNumber > 4
#noLocal=no
# default contextProperties is empty - see below for note on ActiveMQ usage
#contextProperties


# ActiveMQ requires fake JNDI entries to lookup queue / topic names, e.g.,
#contextProperties=queue.localQueueAlias,topic.localTopicAlias
#queue.localQueueAlias=RemoteQueueName
#topic.localTopicAlias=RemoteTopicName
#destinationName=localQueueAlias
# sendGatewayMessage() could be asked to send messages to topic localTopicAlias or
# queue localQueueAlias and the JNDI lookup will resolve to RemoteTopicName or
# RemoteQueueName respectively.

# ActiveMQ also supports dynamicQueues and dynamicTopics:


#durable=no
#publisherName=uniqueSubscriber
#subscriberName=uniqueSubscriber
--->
	<cffunction name="getClass" returntype="string">
    	<cfreturn "railo.extension.gateway.jms.JMSGateway">
    </cffunction>
	<cffunction name="getCFCPath" returntype="string">
    	<cfreturn "">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="no">
    	<cfreturn "JMS">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Processing a JMS messages in Railo">
    </cffunction>
    
	<cffunction name="onBeforeUpdate" returntype="void" output="false">
		<cfargument name="cfcPath" required="true" type="string">
		<cfargument name="startupMode" required="true" type="string">
		<cfargument name="custom" required="true" type="struct">
        
	</cffunction>
    
    
	<cffunction name="getListenerCfcMode" returntype="string" output="no">
		<cfreturn "required">
	</cffunction>
</cfcomponent>

