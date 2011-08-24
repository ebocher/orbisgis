/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data.schema;

import org.gdms.data.types.Type;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ebocher
 */
public class MetadataUtilitiesTest {

        private DefaultMetadata basicMetadata;
        private DefaultMetadata geometryMetadata;
        private DefaultMetadata rasterMetadata;

        @Before
        public void setUp() throws Exception {
                basicMetadata = new DefaultMetadata();
                basicMetadata.addField("name", Type.STRING);
                basicMetadata.addField("surname", Type.STRING);
                basicMetadata.addField("location", Type.STRING);

                geometryMetadata = new DefaultMetadata();
                geometryMetadata.addField("the_geom", Type.GEOMETRY);
                geometryMetadata.addField("surname", Type.STRING);
                geometryMetadata.addField("location", Type.STRING);

                rasterMetadata = new DefaultMetadata();
                rasterMetadata.addField("raster", Type.RASTER);
                rasterMetadata.addField("surname", Type.STRING);
                rasterMetadata.addField("location", Type.STRING);
        }

        @Test
        public void testGetSpatialFieldIndex() throws Exception {
                assertTrue(MetadataUtilities.getSpatialFieldIndex(basicMetadata) == -1);
                assertTrue(MetadataUtilities.getSpatialFieldIndex(geometryMetadata) == 0);
                assertTrue(MetadataUtilities.getSpatialFieldIndex(rasterMetadata) == 0);

        }

        @Test
        public void testGetGeometryFieldIndex() throws Exception {
                assertTrue(MetadataUtilities.getGeometryFieldIndex(basicMetadata) == -1);
                assertTrue(MetadataUtilities.getGeometryFieldIndex(geometryMetadata) == 0);
                assertTrue(MetadataUtilities.getGeometryFieldIndex(rasterMetadata) == -1);
        }

        @Test
        public void testGetRasterFieldIndex() throws Exception {
                assertTrue(MetadataUtilities.getRasterFieldIndex(basicMetadata) == -1);
                assertTrue(MetadataUtilities.getRasterFieldIndex(geometryMetadata) == -1);
                assertTrue(MetadataUtilities.getRasterFieldIndex(rasterMetadata) == 0);
        }

        @Test
        public void testisGeometry() throws Exception {
                assertTrue(!MetadataUtilities.isGeometry(basicMetadata));
                assertTrue(MetadataUtilities.isGeometry(geometryMetadata));
                assertTrue(!MetadataUtilities.isGeometry(rasterMetadata));
        }

        @Test
        public void testisRaster() throws Exception {
                assertTrue(!MetadataUtilities.isRaster(basicMetadata));
                assertTrue(!MetadataUtilities.isRaster(geometryMetadata));
                assertTrue(MetadataUtilities.isRaster(rasterMetadata));
        }
}
