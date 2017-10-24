package com.dlt.demo.iot.datacenter.ocp.brmsds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RulesListener implements ServletContextListener {

    private ExecutorService service = null;

    public void contextInitialized(final ServletContextEvent sce) {
        service = Executors.newSingleThreadExecutor();
        service.submit(new Application());
    }

    public void contextDestroyed(final ServletContextEvent sce) {
        if (service != null && !service.isShutdown()) {
            service.shutdown();
        }
    }

    @Override
    public String toString() {
        return "RulesListener [service=" + service + "]";
    }

}
