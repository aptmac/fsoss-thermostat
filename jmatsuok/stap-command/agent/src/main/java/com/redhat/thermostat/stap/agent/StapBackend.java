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

package com.redhat.thermostat.stap.agent;

import com.redhat.thermostat.backend.BaseBackend;
import com.redhat.thermostat.backend.BackendService;
import com.redhat.thermostat.common.ApplicationService;
import com.redhat.thermostat.common.Timer;
import com.redhat.thermostat.common.TimerFactory;
import com.redhat.thermostat.common.Version;
import com.redhat.thermostat.stap.common.StapDAO;
import com.redhat.thermostat.stap.common.StapData;
import com.redhat.thermostat.storage.core.WriterID;
import com.sun.xml.internal.rngom.parse.xml.SAXParseable;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class StapBackend extends BaseBackend {

    private static final String SCRIPT_OUTPUT = "/home/jmatsuok/stap-out.txt";
    private ApplicationService appService;
    private StapDAO stapDAO;
    private WriterID writerId;
    private Timer timer;
    boolean started;

    public StapBackend(ApplicationService appService, StapDAO stapDAO, Version version, WriterID writerID) {
        super("SystemTap Backend", "Gathers packet information from SystemTap", "Red Hat Inc.", version.getVersionNumber());
        this.appService = appService;
        this.stapDAO = stapDAO;
        this.writerId = writerID;
    }

    public StapBackend(String name, String description, String vendor, String version) {
        super(name, description, vendor, version);
    }

    @Override
    public boolean activate() {
        started = true;
        TimerFactory tf = appService.getTimerFactory();
        timer = tf.createTimer();
        timer.setDelay(1);
        timer.setInitialDelay(0);
        timer.setSchedulingType(Timer.SchedulingType.FIXED_DELAY);
        timer.setTimeUnit(TimeUnit.SECONDS);
        timer.setAction(new Runnable() {
            @Override
            public void run() {
                saveData(parsedRawOutput(parseOutput()));
            }
        });
        timer.start();
        started = true;
        return true;
    }

    @Override
    public boolean deactivate() {
        started = false;
        timer.stop();
        return true;
    }

    @Override
    public boolean isActive() {
        return started;
    }

    @Override
    public int getOrderValue() {
        return ORDER_MEMORY_GROUP + 81;
    }

    private String[] sanitize(String[] input) {
        String[] sanitized = new String[10];
        int j = 0;
        for (int i = 0; i < input.length; i++) {
            if (!input[i].equals("")) {
                sanitized[j] = input[i];
                j++;
            }
        }
        return sanitized;
    }

    private List<String> parseOutput() {
        String line = null;
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(SCRIPT_OUTPUT), StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null ) {
                lines.add(line);
            }
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("File: " + SCRIPT_OUTPUT + " could not be read.");
            System.out.println(fnfe.getMessage());
        }
        catch (IOException ioe) {
            System.out.println("Error reading File: " + SCRIPT_OUTPUT);
            System.out.println(ioe.getMessage());
        }

        return lines;
    }


    private void saveData(List<StapData> data) {
        for (StapData s : data) {
            stapDAO.saveStapData(s);
        }
    }

    private List<StapData> parsedRawOutput(List<String> rawOutput) {
        List<StapData> parsedOutput = new ArrayList<StapData>();
        for (String s : rawOutput) {
            String[] raw = sanitize(s.split(" "));
            StapData st = new StapData();
            st.setAgentId(raw[0]+":"+raw[2]);
            st.setVmId(raw[1]+":"+raw[3]);
            st.setStapMetric(raw[4]+raw[5]+raw[6]+raw[7]+raw[8]);
            st.setId("1");
            parsedOutput.add(st);
        }
        return parsedOutput;
    }

}
