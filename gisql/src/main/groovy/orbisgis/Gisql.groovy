package orbisgis

import org.h2gis.utilities.GeometryTableUtilities
import org.h2gis.utilities.TableLocation
import org.h2gis.utilities.dbtypes.DBTypes
import org.h2gis.utilities.dbtypes.DBUtils

import javax.sql.DataSource
import java.sql.*
import groovy.sql.*

/**
 * Utility script to extend groovy Sql with spatial methods
 *
 * @author Erwan Bocher (CNRS 2021)
 */

class Gisql extends Sql {

    DBTypes dbTypes

    Gisql(DataSource dataSource) {
        super(dataSource)
        this.dbTypes =  DBUtils.getDBType(dataSource.connection)
    }

    Gisql(Connection connection) {
        super(connection)
        this.dbTypes =  DBUtils.getDBType(connection)
    }

    Gisql(Sql parent) {
        super(parent)
        this.dbTypes =  DBUtils.getDBType(parent.connection)
    }

    /**
    * Return the SRID of the first geometry column
    * @param tableName
    */
    int getSRID(String tableName) {
        return GeometryTableUtilities.getSRID(getConnection(), TableLocation.parse(tableName, dbTypes))
    }

    /**
     * Return the SRID for a given geometry column
     * @param tableName
     * @param columnName
     */
    int getSRID(String tableName, String columnName) {
        return GeometryTableUtilities.getSRID(getConnection(), TableLocation.parse(tableName, dbTypes), columnName)
    }

   }
