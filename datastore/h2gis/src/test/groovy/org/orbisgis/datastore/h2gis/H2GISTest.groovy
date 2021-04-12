package org.orbisgis.datastore.h2gis

import org.apache.commons.dbcp.BasicDataSource
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

import java.sql.SQLException
import java.sql.Time

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * Test class dedicated to {@link org.orbisgis.datastore.h2gis.H2GIS}
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class H2GISTest {

    // Some Database configuration data
    private static final def PASSWORD = "psw"
    private static final def USER = "user"
    private static final def PATH = "./target/database"
    private static final def AUTO_SERVER = true
    private static final def ESTIMATED_EXTENDS = true
    private static final def FETCH_SIZE = 100
    private static final def BATCH_INSERT_SIZE = 50

    @Test
    void openClosureTest() {
        def file = new File("./target/database1.mv.db")
        file.delete()
        assert !file.exists()

        def ds = H2GIS.open {
            password PASSWORD
            user USER
            database "${PATH}1"
            autoServer AUTO_SERVER
            estimatedExtends ESTIMATED_EXTENDS
            fetchSize FETCH_SIZE
            batchInsertSize BATCH_INSERT_SIZE
        }
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert PASSWORD == bds.password
        assert USER == bds.username
        assert "jdbc:h2:${PATH}1;AUTO_SERVER=TRUE" == bds.url.toString()
        assert FETCH_SIZE == ds.fetchSize
        assert BATCH_INSERT_SIZE == ds.batchInsertSize
    }

    @Test
    void openMapTest() {
        def file = new File("${PATH}2.mv.db")
        file.delete()
        assert !file.exists()

        def params = [passwd                : PASSWORD,
                      user                  : USER,
                      database              : "${PATH}2",
                      autoserver            : AUTO_SERVER,
                      "Estimated extends"   : ESTIMATED_EXTENDS,
                      "fetch size"          : FETCH_SIZE,
                      "Batch insert size"   : BATCH_INSERT_SIZE]
        def ds = H2GIS.open(params)
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert PASSWORD == bds.password
        assert USER == bds.username
        assert "jdbc:h2:${PATH}2;AUTO_SERVER=TRUE" == bds.url.toString()
        assert FETCH_SIZE == ds.fetchSize
        assert BATCH_INSERT_SIZE == ds.batchInsertSize
    }

    @Test
    void openPathTest() {
        def ds = H2GIS.open(PATH)
        assert ds
        assert new File("${PATH}.mv.db").exists()
    }

    @Test
    void testGetFeatureSource() throws SQLException {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        assertNotNull h2GIS
        def name = "TYPES"
        h2GIS.execute("DROP TABLE IF EXISTS $name")
        h2GIS.execute("CREATE TABLE TYPES (colint INT, colreal REAL, colint2 MEDIUMINT, coltime TIME, " +
                "colvarchar VARCHAR2, colbool boolean, coltiny tinyint, colpoint GEOMETRY(POINT), colgeom GEOMETRY)")
        assertNotNull(h2GIS.getFeatureSource("TYPES"))
    }

    @Test
    void testColumnsType() throws SQLException {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        assertNotNull h2GIS
        def name = "TYPES"
        h2GIS.execute("DROP TABLE IF EXISTS $name")
        h2GIS.execute("CREATE TABLE TYPES (colint INT, colreal REAL, colint2 MEDIUMINT, coltime TIME, " +
                "colvarchar VARCHAR2, colbool boolean, coltiny tinyint, colpoint GEOMETRY(POINT), colgeom GEOMETRY)")
        def fs = h2GIS.getFeatureSource("TYPES")
        def schema = fs.getSchema()
        assertTrue(schema.has("COLINT", Integer.class))
        assertFalse(schema.has("COLINT", Short.class))
        assertTrue(schema.has(
                [COLINT    : Integer.class,
                 COLREAL   : Float.class,
                 COLINT2   : Integer.class,
                 COLTIME   : Time.class,
                 COLVARCHAR: String.class,
                 COLBOOL   : Boolean.class,
                 COLTINY   : Short.class,
                 COLPOINT  : Point.class,
                 COLGEOM   : Geometry.class]))
    }

    @Test
    void queryH2GIS() {
        def h2GIS = H2GIS.open( './target/loadH2GIS')
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        h2GIS.eachRow "SELECT THE_GEOM FROM h2gis", { row -> concat += "$row.the_geom\n" }
        assertEquals("POINT (10 10)\nPOINT (1 1)\n", concat)
    }

    @Test
    void queryH2GISWithBatch() {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int);
                INSERT INTO h2gis VALUES (1), (2);
        """)
        h2GIS.withBatch(1) { stmt ->
            h2GIS.eachRow "SELECT id FROM h2gis", { row ->
                stmt.addBatch """INSERT INTO  h2gis VALUES(${row.id+10})""" }
        }
        assertEquals(4, h2GIS.getFeatureSource("H2GIS").count)
    }

    @Test
    void querySpatialTable() {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (gid int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        h2GIS.getFeatureSource( "H2GIS").eachFeature {
            row -> concat += "$row.GID $row.THE_GEOM\n"
        }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }

    @Test
    void queryTableNames() {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        h2GIS.execute("""
                DROP TABLE IF EXISTS table1, table2;
                CREATE TABLE table1 (gid int, the_geom geometry(point));
                CREATE TABLE table2 (gid int, the_geom geometry(point));
        """)

        def values = h2GIS.getTableNames()
        assertTrue values.contains("LOADH2GIS.PUBLIC.TABLE1")
        assertTrue values.contains("LOADH2GIS.PUBLIC.TABLE2")
    }

    @Test
    void exportImportShpFile() {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (gid int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.export("H2GIS", "target/h2gis_imported.shp", true);
        h2GIS.import("target/h2gis_imported.shp", "H2GIS_IMPORTED", null, true);
        def concat = ""
        h2GIS.getFeatureSource("H2GIS_IMPORTED").eachFeature { row -> concat += "$row.GID $row.THE_GEOM\n" }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }

    @Test
    void exportImportTwoTimesShpFile() {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (gid int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.export("h2gis", "target/h2gis_imported.shp", true);
        h2GIS.import("target/h2gis_imported.shp", "H2GIS_IMPORTED", false);
        h2GIS.import("target/h2gis_imported.shp", "H2GIS_IMPORTED", true);
        def concat = ""
        h2GIS.getFeatureSource("H2GIS_IMPORTED").eachFeature { row -> concat += "$row.GID $row.THE_GEOM\n" }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }


    @Test
    void featureFromSQL() {
        def h2GIS = H2GIS.open('./target/loadH2GIS')
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (gid int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.export("h2gis", "target/h2gis_imported.shp", true);
        h2GIS.linkedFile("target/h2gis_imported.shp", "new_table", true);
        def fs = h2GIS.select("SELECT ST_BUFFER(the_geom, 10) AS THE_GEOM FROM new_table limit 1")
        assert fs.count == 1
        fs.eachFeature {it ->
            assert it.THE_GEOM instanceof Polygon
        }
        assert ["THE_GEOM"]==fs.getSchema().getColumnNames()
    }

}
