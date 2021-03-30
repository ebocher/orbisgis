/*
 * Bundle datastore/utils is part of the OrbisGIS platform
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
 * info_at_ orbisgis.org
 */
package org.orbisgis.coreutils

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Point
import org.opengis.feature.simple.SimpleFeatureType

/**
 * Test class dedicated to {@link SimpleFeatureTypeUtils}.
 *
 * @author Erwan Bocher (CNRS 2021)
 */
class SimpleFeatureTypeUtilsTest {

    @Test
    void toSimpleFeatureType() {
        SimpleFeatureType sft =  "cities".toSimpleFeatureType([
                [name: "geom", type: "Point", srid: 4326],
                [name: "id",   type: "Integer"],
                [name: "name", type: "String"]])
        assert "cities" == sft.getName().getLocalPart()
        assert "cities" == sft.getTypeName()
        assert 3 == sft.getAttributeCount()
        def  des = sft.getDescriptor("geom")
        assert des
        assert Point.class.isAssignableFrom(des.getType().binding)
        des = sft.getDescriptor("id")
        assert des
        assert Integer.class.isAssignableFrom(des.getType().binding)
        des = sft.getDescriptor("name")
        assert des
        assert String.class.isAssignableFrom(des.getType().binding)
    }

    @Test
    void toSimpleFeatureType2() {
        SimpleFeatureType sft =  "cities".toSimpleFeatureType("geom:Point:srid=4326,id:Integer,name:String")
        assert "cities" == sft.getName().getLocalPart()
        assert "cities" == sft.getTypeName()
        assert 3 == sft.getAttributeCount()
        def  des = sft.getDescriptor("geom")
        assert des
        assert Point.class.isAssignableFrom(des.getType().binding)
        des = sft.getDescriptor("id")
        assert des
        assert Integer.class.isAssignableFrom(des.getType().binding)
        des = sft.getDescriptor("name")
        assert des
        assert String.class.isAssignableFrom(des.getType().binding)
    }

    @Test
    void toSimpleFeatureTypePropertiesMissing() {
        SimpleFeatureType sft =  "cities".toSimpleFeatureType("geom:Point:srid=4326,id:Integer,name_field:String")
        assert "cities" == sft.getName().getLocalPart()
        assert "cities" == sft.getTypeName()
        assert 3 == sft.getAttributeCount()
        def  des = sft.geom
        assert des
        assert Point.class.isAssignableFrom(des.getType().binding)
        des = sft.id
        assert des
        assert Integer.class.isAssignableFrom(des.getType().binding)
        des = sft.name_field
        assert des
        assert String.class.isAssignableFrom(des.getType().binding)
    }

}
