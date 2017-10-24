package com.dlt.demo.iot.datacenter.ocp.brmsds;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;

public class BPMSClient {

    private KieSession kieSession;

    @SuppressWarnings("unused")
    private BPMSClient() {
        super();
    }

    public BPMSClient(final String bpmsURL, final String userName, final String password, final String deploymentId) {
        super();

        RuntimeEngine runtimeEngine = getRuntimeEngine(bpmsURL, userName, password, deploymentId);

        kieSession = runtimeEngine.getKieSession();
    }

    public void doCall(final String processId, final DataSet dataSet) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("deviceType", dataSet.getDeviceType());
        parameters.put("deviceID", dataSet.getDeviceID());
        parameters.put("payload", Integer.toString(dataSet.getPayload()));
        parameters.put("timestamp", dataSet.getTimestamp());
        parameters.put("errorCode", Integer.toString(dataSet.getErrorCode()));
        parameters.put("errorMessage", dataSet.getErrorMessage());

        kieSession.startProcess(processId, parameters);
    }

    private static RuntimeEngine getRuntimeEngine(final String bpmsURL, final String userName, final String password,
                                                  final String deploymentId) {
        try {
            URL url = new URL(bpmsURL);
            return RemoteRuntimeEngineFactory.newRestBuilder()
                    .addUrl(url)
                    .addUserName(userName)
                    .addPassword(password)
                    .addDeploymentId(deploymentId)
                    .build();
        } catch(MalformedURLException e) {
            System.out.println("Exception when creating BPM Suite Runtime Engine: " + e.getMessage());
            e.printStackTrace(System.out);
            throw new IllegalStateException("This URL is always expected to be valid!", e);
        }
    }

    @Override
    public String toString() {
        return "BPMSClient [kieSession=" + kieSession + "]";
    }

}
