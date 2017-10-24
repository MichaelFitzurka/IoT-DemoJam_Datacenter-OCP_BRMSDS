package com.dlt.demo.iot.datacenter.ocp.brmsds;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class AMQConsumer implements ExceptionListener {

    private Connection connection;
    private Session session;
    private MessageConsumer messageConsumer;

    @SuppressWarnings("unused")
    private AMQConsumer() {
        super();
    }

    public AMQConsumer(final String userName, final String password, final String brokerURL, final String queueName)
            throws JMSException {
        super();

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);
        connection = connectionFactory.createConnection();
        connection.start();
        connection.setExceptionListener(this);

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(queueName);
        messageConsumer = session.createConsumer(destination);
    }

    public String run(final long timeout) {
        String text = null;

        try {
            // Wait for a message
            Message message = messageConsumer.receive(timeout);

            if (message instanceof TextMessage) {
                text = ((TextMessage) message).getText();
            }
        } catch (Exception e) {
            System.out.println("Exception processing message: " + e.getMessage());
            e.printStackTrace(System.out);
        }

        return text;
    }

    public void close() throws JMSException {
        try {
            if (messageConsumer != null) {
                messageConsumer.close();
            }
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
    }

    public void onException(final JMSException e) {
        System.out.println("Exception from Consumer: " + e.getMessage());
        e.printStackTrace(System.out);
    }

    @Override
    public String toString() {
        return "AMQConsumer [connection=" + connection + ", session=" + session + ", messageConsumer=" + messageConsumer
                + "]";
    }

}
