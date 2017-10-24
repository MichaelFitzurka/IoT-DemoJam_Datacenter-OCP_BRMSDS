package com.dlt.demo.iot.datacenter.ocp.brmsds;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTProducer {

    private MqttClient client;

    @SuppressWarnings("unused")
    private MQTTProducer() {
        super();
    }

    public MQTTProducer(final String brokerURL, final String clientID, final String userName, final String password) {
        super();

        System.out.println(String.format("Connecting to %s with clientID %s, userName %s and password %s", brokerURL,
            clientID, userName, password));

        MemoryPersistence persistence = new MemoryPersistence();
        try {
            client = new MqttClient(brokerURL, clientID, persistence);
        } catch (MqttException e) {
            System.out.println("Exception when creating MQTT Client: " + e.getMessage());
            e.printStackTrace(System.out);
        }

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(userName);
        options.setPassword(password.toCharArray());

        System.out.println("options = " + options);

        try {
            client.connect(options);
        } catch (MqttSecurityException e) {
            System.out.println("Security exception when connecting to MQTT Client!");
            e.printStackTrace();
        } catch (MqttException e) {
            System.out.println("Exception when connecting to MQTT Client: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    public void run(final String topic, final String message) throws MqttException, MqttPersistenceException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());

        System.out.println("Publishing " + mqttMessage.getPayload());

        client.publish(topic, mqttMessage);
    }

    public void close() throws MqttException {
        if (client != null) {
            client.disconnect();
            client.close();
        }
    }

    @Override
    public String toString() {
        return "MQTTProducer [client=" + client + "]";
    }

}
