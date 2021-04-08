package orbisgis

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groovyjarjarantlr4.v4.runtime.misc.Tuple2
import org.h2gis.utilities.GeometryTableUtilities
import org.h2gis.utilities.TableLocation
import org.h2gis.utilities.dbtypes.DBTypes
import org.h2gis.utilities.dbtypes.DBUtils

import java.sql.*
import groovy.sql.*
import groovy.transform.Field

/**
 * Utility script used as extension module adding methods to JDBCDataStore class.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

private static final @Field Map<Connection, Sql> SQLS = new HashMap<>()

private static final @Field Map<Connection, DBTypes> DBTYPES = new HashMap<>()

/**
 * Cache and retrieve  groovy SQL object
 * @param connection
 * @return @link Sql}
 */
private static Sql getSql(Connection connection) {
    if(!SQLS.containsKey(connection) || SQLS.get(connection).isClosed()) {
        SQLS.put(connection,  new Sql(connection))
    }
    return SQLS.get(connection)
}

/**
 * Cache and retrieve the DBType
 * @param connection
 * @return @link Sql}
 */
private static DBTypes getDBType(Connection connection) {
    if(!DBTYPES.containsKey(connection)) {
        DBTYPES.put(connection, DBUtils.getDBType(connection))
    }
    return DBTYPES.get(connection)
}


/**
 * Return the SRID of the first geometry column
 * @param sql
 * @param tableName
 */
static getSRID(Sql sql , String tableName){
    return GeometryTableUtilities.getSRID(sql.getConnection(), TableLocation.parse(tableName, getDBType(sql.getConnection())))
}

/**
 * Return the SRID of the first geometry
 * @param sql
 * @param tableName
 */
static getSRID(Connection connection , String tableName){
    return GeometryTableUtilities.getSRID(connection, TableLocation.parse(tableName,getDBType(connection)))
}


/**
 * Performs the given SQL query, which should return a single {@link java.sql.ResultSet} object. The given closure is called
 * with the {@link java.sql.ResultSet} as its argument.
 *
 * Example usages:
 *
 * sql.query("select * from PERSON where firstname like 'S%'") { ResultSet rs ->
 *     while (rs.next()) println rs.getString('firstname') + ' ' + rs.getString(3)
 * }
 *
 * sql.query("call get_people_places()") { ResultSet rs ->
 *     while (rs.next()) println rs.toRowResult().firstname
 * }
 *
 *
 * All resources including the ResultSet are closed automatically after the closure is called.
 *
 * @param ds      {@link Connection} on which the query is performed.
 * @param sql     The sql statement.
 * @param closure Called for each row with a {@link java.sql.ResultSet}.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static void query(Connection connection, String sql,
                  @ClosureParams(value = SimpleType, options = ["java.sql.ResultSet"]) Closure closure)
        throws SQLException {
    getSql(connection).query(sql, closure)
}