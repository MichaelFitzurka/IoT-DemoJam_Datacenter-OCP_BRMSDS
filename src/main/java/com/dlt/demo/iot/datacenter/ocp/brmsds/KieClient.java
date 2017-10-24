package com.dlt.demo.iot.datacenter.ocp.brmsds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

public class KieClient {

    private static final String KIE_SERVER_CONTAINER_ID = "SensorRules";

    private RuleServicesClient client = null;

    @SuppressWarnings("unused")
    private KieClient() {
        super();
    }

    public KieClient(final String url, final String username, final String password) {
        super();

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);

        config.setTimeout(5000L);

        config.setMarshallingFormat(MarshallingFormat.JAXB);
        Set<Class<?>> classes = new HashSet<Class<?>>(1);
        classes.add(DataSet.class);
        config.addExtraClasses(classes);

        client = KieServicesFactory.newKieServicesClient(config).getServicesClient(RuleServicesClient.class);
    }

    public DataSet fireRules(final DataSet requestDataSet) {
        DataSet responseDataSet = requestDataSet;

        System.out.println("DataSet before rule fire: " + requestDataSet);

        KieCommands commands = KieServices.Factory.get().getCommands();
        List<Command<?>> commandList = new ArrayList<Command<?>>(2);
        commandList.add(commands.newInsert(requestDataSet, "DataSet"));
        commandList.add(commands.newFireAllRules());

        BatchExecutionCommand batch = commands.newBatchExecution(commandList);

        ServiceResponse<ExecutionResults> response = client.executeCommandsWithResults(KIE_SERVER_CONTAINER_ID, batch);

        if (response.getType() == ResponseType.SUCCESS) {
            System.out.println("Commands executed with success!");
            responseDataSet = (DataSet) response.getResult().getValue("DataSet");
        } else {
            System.out.println("Error executing rules. Message: " + response.getMsg());
        }

        System.out.println("DataSet after rule fire: " + responseDataSet);

        return responseDataSet;
    }

    @Override
    public String toString() {
        return "BRMSClient [client=" + client + "]";
    }

}
