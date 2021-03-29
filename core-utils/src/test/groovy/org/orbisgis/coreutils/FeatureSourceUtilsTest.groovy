package org.orbisgis.coreutils

import org.geotools.data.Query
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.feature.FeatureCollection
import org.junit.jupiter.api.Test
import org.opengis.filter.Filter
import org.geotools.data.FeatureSource

/**
 * Test class dedicated to {@link FeatureSourceUtils}.
 *
 * @author Erwan Bocher (CNRS 2020 - 2021)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class FeatureSourceUtilsTest {

    @Test
    void featureCollectionTest() {
        def ds = new ShapefileDataStore(this.getClass().getResource("landcover2000.shp"))
        assert ds.landcover2000.featureCollection in FeatureCollection
        assert ds.landcover2000.getFeatureCollection() in FeatureCollection
        assert ds.landcover2000.getFeatureCollection(Query.ALL) in FeatureCollection
        assert ds.landcover2000.getFeatureCollection(Filter.INCLUDE) in FeatureCollection
    }

    @Test
    void toFeatureSourceTest() {
        def url = this.class.getResource("landcover2000.shp")
        def uri = url.toURI()
        def file = new File(uri)
        def path = file.absolutePath

        def fs = url.toFeatureSource()
        assert fs
        assert fs in FeatureSource
        assert 1234 == fs.getCount(Query.ALL)

        fs = uri.toFeatureSource()
        assert fs
        assert fs in FeatureSource
        assert 1234 == fs.getCount(Query.ALL)

        fs = file.toFeatureSource()
        assert fs
        assert fs in FeatureSource
        assert 1234 == fs.getCount(Query.ALL)

        fs = path.toFeatureSource()
        assert fs
        assert fs in FeatureSource
        assert 1234 == fs.getCount(Query.ALL)
    }

    @Test
    void toFeatureSourceMethods() {
        def url = this.class.getResource("landcover2000.shp")
        def fs = url.toFeatureSource()
        assert fs
        assert fs in FeatureSource
        assert 1234 == fs.getCount()
    }

    @Test
    void eachFeatureSource() {
        def url = this.class.getResource("landcover2000.shp")
        def fs = url.toFeatureSource()
        assert fs
        fs.eachFeature{ feature ->
            println feature
        }
    }

    @Test
    void eachFeatureSourceFilter() {
        def url = this.class.getResource("landcover2000.shp")
        def fs = url.toFeatureSource()
        assert fs
        fs.eachFeature{ feature ->
            println feature
        }
    }

    @Test
    void eachFeatureSourceExpression() {
        def url = this.class.getResource("landcover2000.shp")
        def fs = url.toFeatureSource()
        assert fs
        fs.eachFeature{ feature ->
            println feature
        }
    }

    @Test
    void eachFeatureSourceExpressionFilter() {
        def url = this.class.getResource("landcover2000.shp")
        def fs = url.toFeatureSource()
        assert fs
        fs.eachFeature{ feature ->
            println feature.the_geom.buffer(12)
        }
    }
}
