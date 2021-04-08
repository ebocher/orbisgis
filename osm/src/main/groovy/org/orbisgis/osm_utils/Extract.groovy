/*
 * Bundle OSM is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OSM is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OSM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OSM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OSM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.osm_utils

import groovy.sql.Sql
import groovy.transform.Field
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.orbisgis.osm_utils.utils.DataUtils
import org.orbisgis.osm_utils.utils.ExtractUtils
import org.orbisgis.osm_utils.utils.NominatimUtils
import org.orbisgis.osm_utils.utils.OverpassUtils
import org.h2gis.utilities.GeographyUtilities

import static org.orbisgis.osm_utils.overpass.OSMElement.*

/** Default SRID value. */
@Field DEFAULT_SRID = 4326

/**
 * This process extracts OSM data file and load it in a database using an area
 * The area must be a JTS envelope
 *
 * @param sql A SQL groovy object to manage the database used to load the OSM file. Must not be null.
 * @param filterArea Filtering area as envelope
 * @param distance to expand the envelope of the query box. Default is 0
 *
 * @return The name of the tables that contains the geometry representation of the extracted area (outputZoneTable) and
 * its envelope (outputZoneEnvelopeTable)
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Le Saux (UBS LAB-STICC)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
def fromArea(Sql sql, def filterArea, int distance = 0) {
    //Check the mandatory parameters
    if(!sql) {
        error("No Connection provided.")
        return
    }
    if (!filterArea) {
        error("Filter area not defined.")
        return
    }
    if(distance < 0) {
        error("Negative distance.")
        return
    }

    def outputZoneTable = "ZONE".postfix()
    def outputZoneEnvelopeTable = "ZONE_ENVELOPE".postfix()
    def osmTablesPrefix = "OSM_DATA".postfix()
    def geom
    def factory =new GeometryFactory()
    if (filterArea instanceof Envelope) {
        geom = factory.toGeometry(filterArea)
    } else if (filterArea instanceof Polygon) {
        geom = filterArea
    } else {
        error "The filter area must be an Envelope or a Polygon"
        return
    }
    def env = geom.getEnvelopeInternal().expandEnvelopeByMeters(distance)

    //Create table to store the geometry and the envelope of the extracted area
    sql.execute("""
        CREATE TABLE $outputZoneTable (the_geom GEOMETRY(POLYGON, $DEFAULT_SRID));
        INSERT INTO $outputZoneTable VALUES (ST_GEOMFROMTEXT('${geom}', $DEFAULT_SRID));
    """.toString())

    def geomEnv = factory.toGeometry(env)

    sql.execute("CREATE TABLE $outputZoneEnvelopeTable (the_geom GEOMETRY(POLYGON, $DEFAULT_SRID));" +
            "INSERT INTO $outputZoneEnvelopeTable VALUES " +
            "(ST_GEOMFROMTEXT('$geomEnv',$DEFAULT_SRID));".toString())

    def osmFilePath = OverpassUtils.extract(OverpassUtils.buildOSMQuery(geomEnv, NODE, WAY, RELATION))
    if (osmFilePath) {
        info "Downloading OSM data from the area $filterArea"
        if (DataUtils.load(sql, osmTablesPrefix, osmFilePath)) {
            info "Loading OSM data from the area $filterArea"
            return [zoneTableName        : outputZoneTable,
                    zoneEnvelopeTableName: outputZoneEnvelopeTable,
                    osmTablesPrefix      : osmTablesPrefix,
                    epsg                 : DEFAULT_SRID]
        } else {
            error "Cannot load the OSM data from the area $filterArea"
        }
    } else {
        error "Cannot download OSM data from the area $filterArea"
    }
}

/**
 * This process extracts OSM data file and load it in a database using a place name
 *
 * @param sql A connexion to a DB to load the OSM file
 * @param placeName The name of the place to extract
 * @param distance to expand the envelope of the query box. Default is 0
 *
 * @return The name of the tables that contains the geometry representation of the extracted area (outputZoneTable) and
 * its envelope extended or not by a distance (outputZoneEnvelopeTable)
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Le Saux (UBS LAB-STICC)
 */
def fromPlace(Sql sql, String placeName, int distance = 0) {
    if(!placeName) {
        error("Cannot find an area from a void place name.")
        return
    }
    if(!sql) {
        error("No Connection provided.")
        return
    }

    def formattedPlaceName = placeName.trim().replaceAll("([\\s,\\-])+", "_")
    def outputZoneTable         = formattedPlaceName.prefix("ZONE").postfix()
    def outputZoneEnvelopeTable = formattedPlaceName.prefix("ZONE_ENVELOPE").postfix()
    def osmTablesPrefix         = formattedPlaceName.prefix("OSM_DATA").postfix()

    def geom = NominatimUtils.getArea(placeName)
    if (!geom) {
        error("Cannot find an area from the place name $placeName")
        return
    }
    if (distance < 0) {
        error("Cannot use a negative distance")
        return
    }
    def env = GeographyUtilities.expandEnvelopeByMeters(geom.getEnvelopeInternal(), distance)

    //Create table to store the geometry and the envelope of the extracted area
   sql.execute """
        CREATE TABLE ${outputZoneTable} (the_geom GEOMETRY(POLYGON, $DEFAULT_SRID), ID_ZONE VARCHAR);
        INSERT INTO ${outputZoneTable} VALUES (ST_GEOMFROMTEXT('${geom}', $DEFAULT_SRID), '$placeName');
    """.toString()

    def geomEnv = new GeometryFactory().toGeometry(env)
    sql.execute """
        CREATE TABLE $outputZoneEnvelopeTable (the_geom GEOMETRY(POLYGON, $DEFAULT_SRID), ID_ZONE VARCHAR);
        INSERT INTO $outputZoneEnvelopeTable VALUES (ST_GEOMFROMTEXT('$geomEnv',$DEFAULT_SRID), '$placeName');
    """.toString()

    def osmPath = OverpassUtils.extract(OverpassUtils.buildOSMQuery(geomEnv, NODE, WAY, RELATION))
    if (osmPath) {
        info "Downloading OSM data from the place $placeName"
        if (DataUtils.load(sql, osmTablesPrefix, osmPath)) {
            info "Loading OSM data from the place $placeName"
            return [zoneTableName        : outputZoneTable,
                    zoneEnvelopeTableName: outputZoneEnvelopeTable,
                    osmTablesPrefix      : osmTablesPrefix]
        } else {
            error "Cannot load the OSM data from the place $placeName"
        }

    } else {
        error "Cannot download OSM data from the place $placeName"
    }
}

/**
 * This process is used to extract ways as polygons
 *
 * @param sql a connection to a database
 * @param osmTablesPrefix prefix name for OSM tables
 * @param epsgCode EPSG code to reproject the geometries
 * @param tags list of keys and values to be filtered
 * @param columnsToKeep a list of columns to keep.
 * The name of a column corresponds to a key name
 *
 * @return outputTableName a name for the table that contains all ways transformed as polygons
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Le Saux (UBS LAB-STICC)
 */
def waysAsPolygons(Sql sql, String osmTablesPrefix, int epsgCode = 4326, def tags, def columnsToKeep) {
    if (!sql) {
        error "Invalid database connection"
        return
    }
    if (!osmTablesPrefix) {
        error "Invalid OSM tables name prefix."
        return
    }
    if(epsgCode < 0) {
        error "Invalid EPSG code."
        return
    }

    def outputTableName = "WAYS_POLYGONS".postfix()
    def idWaysPolygons = "ID_WAYS_POLYGONS".prefix(osmTablesPrefix)
    def osmTableTag = "way_tag".prefix(osmTablesPrefix)
    def countTagsQuery = ExtractUtils.getCountTagsQuery(osmTableTag, tags)
    def columnsSelector = ExtractUtils.getColumnSelectorQuery(osmTableTag, tags, columnsToKeep)
    def tagsFilter = ExtractUtils.createWhereFilter(tags)

    if (sql.firstRow(countTagsQuery.toString()).count <= 0) {
        warn "No keys or values found to extract ways. An empty table will be returned."
        sql.execute(""" 
            DROP TABLE IF EXISTS $outputTableName;
            CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
        """.toString())
        return outputTableName
    }

    debug "Build way polygons"
    def waysPolygonTmp = "WAYS_POLYGONS_TMP".postfix()
    sql.execute("DROP TABLE IF EXISTS $waysPolygonTmp;".toString())

    if (tagsFilter) {
        sql.execute("""
            DROP TABLE IF EXISTS $idWaysPolygons;
            CREATE TABLE $idWaysPolygons AS
                SELECT DISTINCT id_way
                FROM $osmTableTag
                WHERE $tagsFilter;
            CREATE INDEX ON $idWaysPolygons(id_way);
        """.toString())
    } else {
        sql.execute("""
            DROP TABLE IF EXISTS $idWaysPolygons;
            CREATE TABLE $idWaysPolygons AS
                SELECT DISTINCT id_way
                FROM $osmTableTag;
            CREATE INDEX ON $idWaysPolygons(id_way);
        """.toString())
    }

    if (columnsToKeep) {
        if (sql.firstRow("""
            SELECT count(*) AS count 
            FROM $idWaysPolygons AS a, ${osmTablesPrefix}_WAY_TAG AS b 
            WHERE a.ID_WAY = b.ID_WAY AND b.TAG_KEY IN ('${columnsToKeep.join("','")}')
        """.toString())[0] < 1) {
            debug "Any columns to keep. Cannot create any geometry polygons. An empty table will be returned."
            sql.execute("""
                DROP TABLE IF EXISTS $outputTableName;
                CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
            """.toString())
            return outputTableName
        }
    }

    sql.execute("""
        CREATE TABLE $waysPolygonTmp AS
            SELECT ST_TRANSFORM(ST_SETSRID(ST_MAKEPOLYGON(ST_MAKELINE(the_geom)), 4326), $epsgCode) AS the_geom, id_way
            FROM(
                SELECT(
                    SELECT ST_ACCUM(the_geom) AS the_geom
                    FROM(
                        SELECT n.id_node, n.the_geom, wn.id_way idway
                        FROM ${osmTablesPrefix}_node n, ${osmTablesPrefix}_way_node wn
                        WHERE n.id_node = wn.id_node
                        ORDER BY wn.node_order)
                    WHERE  idway = w.id_way
                ) the_geom ,w.id_way  
                FROM ${osmTablesPrefix}_way w, $idWaysPolygons b
                WHERE w.id_way = b.id_way
            ) geom_table
            WHERE ST_GEOMETRYN(the_geom, 1) = ST_GEOMETRYN(the_geom, ST_NUMGEOMETRIES(the_geom)) 
            AND ST_NUMGEOMETRIES(the_geom) > 3;
        CREATE INDEX ON $waysPolygonTmp(id_way);
    """.toString())

    sql.execute("""
        DROP TABLE IF EXISTS $outputTableName; 
        CREATE TABLE $outputTableName AS 
            SELECT 'w'||a.id_way AS id, a.the_geom ${ExtractUtils.createTagList(sql, columnsSelector)} 
            FROM $waysPolygonTmp AS a, $osmTableTag b
            WHERE a.id_way=b.id_way
            GROUP BY a.id_way;
    """.toString())

    sql.execute("""
        DROP TABLE IF EXISTS $waysPolygonTmp;
        DROP TABLE IF EXISTS $idWaysPolygons;
    """.toString())

    outputTableName
}

/**
 * This process is used to extract relations as polygons
 *
 * @param sql a connection to a database
 * @param osmTablesPrefix prefix name for OSM tables
 * @param epsgCode EPSG code to reproject the geometries
 * @param tags list of keys and values to be filtered
 * @param columnsToKeep a list of columns to keep.
 * The name of a column corresponds to a key name
 *
 * @return outputTableName a name for the table that contains all relations transformed as polygons
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Le Saux (UBS LAB-STICC)
 */
def relationsAsPolygons(Sql sql, String osmTablesPrefix, int epsgCode = 4326, def tags, def columnsToKeep) {
    if (!sql) {
        error "Invalid database connection"
        return
    }
    if (!osmTablesPrefix) {
        error "Invalid OSM tables name prefix."
        return
    }
    if(epsgCode < 0) {
        error "Invalid EPSG code."
        return
    }

    def outputTableName = "RELATION_POLYGONS".postfix()
    def osmTableTag = "relation_tag".prefix(osmTablesPrefix)
    def countTagsQuery = ExtractUtils.getCountTagsQuery(osmTableTag, tags)
    def columnsSelector = ExtractUtils.getColumnSelectorQuery(osmTableTag, tags, columnsToKeep)
    def tagsFilter = ExtractUtils.createWhereFilter(tags)

    if (sql.firstRow(countTagsQuery.toString()).count <= 0) {
        warn "No keys or values found in the relations. An empty table will be returned."
        sql.execute("""
            DROP TABLE IF EXISTS $outputTableName;
            CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
        """.toString())
        return outputTableName
    }
    debug "Build outer polygons"
    def relationsPolygonsOuter = "RELATIONS_POLYGONS_OUTER".postfix()
    def relationFilteredKeys = "RELATION_FILTERED_KEYS".postfix()
    def outer_condition
    def inner_condition
    if (!tagsFilter) {
        outer_condition = "WHERE w.id_way = br.id_way AND br.role='outer'"
        inner_condition = "WHERE w.id_way = br.id_way AND br.role='inner'"
    } else {
        sql.execute("""
            DROP TABLE IF EXISTS $relationFilteredKeys;
            CREATE TABLE $relationFilteredKeys AS 
                SELECT DISTINCT id_relation
                FROM ${osmTablesPrefix}_relation_tag wt 
                WHERE $tagsFilter;
            CREATE INDEX ON $relationFilteredKeys(id_relation);
        """.toString())

        if (columnsToKeep) {
            if (sql.firstRow("""
                SELECT count(*) AS count 
                FROM $relationFilteredKeys AS a, ${osmTablesPrefix}_RELATION_TAG AS b 
                WHERE a.ID_RELATION = b.ID_RELATION AND b.TAG_KEY IN ('${columnsToKeep.join("','")}')
            """.toString())[0] < 1) {
                debug "Any columns to keep. Cannot create any geometry polygons. An empty table will be returned."
                sql.execute("""
                    DROP TABLE IF EXISTS $outputTableName;
                    CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
                """.toString())
                return outputTableName
            }
        }

        outer_condition = """, $relationFilteredKeys g 
            WHERE br.id_relation=g.id_relation
            AND w.id_way = br.id_way
            AND br.role='outer'
        """
        inner_condition = """, $relationFilteredKeys g
            WHERE br.id_relation=g.id_relation
            AND w.id_way = br.id_way
            AND br.role='inner'
        """
    }

    sql.execute("""
        DROP TABLE IF EXISTS $relationsPolygonsOuter;
        CREATE TABLE $relationsPolygonsOuter AS 
        SELECT ST_LINEMERGE(ST_ACCUM(the_geom)) as the_geom, id_relation 
        FROM(
            SELECT ST_TRANSFORM(ST_SETSRID(ST_MAKELINE(the_geom), 4326), $epsgCode) AS  the_geom, id_relation, role, id_way 
            FROM(
                SELECT(
                    SELECT ST_ACCUM(the_geom) the_geom 
                    FROM(
                        SELECT n.id_node, n.the_geom, wn.id_way idway 
                        FROM ${osmTablesPrefix}_node n, ${osmTablesPrefix}_way_node wn 
                        WHERE n.id_node = wn.id_node ORDER BY wn.node_order) 
                    WHERE  idway = w.id_way) the_geom, w.id_way, br.id_relation, br.role 
                FROM ${osmTablesPrefix}_way w, ${osmTablesPrefix}_way_member br $outer_condition) geom_table
                WHERE st_numgeometries(the_geom)>=2) 
        GROUP BY id_relation;
    """.toString())

    debug "Build inner polygons"
    def relationsPolygonsInner = "RELATIONS_POLYGONS_INNER".postfix()
    sql.execute("""
        DROP TABLE IF EXISTS $relationsPolygonsInner;
        CREATE TABLE $relationsPolygonsInner AS 
        SELECT ST_LINEMERGE(ST_ACCUM(the_geom)) the_geom, id_relation 
        FROM(
            SELECT ST_TRANSFORM(ST_SETSRID(ST_MAKELINE(the_geom), 4326), $epsgCode) AS the_geom, id_relation, role, id_way 
            FROM(     
                SELECT(
                    SELECT ST_ACCUM(the_geom) the_geom 
                    FROM(
                        SELECT n.id_node, n.the_geom, wn.id_way idway 
                        FROM ${osmTablesPrefix}_node n, ${osmTablesPrefix}_way_node wn 
                        WHERE n.id_node = wn.id_node ORDER BY wn.node_order) 
                    WHERE  idway = w.id_way) the_geom, w.id_way, br.id_relation, br.role 
                FROM ${osmTablesPrefix}_way w, ${osmTablesPrefix}_way_member br ${inner_condition}) geom_table 
                WHERE st_numgeometries(the_geom)>=2) 
        GROUP BY id_relation;
    """.toString())

    debug "Explode outer polygons"
    def relationsPolygonsOuterExploded = "RELATIONS_POLYGONS_OUTER_EXPLODED".postfix()
    sql.execute("""
        DROP TABLE IF EXISTS $relationsPolygonsOuterExploded;
        CREATE TABLE $relationsPolygonsOuterExploded AS 
            SELECT ST_MAKEPOLYGON(the_geom) AS the_geom, id_relation 
            FROM st_explode('$relationsPolygonsOuter') 
            WHERE ST_STARTPOINT(the_geom) = ST_ENDPOINT(the_geom)
            AND ST_NPoints(the_geom)>=4;
    """.toString())

    debug "Explode inner polygons"
    def relationsPolygonsInnerExploded = "RELATIONS_POLYGONS_INNER_EXPLODED".postfix()
    sql.execute("""
        DROP TABLE IF EXISTS $relationsPolygonsInnerExploded;
        CREATE TABLE $relationsPolygonsInnerExploded AS 
            SELECT the_geom AS the_geom, id_relation 
            FROM st_explode('$relationsPolygonsInner') 
            WHERE ST_STARTPOINT(the_geom) = ST_ENDPOINT(the_geom)
            AND ST_NPoints(the_geom)>=4; 
    """.toString())

    debug "Build all polygon relations"
    def relationsMpHoles = "RELATIONS_MP_HOLES".postfix()
    sql.execute("""
        CREATE INDEX ON $relationsPolygonsOuterExploded USING RTREE (the_geom);
        CREATE INDEX ON $relationsPolygonsInnerExploded USING RTREE (the_geom);
        CREATE INDEX ON $relationsPolygonsOuterExploded(id_relation);
        CREATE INDEX ON $relationsPolygonsInnerExploded(id_relation);       
        DROP TABLE IF EXISTS $relationsMpHoles;
        CREATE TABLE $relationsMpHoles AS (
            SELECT ST_MAKEPOLYGON(ST_EXTERIORRING(a.the_geom), ST_ACCUM(b.the_geom)) AS the_geom, a.ID_RELATION
            FROM $relationsPolygonsOuterExploded AS a 
            LEFT JOIN $relationsPolygonsInnerExploded AS b 
            ON(
                a.the_geom && b.the_geom 
                AND st_contains(a.the_geom, b.THE_GEOM) 
                AND a.ID_RELATION=b.ID_RELATION)
            GROUP BY a.the_geom, a.id_relation)
        UNION(
            SELECT a.the_geom, a.ID_RELATION 
            FROM $relationsPolygonsOuterExploded AS a 
            LEFT JOIN  $relationsPolygonsInnerExploded AS b 
            ON a.id_relation=b.id_relation 
            WHERE b.id_relation IS NULL);
        CREATE INDEX ON $relationsMpHoles(id_relation);
    """.toString())

    sql.execute("""
        DROP TABLE IF EXISTS $outputTableName;     
        CREATE TABLE $outputTableName AS 
            SELECT 'r'||a.id_relation AS id, a.the_geom ${ExtractUtils.createTagList(sql, columnsSelector)}
            FROM $relationsMpHoles AS a, ${osmTablesPrefix}_relation_tag  b 
            WHERE a.id_relation=b.id_relation 
            GROUP BY a.the_geom, a.id_relation;
    """.toString())

    sql.execute("""
        DROP TABLE IF EXISTS    $relationsPolygonsOuter, 
                                $relationsPolygonsInner,
                                $relationsPolygonsOuterExploded, 
                                $relationsPolygonsInnerExploded, 
                                $relationsMpHoles, 
                                $relationFilteredKeys;
    """.toString())
    outputTableName
}

/**
 * This process is used to extract ways as lines
 *
 * @param sql a connection to a database
 * @param osmTablesPrefix prefix name for OSM tables
 * @param epsgCode EPSG code to reproject the geometries
 * @param tags list of keys and values to be filtered
 * @param columnsToKeep a list of columns to keep.
 * The name of a column corresponds to a key name
 *
 * @return outputTableName a name for the table that contains all ways transformed as lines
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 */
def waysAsLines(Sql sql, String osmTablesPrefix, int epsgCode = 4326, def tags, def columnsToKeep) {
    if (!sql) {
        error "Invalid database connection"
        return
    }
    if (!osmTablesPrefix) {
        error "Invalid OSM tables name prefix."
        return
    }
    if(epsgCode < 0) {
        error "Invalid EPSG code."
        return
    }

    def outputTableName = "WAYS_LINES".postfix()
    def idWaysTable = "ID_WAYS".postfix()
    def osmTableTag = "way_tag".prefix(osmTablesPrefix)
    def countTagsQuery = ExtractUtils.getCountTagsQuery(osmTableTag, tags)
    def columnsSelector = ExtractUtils.getColumnSelectorQuery(osmTableTag, tags, columnsToKeep)
    def tagsFilter = ExtractUtils.createWhereFilter(tags)

    if (sql.firstRow(countTagsQuery.toString()).count <= 0) {
        debug "No keys or values found in the ways. An empty table will be returned."
        sql.execute(""" 
            DROP TABLE IF EXISTS $outputTableName;
            CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
        """.toString())
        return outputTableName
    }
    debug "Build ways as lines"
    def waysLinesTmp = "WAYS_LINES_TMP".postfix()

    if (!tagsFilter) {
        idWaysTable = "way_tag".prefix(osmTablesPrefix)
    } else {
        sql.execute("""
            DROP TABLE IF EXISTS $idWaysTable;
            CREATE TABLE $idWaysTable AS
                SELECT DISTINCT id_way
                FROM ${osmTablesPrefix}_way_tag
                WHERE $tagsFilter;
            CREATE INDEX ON $idWaysTable(id_way);
        """.toString())
    }

    if (columnsToKeep) {
        if (sql.firstRow("""
            SELECT count(*) AS count 
            FROM $idWaysTable AS a, ${osmTablesPrefix}_WAY_TAG AS b 
            WHERE a.ID_WAY = b.ID_WAY AND b.TAG_KEY IN ('${columnsToKeep.join("','")}')
        """.toString())[0] < 1) {
            debug "Any columns to keep. Cannot create any geometry lines. An empty table will be returned."
            sql.execute("""
                DROP TABLE IF EXISTS $outputTableName;
                CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
            """.toString())
            outputTableName
        }
    }


    sql.execute("""
        DROP TABLE IF EXISTS $waysLinesTmp; 
        CREATE TABLE  $waysLinesTmp AS 
            SELECT id_way,ST_TRANSFORM(ST_SETSRID(ST_MAKELINE(THE_GEOM), 4326), $epsgCode) the_geom 
            FROM(
                SELECT(
                    SELECT ST_ACCUM(the_geom) the_geom 
                    FROM(
                        SELECT n.id_node, n.the_geom, wn.id_way idway 
                        FROM ${osmTablesPrefix}_node n, ${osmTablesPrefix}_way_node wn 
                        WHERE n.id_node = wn.id_node
                        ORDER BY wn.node_order) 
                    WHERE idway = w.id_way
                ) the_geom, w.id_way  
                FROM ${osmTablesPrefix}_way w, $idWaysTable b 
                WHERE w.id_way = b.id_way) geom_table 
            WHERE ST_NUMGEOMETRIES(the_geom) >= 2;
        CREATE INDEX ON $waysLinesTmp(ID_WAY);
    """.toString())

    sql.execute("""
        DROP TABLE IF EXISTS $outputTableName;
        CREATE TABLE $outputTableName AS 
            SELECT 'w'||a.id_way AS id, a.the_geom ${ExtractUtils.createTagList(sql, columnsSelector)} 
            FROM $waysLinesTmp AS a, ${osmTablesPrefix}_way_tag b 
            WHERE a.id_way=b.id_way 
            GROUP BY a.id_way;
        DROP TABLE IF EXISTS $waysLinesTmp, $idWaysTable;
    """.toString())
    outputTableName
}

/**
 * This process is used to extract relations as lines
 *
 * @param sql a connection to a database
 * @param osmTablesPrefix prefix name for OSM tables
 * @param epsgCode EPSG code to reproject the geometries
 * @param tags list of keys and values to be filtered
 * @param columnsToKeep a list of columns to keep.
 * The name of a column corresponds to a key name
 *
 * @return outputTableName a name for the table that contains all relations transformed as lines
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 */
def relationsAsLines(Sql sql, String osmTablesPrefix, int epsgCode = 4326, def tags, def columnsToKeep) {
    if (!sql) {
        error "Invalid database connection"
        return
    }
    if (!osmTablesPrefix) {
        error "Invalid OSM tables name prefix."
        return
    }
    if(epsgCode < 0) {
        error "Invalid EPSG code."
        return
    }

    def outputTableName = "RELATIONS_LINES".postfix()
    def osmTableTag = "relation_tag".prefix(osmTablesPrefix)
    def countTagsQuery = ExtractUtils.getCountTagsQuery(osmTableTag, tags)
    def columnsSelector = ExtractUtils.getColumnSelectorQuery(osmTableTag, tags, columnsToKeep)
    def tagsFilter = ExtractUtils.createWhereFilter(tags)

    if (sql.firstRow(countTagsQuery.toString()).count <= 0) {
        warn "No keys or values found in the relations. An empty table will be returned."
        sql.execute("""
            DROP TABLE IF EXISTS $outputTableName;
            CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
        """.toString())
        return outputTableName
    }
    def relationsLinesTmp = "RELATIONS_LINES_TMP".postfix()
    def relationsFilteredKeys = "RELATION_FILTERED_KEYS".postfix()

    if (!tagsFilter) {
        relationsFilteredKeys = "relation".prefix(osmTablesPrefix)
    } else {
        sql.execute("""
            DROP TABLE IF EXISTS $relationsFilteredKeys;
            CREATE TABLE $relationsFilteredKeys AS
                SELECT DISTINCT id_relation
                FROM ${osmTablesPrefix}_relation_tag wt
                WHERE $tagsFilter;
            CREATE INDEX ON $relationsFilteredKeys(id_relation);
        """.toString())
    }

    if (columnsToKeep) {
        if (sql.firstRow("""
            SELECT count(*) AS count 
            FROM $relationsFilteredKeys AS a, ${osmTablesPrefix}_RELATION_TAG AS b 
            WHERE a.ID_RELATION = b.ID_RELATION AND b.TAG_KEY IN ('${columnsToKeep.join("','")}')
        """.toString())[0] < 1) {
            debug "Any columns to keep. Cannot create any geometry lines. An empty table will be returned."
            sql.execute(""" 
                DROP TABLE IF EXISTS $outputTableName;
                CREATE TABLE $outputTableName (the_geom GEOMETRY(GEOMETRY,$epsgCode));
            """.toString())
            return outputTableName
        }
    }

    sql.execute("""
        DROP TABLE IF EXISTS $relationsLinesTmp;
        CREATE TABLE $relationsLinesTmp AS
            SELECT ST_ACCUM(THE_GEOM) AS the_geom, id_relation
            FROM(
                SELECT ST_TRANSFORM(ST_SETSRID(ST_MAKELINE(the_geom), 4326), $epsgCode) the_geom, id_relation, id_way
                FROM(
                    SELECT(
                        SELECT ST_ACCUM(the_geom) the_geom
                        FROM(
                            SELECT n.id_node, n.the_geom, wn.id_way idway
                            FROM ${osmTablesPrefix}_node n, ${osmTablesPrefix}_way_node wn
                            WHERE n.id_node = wn.id_node ORDER BY wn.node_order)
                        WHERE idway = w.id_way
                    ) the_geom, w.id_way, br.id_relation
                    FROM ${osmTablesPrefix}_way w, (
                        SELECT br.id_way, g.ID_RELATION
                        FROM ${osmTablesPrefix}_way_member br , $relationsFilteredKeys g
                        WHERE br.id_relation=g.id_relation
                    ) br
                    WHERE w.id_way = br.id_way
                ) geom_table
                WHERE st_numgeometries(the_geom)>=2)
            GROUP BY id_relation;
        CREATE INDEX ON $relationsLinesTmp(id_relation);
    """.toString())

    sql.execute("""
        DROP TABLE IF EXISTS $outputTableName;
        CREATE TABLE $outputTableName AS
            SELECT 'r'||a.id_relation AS id, a.the_geom ${ExtractUtils.createTagList(sql, columnsSelector)}
            FROM $relationsLinesTmp AS a, ${osmTablesPrefix}_relation_tag  b
            WHERE a.id_relation=b.id_relation
            GROUP BY a.id_relation;
        DROP TABLE IF EXISTS $relationsLinesTmp, $relationsFilteredKeys;
    """.toString())
    outputTableName
}

/**
 * This function is used to extract nodes as points
 *
 * @param sql a connection to a database
 * @param osmTablesPrefix prefix name for OSM tables
 * @param epsgCode EPSG code to reproject the geometries
 * @param outputNodesPoints the name of the nodes points table
 * @param tagKeys list ok keys to be filtered
 * @param columnsToKeep a list of columns to keep.
 * The name of a column corresponds to a key name
 *
 * @return true if some points have been built
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 */
def nodesAsPoints(Sql sql, String osmTablesPrefix, String outputNodesPoints,
                                    int epsgCode = 4326, def tags, def columnsToKeep) {
    if(!sql){
        error("Invalid database connection.")
        return
    }
    if(!osmTablesPrefix){
        error "Invalid OSM table prefix."
        return
    }
    if(!outputNodesPoints){
        error "Invalid output node points table name"
        return
    }
    if(epsgCode < 0){
        error "Invalid EPSG code"
        return
    }

    def tableNode = "node".prefix(osmTablesPrefix)
    def tableNodeTag = "node_tag".prefix(osmTablesPrefix)
    def countTagsQuery = ExtractUtils.getCountTagsQuery(tableNodeTag, tags)
    def columnsSelector = ExtractUtils.getColumnSelectorQuery(tableNodeTag, tags, columnsToKeep)
    def tagsFilter = ExtractUtils.createWhereFilter(tags)

    if (sql.firstRow(countTagsQuery.toString()).count <= 0) {
        debug "No keys or values found in the nodes. An empty table will be returned."
        sql.execute """
                DROP TABLE IF EXISTS $outputNodesPoints;
                CREATE TABLE $outputNodesPoints (the_geom GEOMETRY(POINT,4326));
        """.toString()
        return
    }
    debug "Build nodes as points"
    def tagList = ExtractUtils.createTagList sql, columnsSelector
    if (tagsFilter.isEmpty()) {
        if (columnsToKeep) {
            if (sql.firstRow("select count(*) as count from $tableNodeTag where TAG_KEY in ('${columnsToKeep.join("','")}')".toString())[0] < 1) {
                sql.execute """
                        DROP TABLE IF EXISTS $outputNodesPoints;
                        CREATE TABLE $outputNodesPoints (the_geom GEOMETRY(POINT,4326));
                """.toString()
                return outputNodesPoints
            }
        }
        sql.execute """
            DROP TABLE IF EXISTS $outputNodesPoints;
            CREATE TABLE $outputNodesPoints AS
                SELECT a.id_node,ST_TRANSFORM(ST_SETSRID(a.THE_GEOM, 4326), $epsgCode) AS the_geom $tagList
                FROM $tableNode AS a, $tableNodeTag b
                WHERE a.id_node = b.id_node GROUP BY a.id_node;
        """.toString()

    } else {
        if(columnsToKeep){
            if(sql.firstRow("select count(*) as count from $tableNodeTag where TAG_KEY in ('${columnsToKeep.join("','")}')".toString())[0]<1){
                sql.execute """
                        DROP TABLE IF EXISTS $outputNodesPoints;
                        CREATE TABLE $outputNodesPoints (the_geom GEOMETRY(POINT,4326));
                """.toString()
                return outputNodesPoints
            }
        }
        def filteredNodes = "FILTERED_NODES".postfix()
        sql.execute """
            CREATE TABLE $filteredNodes AS
                SELECT DISTINCT id_node FROM ${osmTablesPrefix}_node_tag WHERE $tagsFilter;
            CREATE INDEX ON $filteredNodes(id_node);
            DROP TABLE IF EXISTS $outputNodesPoints;
            CREATE TABLE $outputNodesPoints AS
                SELECT a.id_node, ST_TRANSFORM(ST_SETSRID(a.THE_GEOM, 4326), $epsgCode) AS the_geom $tagList
                FROM $tableNode AS a, $tableNodeTag  b, $filteredNodes c
                WHERE a.id_node=b.id_node
                AND a.id_node=c.id_node
                GROUP BY a.id_node;
        """.toString()
    }
    return outputNodesPoints
}
