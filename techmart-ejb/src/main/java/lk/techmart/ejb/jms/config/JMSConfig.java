package lk.techmart.ejb.jms.config;

import jakarta.jms.JMSConnectionFactoryDefinition;
import jakarta.jms.JMSDestinationDefinition;

@JMSConnectionFactoryDefinition(
        name = "jms/myQueueConnectionFactory",
        interfaceName = "jakarta.jms.ConnectionFactory"
)
@JMSDestinationDefinition(
        name = "jms/myQueue",
        interfaceName = "jakarta.jms.Queue",
        destinationName = "myQueue"
)
public class JMSConfig {

}
