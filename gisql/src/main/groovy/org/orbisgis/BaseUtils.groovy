package org.orbisgis

import org.h2.util.ScriptReader
import org.h2gis.utilities.FileUtilities
import org.h2gis.utilities.GeometryTableUtilities
import org.h2gis.utilities.URIUtilities
import org.locationtech.jts.geom.Geometry

import java.sql.*
import groovy.sql.*
import java.util.logging.Logger

/**
 * Utility script to extend groovy Sql with spatial methods
 *
 * @author Erwan Bocher (CNRS 2021)
 */

class BaseUtils {

    static final Logger LOG = Logger.getLogger(BaseUtils.class.getName());

    static String NO_GEOMETRY_COLUMN = "This table doesn't contain a Geometry column"

    /**
     * Return the connection of the SQL object
     * @param sql Groovy Sql object
     * @return the connection
     */
    static Connection getSQLConnection(Sql sql){
         def conn = sql.getConnection()
         if(conn==null){
             def ds = sql.dataSource
             if(ds){
                 conn = ds.connection
             }
         }
        return conn
    }

    /**
     * Drop the tables
     * @param tableName
     */
    static int dropTables(Sql sql, String... tableNames) {
        return sql.execute("drop table if exists ${tableNames.join(",")}")
    }

    /**
     * Return the number of rows
     * @param tableName
     */
    static int rowCount(Sql sql, String tableName) {
        return sql.firstRow("SELECT COUNT(*) as count FROM ${tableName}".toString()).count
    }

    /**
     * Return the column names of the input table
     * @param tableName
     */
    static List<String> columnNames(Sql sql, String tableName) {
        def columns =[]
        sql.rows("SELECT * from ${tableName} limit 1".toString(),
                { meta ->
                    (1..meta.columnCount).each {
                        columns << meta.getColumnLabel(it)
                    }})
        return columns
    }

    /**
     * Return the first geometry column
     * @param tableName
     */
    static String geometryColumn(Sql sql, String tableName) {
        sql.rows("SELECT * from ${tableName} limit 1".toString(),
                { meta ->
                    (1..meta.columnCount).each {
                        if(meta.getColumnTypeName(it).equalsIgnoreCase("geometry")){
                            return meta.getColumnLabel(it)
                        }
                    }})
    }

    /**
    * Return the SRID of the first geometry column
    * @param tableName
    */
    static int srid(Sql sql, String tableName) {
        return GeometryTableUtilities.getSRID(getSQLConnection(sql), tableName)
    }

    /**
     * Return the SRID for a given geometry column
     * @param tableName
     * @param columnName
     */
    static int srid(Sql sql, String tableName, String columnName) {
        return GeometryTableUtilities.getSRID(getSQLConnection(sql), tableName, columnName)
    }

    /**
     * Return the geometry column names
     * @param tableName
     */
    static List<String> geometryColumnNames(Sql sql, String tableName) {
        return GeometryTableUtilities.getGeometryColumnNames(getSQLConnection(sql), tableName)
    }

    /**
     * Return the Envelope of the first geometry
     * @param tableName
     */
    static Geometry envelope(Sql sql, String tableName,String filter) {
        return GeometryTableUtilities.getEnvelope(getSQLConnection(sql), tableName, [geometryColumn(sql, tableName)], filter)
    }

    /**
     * Return the full Envelope of table (merge all geometry envelopes)
     * @param the sql groovy object
     * @param tableName the name of table
     * @param filter a select from filter
     */
    static Geometry fullEnvelope(Sql sql, String tableName,String filter =null) {
        return GeometryTableUtilities.getEnvelope(getSQLConnection(sql), tableName, geometryColumnNames(sql,tableName), filter)
    }

    /**
     * Return the Envelope of the table based on all geometry columns
     * @param the sql groovy object
     * @param tableName the name of table
     * @param filter a select from filter
     */
    static Geometry envelope(Sql sql, String tableName, String geometryColumn, String filter) {
        return GeometryTableUtilities.getEnvelope(getSQLConnection(sql), tableName, [geometryColumn],filter)
    }

    /**
     * Return an estimated extent of the table
     * Faster than envelope but less accurate than getEnvelope
     * @param tableName
     */
    static Geometry estimatedExtent(Sql sql, String tableName) {
        return GeometryTableUtilities.getEstimatedExtent(getSQLConnection(sql), tableName)
    }


    /**
     * Alter the table with a new epsg code (SRID)
     * @param tableName
     */
    static boolean alterSRID(Sql sql, String tableName, int srid) {
        def geomCol = geometryColumn(sql, tableName)
        if(geomCol) {
            return GeometryTableUtilities.alterSRID(getSQLConnection(sql), tableName, geomCol, srid)
        }else{
            LOG.warning(NO_GEOMETRY_COLUMN)
            return false
        }
    }

    /**
     * This method is used to execute a SQL file
     *
     * @param fileName The sql file
     * @return True if the script has been successfully run, false otherwise.
     */
    static boolean executeScript(Sql sql, String fileName) throws Exception{
        if(fileName){
            File file = URIUtilities.fileFromString(fileName);
            boolean b = false;
            try {
                if (FileUtilities.isExtensionWellFormated(file, "sql")) {
                    ScriptReader scriptReader = new ScriptReader(new InputStreamReader(new FileInputStream(file)));
                    scriptReader.setSkipRemarks(true);
                    while (true) {
                        String commandSQL = scriptReader.readStatement()
                        if (commandSQL == null) {
                            break;
                        }
                        def cleanCommand =  commandSQL.trim();
                        if(cleanCommand){
                            try {
                                sql.execute(cleanCommand);
                                if(!getSQLConnection(sql).getAutoCommit()){
                                    getSQLConnection(sql).commit();
                                }
                            } catch (SQLException e) {
                                LOG.warning("Unable to execute the Sql command '" + commandSQL + "'.\n" + e.getLocalizedMessage());
                                return false;
                            }
                        }

                    }
                    return true;
                }
            } catch (IOException | SQLException e) {
                LOG.warning("Unable to read the SQL file.", e.getLocalizedMessage());
                try {
                    if(!getConnection(sql).getAutoCommit()){
                        getConnection(sql).rollback();
                    }
                } catch (SQLException e2) {
                    LOG.warning("Unable to rollback.", e2.getLocalizedMessage());
                }
            }
            return b;
        }
    }

    /**
     * Wrap a table name or a query to a groovy SQL method
     * @param tableName
     */
    static SQLWrapper getTable(Sql sql, String tableName, Map options = null){
        if (!tableName) {
            return
        }
        def filter, columns
        if (options) {
            filter = options.get("filter", null)
            columns = options.get("columns", null)
        }
        return new SQLWrapper(sql, "SELECT ${columns?columns.join(","):"*"} from ${tableName} ${filter ? filter : ""}".toString())
    }

   }
