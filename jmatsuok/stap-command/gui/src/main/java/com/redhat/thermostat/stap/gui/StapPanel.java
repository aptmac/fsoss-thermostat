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

package com.redhat.thermostat.stap.gui;

import java.awt.*;
import java.awt.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import com.redhat.thermostat.client.swing.SwingComponent;
import com.redhat.thermostat.client.swing.components.HeaderPanel;
import com.redhat.thermostat.client.swing.components.experimental.ThermostatChartPanel;
import com.redhat.thermostat.client.swing.experimental.ComponentVisibilityNotifier;
import com.redhat.thermostat.common.ActionNotifier;
import com.redhat.thermostat.common.Duration;
import com.redhat.thermostat.shared.locale.Translate;
import com.redhat.thermostat.storage.model.DiscreteTimeData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;

/**
 * Created by jmatsuok on 24/10/16.
 */
public class StapPanel extends StapView implements SwingComponent {

    private static final Translate<LocaleResources> translator = LocaleResources.createLocalizer();
    private Map<String, TimeSeriesCollection> collections = new HashMap<>();
    private HeaderPanel visiblePanel;
    private ThermostatChartPanel chartPanel;
    private Duration duration;
    private ActionNotifier<UserAction> userActionNotifier = new ActionNotifier<>(this);
    private static final Color WHITE = new Color(255,255,255,0);
    private static final Color BLACK = new Color(0,0,0,0);
    private static final float TRANSPARENT = 0.0f;
    private Map<String, JFreeChart> charts = new HashMap<>();

    public StapPanel() {
        super();
        initializePanel();

        new ComponentVisibilityNotifier().initialize(visiblePanel, notifier);
    }

    private void initializePanel() {
        visiblePanel = new HeaderPanel();
        visiblePanel.setHeader(translator.localize(LocaleResources.STAP));

        JPanel p = initializeChartsPanel();
        visiblePanel.setContent(p);
    }

    private JPanel initializeChartsPanel() {
        JPanel p = new JPanel();
        //JLabel h = new JLabel("Harambe");
        JPanel detailsPanel = new JPanel();
        duration = ThermostatChartPanel.DEFAULT_DATA_DISPLAY;
        chartPanel = new ThermostatChartPanel(duration);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        detailsPanel.setLayout(new BorderLayout());
        chartPanel.addPropertyChangeListener(ThermostatChartPanel.PROPERTY_VISIBLE_TIME_RANGE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                duration = (Duration) evt.getNewValue();
                userActionNotifier.fireAction(UserAction.USER_CHANGED_TIME_RANGE);
            }
        });
        detailsPanel.add(chartPanel, BorderLayout.CENTER);
        return detailsPanel;
    }

    private JFreeChart createChart(String name, TimeSeriesCollection collection) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                name,
                "",
                translator.localize(LocaleResources.STAP_Y_AXIS).getContents(),
                collection,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);

        chart.getPlot().setBackgroundPaint(WHITE);
        chart.getPlot().setBackgroundImageAlpha(TRANSPARENT);
        chart.getPlot().setOutlinePaint(BLACK);

        chart.getXYPlot().setDomainAxis(new DateAxis(translator.localize(LocaleResources.STAP_X_AXIS).getContents()));
        chart.getXYPlot().getRangeAxis().setLowerBound(0.0);

        charts.put(name, chart);

        return chart;
    }

    @Override
    public void addChart(String tag) {
        TimeSeriesCollection collection = new TimeSeriesCollection();

        collection.addSeries(new TimeSeries(translator.localize(LocaleResources.STAP_HITS).getContents()));

        collections.put(tag, collection);

        chartPanel.addChart(createChart(tag, collection));
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    public void addData(final String tag, final java.util.List<DiscreteTimeData<Double>[]> data) {
        final java.util.List<DiscreteTimeData<Double>[]> copy = new ArrayList<>(data);
        final TimeSeriesCollection collection = collections.get(tag);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (DiscreteTimeData<Double>[] d : copy) {
                    for (int j = 0; j < 3; j++) {
                        if (null == collection.getSeries(j).getDataItem(new Millisecond(new Date(d[j].getTimeInMillis())))) {
                            collection.getSeries(j).add(new Millisecond(new Date(d[j].getTimeInMillis())), d[j].getData());
                        }
                    }
                }
            }
        });
    }

    @Override
    public Component getUiComponent() {
        return visiblePanel;
    }

    private enum UserAction {
        USER_CHANGED_TIME_RANGE
    }
}
