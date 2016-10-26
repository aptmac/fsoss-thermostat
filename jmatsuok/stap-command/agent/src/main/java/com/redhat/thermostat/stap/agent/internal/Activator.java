package com.redhat.thermostat.stap.agent.internal;

import com.redhat.thermostat.backend.Backend;
import com.redhat.thermostat.backend.BackendService;
import com.redhat.thermostat.common.ApplicationService;
import com.redhat.thermostat.common.MultipleServiceTracker;
import com.redhat.thermostat.common.Version;
import com.redhat.thermostat.stap.agent.StapBackend;
import com.redhat.thermostat.stap.common.StapDAO;
import com.redhat.thermostat.storage.core.WriterID;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Map;

/**
 * Created by jmatsuok on 25/10/16.
 */
public class Activator implements BundleActivator {

    private MultipleServiceTracker tracker;
    private StapBackend backend;
    private ServiceRegistration<Backend> reg;

    @Override
    public void start(final BundleContext context) {

        Class<?>[] deps = new Class<?>[] {
                BackendService.class,
                StapDAO.class,
                ApplicationService.class,
                WriterID.class, // numa backend uses it
        };
        tracker = new MultipleServiceTracker(context, deps, new MultipleServiceTracker.Action() {

            @Override
            public void dependenciesAvailable(Map<String, Object> services) {
                ApplicationService appService = (ApplicationService) services.get(ApplicationService.class.getName());
                StapDAO stapDAO = (StapDAO) services.get(StapDAO.class.getName());
                Version version = new Version(context.getBundle());
                WriterID writerId = (WriterID) services.get(WriterID.class.getName());
                backend = new StapBackend(appService, stapDAO, version, writerId);
                reg = context.registerService(Backend.class, backend, null);
            }

            @Override
            public void dependenciesUnavailable() {
                if (backend.isActive()) {
                    backend.deactivate();
                }
                reg.unregister();
            }
        });
        tracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        tracker.close();
    }
}
