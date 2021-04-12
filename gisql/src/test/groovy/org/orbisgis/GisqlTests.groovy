package org.orbisgis

import groovy.sql.Sql
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static org.orbisgis.Gisql.*

import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * Test class dedicated to {@link BaseUtils}.
 *
 * @author Erwan Bocher (CNRS 2021)
 */
class GisqlTests {

    static Sql h2gis

    @BeforeAll
    static void beforeAll() {
        h2gis = Gisql.createH2GISDatabase("./target/gisql_db")
        h2gis.execute("""
            CREATE TABLE elements (
                id int,
                name varchar(255),
                number int,
                the_geom GEOMETRY(POINT, 4326)
            );
            INSERT INTO elements VALUES (1, 'Simple Name', 2846, ST_GEOMFROMTEXT('POINT(0 0)', 4326));
            INSERT INTO elements VALUES (2, 'Maybe a complex Name', 7455, ST_GEOMFROMTEXT('POINT(0 10)', 4326));
            INSERT INTO elements VALUES (3, 'S N', 9272, ST_GEOMFROMTEXT('POINT(10 0)', 4326));
        """)
    }

    @Test
    void getSpatialMetadata() {
        assert h2gis.srid("elements") == 4326
        assert h2gis.srid("elements", "THE_GEOM") == 4326
        assert h2gis.srid("elements", "the_geom") == 4326
        assert h2gis.geometryColumnNames("elements") == ["THE_GEOM"]
        assert h2gis.geometryColumnNames("ELEMENTS") == ["THE_GEOM"]
    }

    @Test
    void reprojectTest() {
        def firsRows = h2gis.reproject("elements", 3857).firstRow()
        assert 1 == firsRows.ID
        assert "POINT (0 -0.0000000007081155)" == firsRows.THE_GEOM.toString()
        assert 3 == h2gis.reproject("elements", 3857).rows().size()
    }

    @Test
    void reprojectFilterTest() {
        def firsRows = h2gis.reproject("elements", 3857, "where id = 2").firstRow()
        assert 2 == firsRows.ID
        assert "POINT (0 1118889.9748579597)" == firsRows.THE_GEOM.toString()
        firsRows = h2gis.reproject("elements", 3857, null).firstRow()
        assert 1 == firsRows.ID
        assert 1 == h2gis.reproject("elements", 3857, "where id = 2").rows().size()
    }

    @Test
    void reprojectColumnsTest() {
        def firsRows = h2gis.reproject("elements", 3857, [columns: "the_geom"]).firstRow()
        assert !firsRows.containsKey("ID")
        assert "POINT (0 -0.0000000007081155)" == firsRows.THE_GEOM.toString()
        assert 1 == h2gis.reproject("elements", 3857, [columns: "the_geom", filter: "where id = 2"]).rows().size()
    }

    @Test
    void executeScriptTest() {
        def script = """/*Select all the columns of all \nthe records in the customers table:*/\n
       DROP TABLE IF EXISTS customers;\n
       CREATE TABLE customers (id int);\n
       --INSERT INTO customers VALUES (2), (110), (120);\n
       --A comment in one line\n
       INSERT INTO customers VALUES (1), (11), (12);"""
        def sqlFile = new File("./target/sql_file.sql")
        sqlFile.delete()
        sqlFile.write(script)
        h2gis.executeScript(sqlFile.absolutePath)
        assert h2gis.columnNames("customers") == ["ID"]
        assert h2gis.rowCount("customers") == 3
    }

    @Test
    void exportImportTwoTimesShpFile() {
        h2gis.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (gid int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2gis.export("h2gis", "target/h2gis_imported.shp", true);
        h2gis.import("target/h2gis_imported.shp", "H2GIS_IMPORTED", false);
        h2gis.import("target/h2gis_imported.shp", "H2GIS_IMPORTED", true);
        def concat = ""
        h2gis.getTable("H2GIS_IMPORTED").eachRow { row -> concat += "$row.GID $row.THE_GEOM\n" }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }


}
