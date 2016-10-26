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

package com.redhat.thermostat.stap.common.internal;

import com.redhat.thermostat.storage.core.Category;
import com.redhat.thermostat.storage.core.experimental.statement.*;
import com.redhat.thermostat.storage.dao.AbstractDaoQuery;
import com.redhat.thermostat.storage.dao.SimpleDaoQuery;
import com.redhat.thermostat.storage.model.AggregateCount;
import com.redhat.thermostat.storage.model.DistinctResult;
import com.redhat.thermostat.storage.model.Pojo;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.redhat.thermostat.common.utils.LoggingUtils;
import com.redhat.thermostat.stap.common.StapDAO;
import com.redhat.thermostat.stap.common.StapData;
import com.redhat.thermostat.storage.core.*;
import com.redhat.thermostat.storage.dao.AbstractDao;
import com.redhat.thermostat.storage.dao.AbstractDaoStatement;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Component
@Service(value = StapDAO.class)
public class StapDAOImpl extends AbstractDao implements StapDAO {

    Category<AggregateCount> aggregateCountCategory;

    static Key<String> KEY_STAP_METRIC = new Key<>("stapMetric");
    //static Key<String> SOURCE_IP = new Key<>("srcIP");
    //static Key<String> DEST_IP = new Key<>("destIP");

    static final Category<StapData> STAP_CATEGORY = new Category<StapData>(
            "stap-metrics",
            StapData.class,
            Key.TIMESTAMP, Key.AGENT_ID, Key.VM_ID,
            KEY_STAP_METRIC);

    static final String ADD = "ADD " + STAP_CATEGORY.getName()
            + " SET '" + Key.AGENT_ID.getName() + "' = ?s , "
            + "     '" + Key.VM_ID.getName() + "' = ?s , "
            + "     '" + Key.TIMESTAMP.getName() + "' = ?l , "
            + "     '" + KEY_STAP_METRIC.getName() + "' = ?s";

    static final String QUERY_COUNT_PACKETS_BY_SOURCE_IP = ""
            + "QUERY-COUNT " + STAP_CATEGORY.getName() + " "
            + "WHERE '" + Key.AGENT_ID.getName() + "' = ?s";

    static final String QUERY_ALL = "" +
            "QUERY-DISTINCT(" + Key.AGENT_ID.getName() +") " + STAP_CATEGORY.getName()
            + " WHERE '" + Key.ID.getName() + "' = ?s";

    //static final String QUERY_UNIQUE = ""
    //        + "QUERY DISTINCT" + STAP_CATEGORY.getName();
    private static final Logger logger = LoggingUtils.getLogger(StapDAOImpl.class);

    @Reference
    private Storage storage;

    private Category<DistinctResult> aggregateCategory;

    public StapDAOImpl() {

    }

    public StapDAOImpl(Storage storage) {
        this.storage = storage;
    }

    @Activate
    private void activate() {
        storage.registerCategory(STAP_CATEGORY);
        CategoryAdapter<StapData, AggregateCount> adapter = new CategoryAdapter<>(STAP_CATEGORY);
        CategoryAdapter<StapData, DistinctResult> adapter2 = new CategoryAdapter<>(STAP_CATEGORY);
        aggregateCategory = adapter2.getAdapted(DistinctResult.class);
        aggregateCountCategory = adapter.getAdapted(AggregateCount.class);
        storage.registerCategory(aggregateCountCategory);
    }

    @Override
    public List<StapData> getAll() {
        return executeQuery(new SimpleDaoQuery<>(storage, STAP_CATEGORY, QUERY_ALL)).asList();
    }

    @Override
    public void saveStapData(final StapData data) {
        executeStatement(new AbstractDaoStatement<StapData>(storage, STAP_CATEGORY, ADD) {
            @Override
            public PreparedStatement<StapData> customize(PreparedStatement<StapData> ps) {
                ps.setString(0, data.getAgentId());
                ps.setString(1, data.getVmId());
                ps.setLong(2, data.getTimeStamp());
                ps.setString(3, data.getStapMetric());
                return ps;
            }
        });
    }

    @Override
    public long getCount(final String source) {
        return executeQuery(new AbstractDaoQuery<AggregateCount>(storage, aggregateCountCategory, QUERY_COUNT_PACKETS_BY_SOURCE_IP) {
            @Override
            public PreparedStatement<AggregateCount> customize(PreparedStatement<AggregateCount> preparedStatement) {
                preparedStatement.setString(0, source);
                return preparedStatement;
            }
        }).head().getCount();
    }

    @Override
    public Set<String> getDistinctIPs() {
        DistinctResult dr = executeQuery(new AbstractDaoQuery<DistinctResult>(storage, aggregateCategory, QUERY_ALL) {
            @Override
            public PreparedStatement<DistinctResult> customize(PreparedStatement<DistinctResult> preparedStatement) {
                return preparedStatement;
            }
        }).head();
        Set<String> unique = new HashSet<>();
        if (dr != null) {
            Collections.addAll(unique, dr.getValues());
        }
        return unique;
    }

    @Override
    protected Logger getLogger() { return logger; }

}
