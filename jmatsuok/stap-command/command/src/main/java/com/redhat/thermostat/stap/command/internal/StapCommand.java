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

package com.redhat.thermostat.stap.command.internal;

import com.redhat.thermostat.stap.common.StapData;
import com.redhat.thermostat.storage.core.AgentId;
import com.redhat.thermostat.storage.core.StatementDescriptor;
import com.redhat.thermostat.storage.core.VmId;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;

import com.redhat.thermostat.stap.agent.StapBackend;
import com.redhat.thermostat.common.cli.Command;
import com.redhat.thermostat.common.cli.CommandContext;
import com.redhat.thermostat.common.cli.CommandException;
import com.redhat.thermostat.common.cli.DependencyServices;
import com.redhat.thermostat.stap.common.StapDAO;

import java.util.List;

@Component
@Service
@Property(name = Command.NAME, value = "stap")
@References({
        @Reference(name = "stapDao", referenceInterface = StapDAO.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_UNARY),
        })
public class StapCommand implements Command {

    private static final String SCRIPT_PATH = "/home/Jmatsuok/FSOSS-2016/stap/gc.stp";
    private static final String SCRIPT_OUTPUT = "/home/Jmatsuok/packet-output.txt";
    public static final String NAME = "stap";

    private StapDAO stapDao;
    private DependencyServices dependencyServices = new DependencyServices();

    public void run(CommandContext ctx) throws CommandException {
        stapDao = dependencyServices.getService(StapDAO.class);
        if (stapDao == null) {
            System.out.println("StapDAO unavailable.");
        }
        if (ctx.getArguments().getNonOptionArguments().size() != 1) {
            System.out.println("Must supply an IP Address/Port");
            return;
        }
        long count = stapDao.getCount(ctx.getArguments().getNonOptionArguments().get(0));
        System.out.println(ctx.getArguments().getNonOptionArguments().get(0) +
                " Received " + count + " Packets");
        System.out.println(stapDao.getDistinctIPs().toString());

    }

    public boolean isStorageRequired() {
        return true;
    }

    void bindStapDao(StapDAO dao) {
        dependencyServices.addService(StapDAO.class, dao);
    }

    void unbindStapDao(StapDAO dao) {
        dependencyServices.removeService(StapDAO.class);
    }

}

