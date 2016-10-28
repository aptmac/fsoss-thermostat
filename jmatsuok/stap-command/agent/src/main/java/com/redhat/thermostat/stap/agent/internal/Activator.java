/*
 * Copyright 2012-2016 Red Hat, Inc.
 *
 * This file is part of Thermostat.
 *
 * Thermostat is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2, or (at your
 * option) any later version.
 *
 * Thermostat is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermostat; see the file COPYING.  If not see
 * <http://www.gnu.org/licenses/>.
 *
 * Linking this code with other modules is making a combined work
 * based on this code.  Thus, the terms and conditions of the GNU
 * General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this code give
 * you permission to link this code with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also
 * meet, for each linked independent module, the terms and conditions
 * of the license of that module.  An independent module is a module
 * which is not derived from or based on this code.  If you modify
 * this code, you may extend this exception to your version of the
 * library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

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
                WriterID.class,
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
