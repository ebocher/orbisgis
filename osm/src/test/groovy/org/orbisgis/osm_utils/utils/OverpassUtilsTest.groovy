/*
 * Bundle common-utils is part of the OrbisGIS platform
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
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
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
 * info_at_ org.orbisgis.org
 */
package org.orbisgis.osm_utils.utils

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.orbisgis.osm_utils.overpass.OSMElement

/**
 * Test class dedicated to OverpassUtils.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class OverpassUtilsTest {

    private static final def FACTORY = new GeometryFactory()

    @Test
    void extractTest() {
        def method = OverpassUtils.&executeAsOverPassQuery
        OverpassUtils.metaClass.static.executeAsOverPassQuery = {
            def query, File outputOSMFile -> return true
        }
        def path1 = OverpassUtils.extract("query")
        def path2 = OverpassUtils.extract("query")
        assert path1.endsWith(".osm")
        assert path1.startsWith("/tmp/")
        assert path1 == path2
        OverpassUtils.metaClass.static.executeAsOverPassQuery = method
    }

    @Test
    void badExtractTest() {
        def method = OverpassUtils.&executeAsOverPassQuery
        assert !OverpassUtils.extract(null)
        assert !OverpassUtils.extract("")
        OverpassUtils.metaClass.static.executeAsOverPassQuery = {
            def query, File outputOSMFile -> return false
        }
        assert !OverpassUtils.extract("query")
        OverpassUtils.metaClass.static.executeAsOverPassQuery = method
    }

    @Test
    void toOSMPolyTest() {
        Coordinate[] coordinates = [new Coordinate(2.0, 2.0),
                                    new Coordinate(4.0, 2.0),
                                    new Coordinate(4.0, 4.0),
                                    new Coordinate(2.0, 4.0),
                                    new Coordinate(2.0, 2.0)]
        assert "(poly:\"2.0 2.0 2.0 4.0 4.0 4.0 4.0 2.0\")" == (OverpassUtils.toOSMPoly(FACTORY.createPolygon(coordinates)))
        assert !OverpassUtils.toOSMPoly(FACTORY.createPolygon())
        assert !OverpassUtils.toOSMPoly(FACTORY.createPoint())
        assert !OverpassUtils.toOSMPoly(null)
    }

    @Test
    void buildOsmQueryTest() {
        def query = """[bbox:0.0,0.0,1.0,1.0];
(
\tnode["building"];
\tway["building"];
);
(._;>;);
out;"""
        def queryAllData = """[bbox:0.0,0.0,1.0,1.0];
(
\tnode["building"](poly:"0.0 0.0 1.0 0.0 1.0 1.0");
\tway["building"](poly:"0.0 0.0 1.0 0.0 1.0 1.0");
);
(._;>;);
out;"""
        def queryAllDataEnv = """[bbox:0.0,0.0,1.0,1.0];
(
\tnode["building"](poly:"0.0 0.0 1.0 0.0 1.0 1.0 0.0 1.0");
\tway["building"](poly:"0.0 0.0 1.0 0.0 1.0 1.0 0.0 1.0");
);
(._;>;);
out;"""
        Coordinate[] coordinates = [new Coordinate(0.0, 0.0),
                                    new Coordinate(0.0, 1.0),
                                    new Coordinate(1.0, 1.0),
                                    new Coordinate(0.0, 0.0)]
        def geom = FACTORY.createPolygon(coordinates)
        def keys = ["building"]
        def elements = [OSMElement.NODE, OSMElement.WAY]
        assert queryAllData == OverpassUtils.buildOSMQuery(geom, keys, elements)
        assert query == OverpassUtils.buildOSMQuery(geom, keys, elements, false)
        assert queryAllDataEnv == OverpassUtils.buildOSMQuery(geom.envelopeInternal, keys, elements)
        assert query == OverpassUtils.buildOSMQuery(geom.envelopeInternal, keys, elements, false)
    }
}