package com.dlt.demo.iot.datacenter.ocp.brmsds;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.Callable;

import javax.jms.JMSException;
import javax.xml.bind.JAXB;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class Application implements Callable<Void> {

    private static final String BPMSIPS_URL = System.getenv("BPMSIPS_URL");
    private static final String BPMSIPS_USERNAME = System.getenv("BPMSIPS_USERNAME");
    private static final String BPMSIPS_PASSWORD = System.getenv("BPMSIPS_PASSWORD");
    private static final String BPMSIPS_DEPLOYMENT_ID = System.getenv("BPMSIPS_DEPLOYMENT_ID");
    private static final String BPMSIPS_PROCESS_ID = System.getenv("BPMSIPS_PROCESS_ID");

    private static final String DATA_GRID_URL_PREFIX = System.getenv("DATA_GRID_URL_PREFIX");

    private static final String KIE_SERVER_URL = System.getProperty("org.kie.server.location");
    private static final String KIE_SERVER_USER = System.getenv("KIE_SERVER_USER");
    private static final String KIE_SERVER_PASSWORD = System.getenv("KIE_SERVER_PASSWORD");

    private static final String SOURCE_AMQ_BROKER_URL = System.getenv("SOURCE_AMQ_BROKER_URL");
    private static final String SOURCE_BROKER_USERNAME = System.getenv("SOURCE_BROKER_USERNAME");
    private static final String SOURCE_BROKER_PASSWORD = System.getenv("SOURCE_BROKER_PASSWORD");
    private static final String SOURCE_QUEUE = System.getenv("SOURCE_QUEUE");

    private static final String TARGET_MQTT_BROKER_URL = System.getenv("TARGET_MQTT_BROKER_URL");
    private static final String TARGET_BROKER_USERNAME = System.getenv("TARGET_BROKER_USERNAME");
    private static final String TARGET_BROKER_PASSWORD = System.getenv("TARGET_BROKER_PASSWORD");
    private static final String TARGET_CLIENT_ID = System.getenv("TARGET_CLIENT_ID");
    private static final String TARGET_TOPIC = System.getenv("TARGET_TOPIC");

    private static final long AMQ_TIMEOUT = 20000L;
    private static final String ISSUE_KNOWN = "known";
    private static final String ISSUE_SOLVED = "solved";
    private static final String ON_MESSAGE_SUFFIX = "|on";

    private static AMQConsumer amqConsumer = null;
    private static AMQTester amqTester = null;
    private static BPMSClient bpmsClient = null;
    private static DataGridHelper dgHelper = null;
    private static KieClient kieClient = null;
    private static MQTTProducer mqttProducer = null;

    public Application() {
        super();
    }

    @Override
    public Void call() throws Exception {
        main(null);
        return null;
    }

    public static void main(final String[] args)
            throws InterruptedException, JMSException, MqttPersistenceException, MqttException {
        amqConsumer = new AMQConsumer(SOURCE_BROKER_USERNAME, SOURCE_BROKER_PASSWORD, SOURCE_AMQ_BROKER_URL,
                SOURCE_QUEUE);
        amqTester = new AMQTester();
        dgHelper = new DataGridHelper();

        amqTester.waitForBroker(SOURCE_BROKER_USERNAME, SOURCE_BROKER_PASSWORD, SOURCE_AMQ_BROKER_URL);

        String messageFromQueue;
        DataSet dataSet;
        while (true) {
            messageFromQueue = amqConsumer.run(AMQ_TIMEOUT);

            if (messageFromQueue != null) {
            dataSet = processMessageFromQueue(messageFromQueue);

                if (hasCachedIssue(dataSet.getDeviceType(), dataSet.getDeviceID())) {
                    System.out.println("No need to start business process, we know this event already.");
                } else {
                    dataSet = fireRules(dataSet);

                    if (dataSet.getErrorCode() == 0) {
                        System.out.println("Business Rules report no reason to act.");
                    } else {
                        callBPMSuite(dataSet);
                        turnAlarmLightOn(dataSet.getDeviceID());
                        cacheIssue(dataSet.getDeviceType(), dataSet.getDeviceID());
                    }
                }
            }
        }
    }

    private static void cacheIssue(final String deviceType, final String deviceID) {
        System.out.println("Pushing this event to distributed cache!");

        try {
            dgHelper.putMethod(DATA_GRID_URL_PREFIX + deviceType + deviceID, ISSUE_KNOWN);
            System.out.println(String.format("Value via REST after put = <%s>",
                    dgHelper.getMethod(DATA_GRID_URL_PREFIX + deviceType + deviceID)));
        } catch (IOException e) {
            System.out.println("Exception when calling Data Grid: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    private static void callBPMSuite(final DataSet dataSet) {
        System.out.println("Notifying BPM Suite!");

        try {
            if (bpmsClient == null) {
                bpmsClient = new BPMSClient(BPMSIPS_URL, BPMSIPS_USERNAME, BPMSIPS_PASSWORD, BPMSIPS_DEPLOYMENT_ID);
            }

            bpmsClient.doCall(BPMSIPS_PROCESS_ID, dataSet);
        } catch (Exception e) {
            System.out.println("Exception when calling BPM Suite: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    private static DataSet fireRules(final DataSet dataSet) {
        System.out.println(String.format("Verifying DeviceType %s with ID %s with value %s against business rules.",
                dataSet.getDeviceType(), dataSet.getDeviceID(), dataSet.getPayload()));

        if (kieClient == null) {
            kieClient = new KieClient(KIE_SERVER_URL, KIE_SERVER_USER, KIE_SERVER_PASSWORD);
        }

        DataSet result = kieClient.fireRules(dataSet);

        System.out.println(String.format("event <%s%s> [%d] %s", result.getDeviceType(), result.getDeviceID(),
                result.getErrorCode(), result.getErrorMessage()));

        return result;
    }

    private static boolean hasCachedIssue(final String deviceType, final String deviceID) {
        String cacheValue = null;

        System.out.println(String.format("Checking cache if we have an open issue from DeviceType %s with ID %s.",
                deviceType, deviceID));

        try {
            cacheValue = dgHelper.getMethod(DATA_GRID_URL_PREFIX + deviceType + deviceID);
        } catch (IOException e) {
            System.out.println("Exception when calling DataGrid: " + e.getMessage());
            cacheValue = null;
        }

        System.out.println(
                String.format("Cached value for DeviceType %s with ID %s is: %s.", deviceType, deviceID, cacheValue));

        return (cacheValue != null) && !cacheValue.contains(ISSUE_SOLVED);
    }

    private static DataSet processMessageFromQueue(final String messageFromQueue) {
        StringReader reader = new StringReader(messageFromQueue);

        DataSet dataSet = JAXB.unmarshal(reader, DataSet.class);

        dataSet.setRequired(0);
        dataSet.setErrorCode(0);

        return dataSet;
    }

    private static void turnAlarmLightOn(final String deviceID) throws MqttException, MqttPersistenceException {
        System.out.println("Turning on alarm light!");

        if (mqttProducer == null) {
            mqttProducer = new MQTTProducer(TARGET_MQTT_BROKER_URL, TARGET_CLIENT_ID, TARGET_BROKER_USERNAME,
                    TARGET_BROKER_PASSWORD);
        }

        mqttProducer.run(TARGET_TOPIC, deviceID + ON_MESSAGE_SUFFIX);
    }

    @Override
    public String toString() {
        return "Application []";
    }

}
