package orbisgis

import groovy.sql.Sql
import org.h2gis.functions.factory.H2GISDBFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import javax.sql.DataSource
import java.sql.Connection

/**
 * Test class dedicated to {@link orbisgis.Gisql}.
 *
 * @author Erwan Bocher (CNRS 2021)
 */
class GisqlTest {

    static Gisql h2gis

    @BeforeAll
    static void beforeAll() {
        h2gis = new Gisql(H2GISDBFactory.createSpatialDataBase("./target/gisql_db"))
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
    void getSrid(){
        assert h2gis.getSRID("elements")==4326
        assert h2gis.getSRID("elements", "THE_GEOM")==4326
        assert h2gis.getSRID("elements", "the_geom")==4326
    }

}
