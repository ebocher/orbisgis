/*
 * Bundle GISQL is part of the OrbisGIS platform
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
 * GISQL-utils is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * GISQL-utils is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * GISQL-utils is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * GISQL-utils. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.gisql

import groovy.sql.DataSet
import groovy.sql.Sql
import org.h2gis.functions.factory.H2GISDBFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import java.sql.SQLException

/**
 * Test class dedicated to {@link org.orbisgis.gisql.GISQL}.
 *
 * @author Erwan Bocher (CNRS - 2020)
 */
class GISQLTest {

    private static Sql h2GIS

    @BeforeAll
    static void beforeAll(){
        def  connection = H2GISDBFactory.createSpatialDataBase("./target/GISQL_utils;AUTO_SERVER=TRUE");
        h2GIS = new Sql(connection);
        h2GIS.hashCode()
    }

    @Test
    void createIndex1() {
        h2GIS """DROP TABLE IF EXISTS GISQL;
              CREATE TABLE GISQL (ID INT);
              INSERT INTO GISQL VALUES (1),(2)"""
        h2GIS.createIndex("GISQL","id")
        assert h2GIS.hasIndex("GISQL", "id")
    }

    @Test
    void createIndex2() {
        h2GIS """DROP TABLE IF EXISTS GISQL;
              CREATE TABLE GISQL (ID INT);
              INSERT INTO GISQL VALUES (1),(2)"""
        assertThrows(SQLException.class, () -> {
            h2GIS.createSpatialIndex("gisql","id")
        })
    }

    @Test
    void createSpatialIndex1() {
        h2GIS """DROP TABLE IF EXISTS GISQL;
              CREATE TABLE GISQL (THE_GEOM GEOMETRY(POINT, 4326));
              INSERT INTO GISQL VALUES ('SRID=4326;POINT(10 -10)')"""
        h2GIS.createSpatialIndex("GISQL","the_geom")
        assert h2GIS.hasSpatialIndex("GISQL", "the_geom")
    }

    @Test
    void columns1() {
        h2GIS """DROP TABLE IF EXISTS GISQL;
              CREATE TABLE GISQL (ID INT);
              INSERT INTO GISQL VALUES (1),(2)"""
        print h2GIS.columns("GISQL")
    }

    @Test
    void columns2() {
        h2GIS """DROP TABLE IF EXISTS GISQL;
              CREATE TABLE GISQL as select *, X+2 AS B, X-3 as t from SYSTEM_RANGE(1, 20000);"""
        assert ["X","B","T"] ==h2GIS.columns("GISQL")
    }

    @Test
    void dataSet() {
        h2GIS """DROP TABLE IF EXISTS GISQL;
              CREATE TABLE GISQL as select *, X+2 AS B, X-3 as t from SYSTEM_RANGE(1, 10);"""
        DataSet dataset = h2GIS.dataSet("GISQL")
        dataset.each {it->
            println( it)
        }
    }
}
