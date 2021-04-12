package org.orbisgis

import groovy.sql.Sql
import org.h2gis.functions.factory.H2GISDBFactory

import java.util.logging.Logger

class Gisql {

    static final Logger LOG = Logger.getLogger(BaseUtils.class.getName());

    static String NO_GEOMETRY_COLUMN = "This table doesn't contain a Geometry column"

    /**
     * Create an H2GIS database and return the groovy Sql object
     * @param dbPath
     * @param h2parameters
     * @return
     */
    static Sql createH2GISDatabase(String dbPath, String h2parameters=""){
        return new Sql(H2GISDBFactory.createSpatialDataBase(dbPath, true, h2parameters))
    }
}
