package com.dlt.demo.iot.datacenter.ocp.brmsds;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

public class AMQTester {

    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;

    public AMQTester() {
        super();
    }

    public void waitForBroker(final String userName, final String password, final String brokerURL) {
        while (!isAvailabile(userName, password, brokerURL)) {
            System.out.println(String.format("AMQ-Broker %s not yet available.", brokerURL));

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(String.format("AMQ-Broker %s ready to work!", brokerURL));
    }

    public boolean isAvailabile(final String userName, final String password, final String brokerURL) {
        boolean result = false;

        try {
            connectionFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);
            connection = connectionFactory.createConnection();
            connection.start();
            connection.close();
            result = true;
        } catch (JMSException e) {
            result = false;
        }

        return result;
    }

    @Override
    public String toString() {
        return "AMQTester [connectionFactory=" + connectionFactory + ", connection=" + connection + "]";
    }

}
