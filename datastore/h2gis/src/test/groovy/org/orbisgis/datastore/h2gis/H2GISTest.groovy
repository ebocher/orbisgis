package org.orbisgis.datastore.h2gis

import org.apache.commons.dbcp.BasicDataSource
import org.geotools.data.Query
import org.junit.jupiter.api.Test


/**
 * Test class dedicated to {@link org.orbisgis.datastore.h2gis.H2GIS}
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class H2GISTest {

    @Test
    void openClosureTest() {
        def file = new File("./target/database1.mv.db")
        file.delete()
        assert !file.exists()

        def ds = H2GIS.open {
            password "pwd"
            user "user"
            database "./target/database1"
            autoServer true
            estimatedExtends true
            fetchSize 100
            batchInsertSize 50
        }
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert "pwd" == bds.password
        assert "user" == bds.username
        assert "jdbc:h2:./target/database1;AUTO_SERVER=TRUE" == bds.url.toString()
        assert 100 == ds.fetchSize
        assert 50 == ds.batchInsertSize
    }

    @Test
    void openMapTest() {
        def file = new File("./target/database2.mv.db")
        file.delete()
        assert !file.exists()

        def params = [passwd:"pwd",
                      user:"user",
                      database:"./target/database2",
                      autoserver:true,
                      "Estimated extends":true,
                      "fetch size":100,
                      "Batch insert size":50]
        def ds = H2GIS.open(params)
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert "pwd" == bds.password
        assert "user" == bds.username
        assert "jdbc:h2:./target/database2;AUTO_SERVER=TRUE" == bds.url.toString()
        assert 100 == ds.fetchSize
        assert 50 == ds.batchInsertSize
    }

    @Test
    void openH2GISFromPath() {
        def ds = H2GIS.open("./target/db_h2gis")
        assert ds
        assert new File("./target/db_h2gis.mv.db").exists()
    }

    @Test
    void createReadFeatureSource() {
        def ds = H2GIS.open("./target/db_h2gis")
        assert ds
        ds.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point));
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def fs = ds.getFeatureSource("GEOTABLE")
        assert fs
        assert fs.getCount(Query.ALL)==2
    }
}
