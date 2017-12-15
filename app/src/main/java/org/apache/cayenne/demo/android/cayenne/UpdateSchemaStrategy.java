package org.apache.cayenne.demo.android.cayenne;

import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.access.dbsync.BaseSchemaUpdateStrategy;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dbsync.merge.DataMapMerger;
import org.apache.cayenne.dbsync.merge.factory.DefaultMergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.dbsync.merge.token.db.AbstractToDbToken;
import org.apache.cayenne.dbsync.merge.token.db.SetAllowNullToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetColumnTypeToDb;
import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.dbsync.reverse.dbimport.ExcludeTable;
import org.apache.cayenne.dbsync.reverse.dbload.DbLoader;
import org.apache.cayenne.dbsync.reverse.dbload.DbLoaderConfiguration;
import org.apache.cayenne.dbsync.reverse.dbload.DefaultDbLoaderDelegate;
import org.apache.cayenne.dbsync.reverse.filters.FiltersConfig;
import org.apache.cayenne.dbsync.reverse.filters.IncludeTableFilter;
import org.apache.cayenne.dbsync.reverse.filters.PatternFilter;
import org.apache.cayenne.dbsync.reverse.filters.TableFilter;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Simple migration strategy, tries to create full db if no tables found,
 * or launch built-in DataMapMerger utilities.
 *
 * Targeted only on SQLite DB.
 * Can be generalized, only thing is missing is proper MergerTokenFactory.
 */
public class UpdateSchemaStrategy extends BaseSchemaUpdateStrategy {

    @Override
    protected void processSchemaUpdate(DataNode dataNode) throws SQLException {
        // check and create full db if no yet
        if(checkCreateFull(dataNode)) {
            return;
        }

        // launch datamap merger
        FiltersConfig filters = getFiltersConfig();
        DbLoaderConfiguration config = new DbLoaderConfiguration();
        config.setFiltersConfig(filters);

        // DataMap based on DB state
        DataMap sourceDataMap = load(dataNode, config);
        if(sourceDataMap == null) {
            return;
        }
        // Actual working DataMap
        DataMap targetDataMap = dataNode.getDataMap("app");

        // Merge
        Collection<MergerToken> tokens = createTokens(filters, sourceDataMap, targetDataMap);
        Collection<String> sql = createSql(tokens, dataNode.getAdapter());
        executeSql(dataNode, sql);
    }

    private boolean checkCreateFull(DataNode dataNode) throws SQLException {
        Map<String, Boolean> nameTables = getNameTablesInDB(dataNode);
        Collection<DbEntity> entities = dataNode.getEntityResolver().getDbEntities();
        for (DbEntity entity : entities) {
            if (nameTables.get(entity.getName()) != null) {
                return false;
            }
        }

        generateFull(dataNode);
        return true;
    }

    private void generateFull(DataNode dataNode) {
        Collection<DataMap> map = dataNode.getDataMaps();
        for (DataMap aMap : map) {
            DbGenerator gen = new DbGenerator(dataNode.getAdapter(), aMap, dataNode.getJdbcEventLogger());
            gen.setShouldCreateTables(true);
            gen.setShouldDropTables(false);
            gen.setShouldCreateFKConstraints(true);
            gen.setShouldCreatePKSupport(true);
            gen.setShouldDropPKSupport(false);
            try {
                gen.runGenerator(dataNode.getDataSource());
            } catch (Exception e) {
                throw new CayenneRuntimeException(e);
            }
        }
    }

    private DataMap load(DataNode dataNode, DbLoaderConfiguration config) {
        try (Connection connection = dataNode.getDataSource().getConnection()) {
            return new DbLoader(dataNode.getAdapter(), connection, config,
                    new DefaultDbLoaderDelegate(),
                    new DefaultObjectNameGenerator()).load();
        } catch (SQLException ex) {
            Log.e("Cayenne", "Unable to find existing DB", ex);
            return null;
        }
    }

    @NonNull
    private Collection<MergerToken> createTokens(FiltersConfig filters, DataMap sourceDataMap, DataMap targetDataMap) {
        DefaultMergerTokenFactory tokenFactory = new DefaultMergerTokenFactory();
        return DataMapMerger.builder(tokenFactory)
                .filters(filters)
                .skipPKTokens(false)
                .skipRelationshipsTokens(false)
                .build()
                .createMergeTokens(targetDataMap, sourceDataMap);
    }

    @NonNull
    private Collection<String> createSql(Collection<MergerToken> tokens, DbAdapter adapter) {
        Collection<String> sqlResult = new ArrayList<>(tokens.size() + 2);

        for(MergerToken token : tokens) {
            if(testToken(token)) {
                sqlResult.addAll(((AbstractToDbToken)token).createSql(adapter));
            }
        }

        return sqlResult;
    }

    private void executeSql(DataNode dataNode, Collection<String> sqlCollection) throws SQLException {
        try (Connection connection = dataNode.getDataSource().getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(true);
            try {
                for(String sql : sqlCollection) {
                    try (Statement statement = connection.createStatement()) {
                        Log.i("Cayenne", "Executing: " + sql);
                        statement.execute(sql);
                    } catch (SQLException ex) {
                        Log.e("Cayenne", "Unable execute statement", ex);
                    }
                }
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }
    }

    /**
     * Filter out some tokens that will fail on sqlite
     */
    private boolean testToken(MergerToken token) {
        if (!(token instanceof AbstractToDbToken)) {
            return false;
        }

        if(token instanceof SetColumnTypeToDb) {
            return false;
        }

        if(token instanceof SetAllowNullToDb) {
            return false;
        }

        return true;
    }

    @NonNull
    private FiltersConfig getFiltersConfig() {
        TreeSet<IncludeTableFilter> includes = new TreeSet<>();
        includes.add(new IncludeTableFilter(null));
        TreeSet<Pattern> excludes = new TreeSet<>(PatternFilter.PATTERN_COMPARATOR);
        excludes.add(PatternFilter.pattern("AUTO_PK_SUPPORT"));

        return FiltersConfig.create(null, null,
                new TableFilter(includes, excludes), PatternFilter.INCLUDE_NOTHING);
    }

    protected Map<String, Boolean> getNameTablesInDB(DataNode dataNode) throws SQLException {
        String tableLabel = dataNode.getAdapter().tableTypeForTable();
        Map<String, Boolean> nameTables = new HashMap<>();
        try (Connection con = dataNode.getDataSource().getConnection()) {
            try (ResultSet rs = con.getMetaData().getTables(null, null, "%", new String[]{tableLabel})) {
                while (rs.next()) {
                    String name = rs.getString("TABLE_NAME");
                    nameTables.put(name, false);
                }
            }
        }

        return nameTables;
    }
}
