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
package org.orbisgis.osm_utils.utils

import groovy.sql.Sql
import org.h2gis.functions.factory.H2GISDBFactory
import org.h2gis.utilities.JDBCUtilities
import org.h2gis.utilities.TableLocation
import org.h2gis.utilities.dbtypes.DBTypes
import org.junit.jupiter.api.*
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.Polygon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection

import static org.h2gis.utilities.JDBCUtilities.isH2DataBase
import static org.junit.jupiter.api.Assertions.*

/**
 * Test class for the processes in {@link org.orbisgis.osm_utils.Transform}
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2019)
 */
class TransformUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformUtilsTest)
    private static Sql sql
    private static String DB_NAME

    @BeforeAll
    static void beforeAll() {
        DB_NAME = (this.simpleName.postfix()).toUpperCase()
        sql = new Sql(H2GISDBFactory.createSpatialDataBase("./target/" + DB_NAME))
    }

    /**
     * Test the {@link org.orbisgis.osm_utils.utils.TransformUtils#buildIndexes(Sql, java.lang.String)}
     * method with bad data.
     */
    @Test
    void badBuildIndexesTest(){
        def osmTable = "toto"

        LOGGER.warn("An error will be thrown next")
        assertFalse TransformUtils.buildIndexes(sql, null)
        LOGGER.warn("An error will be thrown next")
        assertFalse TransformUtils.buildIndexes(null, null)
        LOGGER.warn("An error will be thrown next")
        assertFalse TransformUtils.buildIndexes(null, osmTable)
    }

    /**
     * Test the {@link org.orbisgis.osm_utils.utils.TransformUtils#buildIndexes(Sql, java.lang.String)}
     * method.
     */
    @Test
    void buildIndexesTest(){
        def osmTablesPrefix = "toto"
        sql.execute """
            CREATE TABLE ${osmTablesPrefix}_node(id_node varchar);
            CREATE TABLE ${osmTablesPrefix}_way_node(id_node varchar, node_order varchar, id_way varchar);
            CREATE TABLE ${osmTablesPrefix}_way(id_way varchar, not_taken_into_account varchar);
            CREATE TABLE ${osmTablesPrefix}_way_tag(tag_key varchar,id_way varchar,tag_value varchar);
            CREATE TABLE ${osmTablesPrefix}_relation_tag(tag_key varchar,id_relation varchar,tag_value varchar);
            CREATE TABLE ${osmTablesPrefix}_relation(id_relation varchar);
            CREATE TABLE ${osmTablesPrefix}_way_member(id_relation varchar);
            CREATE TABLE ${osmTablesPrefix}_way_not_taken_into_account(id_way varchar);
            CREATE TABLE ${osmTablesPrefix}_relation_not_taken_into_account(id_relation varchar);
        """.toString()

        TransformUtils.buildIndexes(sql, osmTablesPrefix)
        def loc = TableLocation.parse("${osmTablesPrefix}_node", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc.toString()).contains("ID_NODE")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_NODE")

        loc = TableLocation.parse("${osmTablesPrefix}_way_node",DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_NODE")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_NODE")
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("NODE_ORDER")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "NODE_ORDER")
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_WAY")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_WAY")

        loc = TableLocation.parse("${osmTablesPrefix}_way", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_WAY")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_WAY")
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("NOT_TAKEN_INTO_ACCOUNT")
        assert !JDBCUtilities.isIndexed(sql.connection, loc, "NOT_TAKEN_INTO_ACCOUNT")

        loc = TableLocation.parse("${osmTablesPrefix}_way_tag", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("TAG_KEY")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "TAG_KEY")
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_WAY")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_WAY")
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("TAG_VALUE")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "TAG_VALUE")

        loc = TableLocation.parse("${osmTablesPrefix}_relation_tag", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("TAG_KEY")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "TAG_KEY")
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_RELATION")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_RELATION")
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("TAG_VALUE")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "TAG_VALUE")

        loc = TableLocation.parse("${osmTablesPrefix}_relation", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_RELATION")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_RELATION")

        loc = TableLocation.parse("${osmTablesPrefix}_way_member", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_RELATION")
        assert JDBCUtilities.isIndexed(sql.connection, loc, "ID_RELATION")

        loc = TableLocation.parse("${osmTablesPrefix}_way_not_taken_into_account", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_WAY")
        assert !JDBCUtilities.isIndexed(sql.connection, loc, "ID_WAY")

        loc = TableLocation.parse("${osmTablesPrefix}_relation_not_taken_into_account", DBTypes.H2)
        assert JDBCUtilities.tableExists(sql.connection, loc)
        assert JDBCUtilities.getColumnNames(sql.connection, loc).contains("ID_RELATION")
        assert !JDBCUtilities.isIndexed(sql.connection, loc, "ID_RELATION")
    }

    /**
     * test the {@link org.orbisgis.osm_utils.utils.TransformUtils#toPolygonOrLine(java.lang.String, Sql, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)}
     * method with bad data.
     */
    @Test
    void badToPolygonOrLineTest(){
        def lineType = TransformUtils.LINES
        def prefix = "OSM".postfix()
        def epsgCode = 2145
        def badEpsgCode = -1
        def tags = [:]
        def columnsToKeep = []

        assert !TransformUtils.toPolygonOrLine(null, sql, prefix, epsgCode, tags, columnsToKeep)
        assert !TransformUtils.toPolygonOrLine(lineType, null, prefix, epsgCode, tags, columnsToKeep)
        assert !TransformUtils.toPolygonOrLine(lineType, sql, null, epsgCode, tags, columnsToKeep)
        assert !TransformUtils.toPolygonOrLine(lineType, sql, prefix, badEpsgCode, tags, columnsToKeep)
        assert !TransformUtils.toPolygonOrLine(lineType, sql, prefix, null, tags, columnsToKeep)
        assert !TransformUtils.toPolygonOrLine(lineType, sql, prefix, epsgCode, null, null)
    }

    /**
     * test the {@link org.orbisgis.osm_utils.utils.TransformUtils#toPolygonOrLine(java.lang.String,Sql, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)}
     * method.
     */
    @Test
    void toPolygonOrLineTest() {
        def lineType = TransformUtils.LINES
        def polygonType = TransformUtils.POLYGONS
        def prefix = "OSM".postfix()
        def epsgCode = 2145
        def tags = ["building": ["house"]]
        def columnsToKeep = ["water"]

        //Load data
        createData(sql, prefix)

        //Test line
        def result = TransformUtils.toPolygonOrLine(lineType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result
        def loc = TableLocation.parse(result, DBTypes.H2)
        assert 2 == JDBCUtilities.getRowCount(sql.connection, loc)
        def rows = sql.rows("SELECT * FROM ${loc.toString()}".toString())
        rows.eachWithIndex { it, i ->
            switch(i){
                case 0:
                    assert "house" == it.building
                    assert "w1" == it.id
                    assert it.the_geom instanceof LineString
                    assert "lake" == it.water
                    break
                case 1:
                    assert "house" == it.building
                    assert "r1" == it.id
                    assert it.the_geom instanceof MultiLineString
                    assert "lake" == it.water
                    break
            }
        }

        //Test polygon
        result = TransformUtils.toPolygonOrLine(polygonType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result
        loc = TableLocation.parse(result, DBTypes.H2)
        assert 2 == JDBCUtilities.getRowCount(sql.connection, loc)
        rows = sql.rows("SELECT * FROM ${loc.toString()}".toString())
        rows.eachWithIndex { it, i ->
            switch(i){
                case 1:
                    assert "house", it.building
                    assert "w1", it.id
                    assert it.the_geom instanceof Polygon
                    assert "lake", it.water
                    break
                case 2:
                    assert "house", it.building
                    assert "r1", it.id
                    assert it.the_geom instanceof Polygon
                    assert "lake", it.water
                    break
            }
        }

        //Test no way tags
        sql.execute "DROP TABLE ${prefix}_way_tag".toString()
        sql.execute "CREATE TABLE ${prefix}_way_tag (id_way int, tag_key varchar, tag_value varchar)".toString()
        result = TransformUtils.toPolygonOrLine(polygonType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result
        loc = TableLocation.parse(result, DBTypes.H2)
        assert 1 == JDBCUtilities.getRowCount(sql.connection, loc)
        rows = sql.rows("SELECT * FROM ${loc.toString()}".toString())
        rows.eachWithIndex { it, i ->
            switch(i){
                case 1:
                    assert "house", it.building
                    assert "r1", it.id
                    assert it.the_geom instanceof Polygon
                    assert "lake", it.water
                    break
            }
        }
        result = TransformUtils.toPolygonOrLine(lineType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result
        loc = TableLocation.parse(result, DBTypes.H2)
        assert 1 == JDBCUtilities.getRowCount(sql.connection, loc)
        rows = sql.rows("SELECT * FROM ${loc.toString()}".toString())
        rows.eachWithIndex { it, i ->
            switch(i){
                case 1:
                    assert "house", it.building
                    assert "r1", it.id
                    assert it.the_geom instanceof MultiLineString
                    assert "lake", it.water
                    break
            }
        }

        //Test no relation tags
        sql.execute """DROP TABLE ${prefix}_way_tag;
        CREATE TABLE ${prefix}_way_tag (id_way int, tag_key varchar, tag_value varchar);
        INSERT INTO ${prefix}_way_tag VALUES(1, 'building', 'house');
        INSERT INTO ${prefix}_way_tag VALUES(1, 'material', 'concrete');
        INSERT INTO ${prefix}_way_tag VALUES(1, 'water', 'lake');
        DROP TABLE ${prefix}_relation_tag;
        CREATE TABLE ${prefix}_relation_tag (id_relation int, tag_key varchar, tag_value varchar);""".toString()
        result = TransformUtils.toPolygonOrLine(polygonType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result
        loc = TableLocation.parse(result, DBTypes.H2)
        assert 1 == JDBCUtilities.getRowCount(sql.connection, loc)
        rows = sql.rows("SELECT * FROM ${loc.toString()}".toString())
        rows.eachWithIndex { it, i ->
            switch(i){
                case 1:
                    assert "house", it.building
                    assert "w1", it.id
                    assert it.the_geom instanceof Polygon
                    assert "lake", it.water
                    break
            }
        }
        result = TransformUtils.toPolygonOrLine(lineType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result
        loc = TableLocation.parse(result, DBTypes.H2)
        assert 1 == JDBCUtilities.getRowCount(sql.connection, loc)
        rows = sql.rows("SELECT * FROM ${loc.toString()}".toString())
        rows.eachWithIndex { it, i ->
            switch(i){
                case 1:
                    assert "house", it.building
                    assert "w1", it.id
                    assert it.the_geom instanceof LineString
                    assert "lake", it.water
                    break
            }
        }

        //Test no tags
        sql.execute """DROP TABLE ${prefix}_way_tag;
        CREATE TABLE ${prefix}_way_tag (id_way int, tag_key varchar, tag_value varchar);
        DROP TABLE ${prefix}_relation_tag;
        CREATE TABLE ${prefix}_relation_tag (id_relation int, tag_key varchar, tag_value varchar);""".toString()

        result = TransformUtils.toPolygonOrLine(polygonType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result

        result = TransformUtils.toPolygonOrLine(lineType, sql, prefix, epsgCode, tags, columnsToKeep)
        assert result
    }

    /**
     * Create a sample of OSM data
     *
     * @param sql sql where the data should be created.
     * @param prefix Prefix of the OSM tables.
     */
    private static void createData(def sql, def prefix){
        sql.execute """CREATE TABLE ${prefix}_node_tag (id_node int, tag_key varchar, tag_value varchar);
        INSERT INTO ${prefix}_node_tag VALUES(1, 'building', 'house');
        INSERT INTO ${prefix}_node_tag VALUES(1, 'material', 'concrete');
        INSERT INTO ${prefix}_node_tag VALUES(2, 'material', 'concrete');
        INSERT INTO ${prefix}_node_tag VALUES(3, 'water', 'lake');
        INSERT INTO ${prefix}_node_tag VALUES(4, 'water', 'lake');
        INSERT INTO ${prefix}_node_tag VALUES(4, 'building', 'house');

        CREATE TABLE ${prefix}_way_tag (id_way int, tag_key varchar, tag_value varchar);
        INSERT INTO ${prefix}_way_tag VALUES(1, 'building', 'house');
        INSERT INTO ${prefix}_way_tag VALUES(1, 'material', 'concrete');
        INSERT INTO ${prefix}_way_tag VALUES(1, 'water', 'lake');

        CREATE TABLE ${prefix}_relation_tag (id_relation int, tag_key varchar, tag_value varchar);
        INSERT INTO ${prefix}_relation_tag VALUES(1, 'building', 'house');
        INSERT INTO ${prefix}_relation_tag VALUES(1, 'material', 'concrete');
        INSERT INTO ${prefix}_relation_tag VALUES(1, 'water', 'lake');

        CREATE TABLE ${prefix}_node(the_geom geometry, id_node int);
        INSERT INTO ${prefix}_node VALUES('POINT(0 0)', 1);
        INSERT INTO ${prefix}_node VALUES('POINT(10 0)', 2);
        INSERT INTO ${prefix}_node VALUES('POINT(0 10)', 3);
        INSERT INTO ${prefix}_node VALUES('POINT(10 10)', 4);

        CREATE TABLE ${prefix}_way_node(id_way int, id_node int, node_order int);
        INSERT INTO ${prefix}_way_node VALUES(1, 1, 1);
        INSERT INTO ${prefix}_way_node VALUES(1, 2, 2);
        INSERT INTO ${prefix}_way_node VALUES(1, 3, 3);
        INSERT INTO ${prefix}_way_node VALUES(1, 4, 4);
        INSERT INTO ${prefix}_way_node VALUES(1, 1, 5);

        CREATE TABLE ${prefix}_way(id_way int);
        INSERT INTO ${prefix}_way VALUES(1);

        CREATE TABLE ${prefix}_relation(id_relation int);
        INSERT INTO ${prefix}_relation VALUES(1);

        CREATE TABLE ${prefix}_way_member(id_relation int, id_way int, role varchar);
        INSERT INTO ${prefix}_way_member VALUES(1, 1, 'outer');""".toString()
    }
}
