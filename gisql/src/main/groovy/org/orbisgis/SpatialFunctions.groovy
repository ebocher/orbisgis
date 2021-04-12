package org.orbisgis

import groovy.sql.Sql

import java.util.logging.Logger

class SpatialFunctions {

    static final Logger LOG = Logger.getLogger(SpatialFunctions.class.getName());


    /**
     * Build the SQL query to reproject a table name to another referenced coordinate system .
     * The reprojection is applied on the first geometry column.
     *
     * @param srid
     * @param sql
     * @param tableName
     * @param srid
     * @param filter
     * e.g  "where id >12"
     * @return
     */
    static SQLWrapper reproject(Sql sql, String tableName, int srid, String filter) {
        return reproject(sql, tableName, srid, [filter: filter])
    }
    /**
     * Build the SQL query to reproject a table name to another referenced coordinate system .
     * The reprojection is applied on the first geometry column.
     *
     * @param srid EPSG code as specified by the EPSG spatial reference system database.
     * @return The sql query to reproject the table
     *
     * @param sql
     * @param tableName
     * @param srid
     * @param options add columns selection or filter
     * e.g [columns:"the_geom, id", filter : "where id >12"]
     */
    static SQLWrapper reproject(Sql sql, String tableName, int srid, Map options = null) {
        if (!tableName) {
            return
        }
        if (srid <= 0) {
            return
        }
        def fieldNames = []
        def noGeom = false
        def filter, columns
        if (options) {
            filter = options.get("filter", null)
            columns = options.get("columns", null)
        }
        sql.rows("SELECT ${columns ? columns : "*"} from ${tableName} limit 1".toString(),
                { meta ->
                    (1..meta.columnCount).each {
                        def columnTypeName = meta.getColumnTypeName(it).toLowerCase()
                        String columnName = meta.getColumnName(it)
                        if (columnTypeName in ["geometry", "geography"]) {
                            fieldNames << "ST_TRANSFORM($columnName ,  $srid ) AS  $columnName"
                            noGeom = true
                        } else {
                            fieldNames << columnName
                        }
                    }
                })
        if (!noGeom) {
            LOG.warning(BaseUtils.NO_GEOMETRY_COLUMN)
            return
        }
        return new SQLWrapper(sql, "SELECT ${fieldNames.join(",")} from ${tableName} ${filter ? filter : ""}".toString())
    }
}
