/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.urbsat.custom;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;

import com.vividsolutions.jts.geom.Geometry;
/**
 * return the average height of the build witch intersect the grid for each cell.
 * @author thebaud
 *Le volume d'un batiment r�sulte du produit entre la surface batie et la hauteur moyenne du batiment.
 * La somme des volumes contenus dans la zone �tudi�e
 * est calcul�e puis divis�e par le nombre de batiments et pond�r� par la surface
 * pour obtenir un volume moyen.
 */

public class BalancedBuildVolume implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values)
			throws ExecutionException {

		if (tables.length != 2)
			throw new ExecutionException(
					"BalancedBuildVolume only operates on two tables");
		if (values.length != 2)
			throw new ExecutionException(
					"BalancedBuildVolume only operates with two values");

		DataSource resultDs = null;
		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "index", "BlancedBuildVolume" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.DOUBLE) });

			resultDs = dsf.getDataSource(driver);
			resultDs.open();
			SpatialDataSourceDecorator parcels = new SpatialDataSourceDecorator(
					tables[0]);
			SpatialDataSourceDecorator grid = new SpatialDataSourceDecorator(tables[1]);
			String parcelFieldName = values[0].toString();
			String gridFieldName = values[1].toString();
			grid.open();
			parcels.open();
			grid.setDefaultGeometry(gridFieldName);

			for (int i = 0; i < grid.getRowCount(); i++) {
				Geometry cell = grid.getGeometry(i);
				int intfield = grid.getFieldIndexByName("index");
				Value t = grid.getFieldValue(i, intfield);


				IndexQuery query = new SpatialIndexQuery(cell
						.getEnvelopeInternal(), parcelFieldName);
				Iterator<PhysicalDirection> iterator = parcels
						.queryIndex(query);
				double totalVolume = 0;
				int number = 0;
				while (iterator.hasNext()) {
					PhysicalDirection dir = (PhysicalDirection) iterator.next();
					Value geom = dir.getFieldValue(parcels
							.getFieldIndexByName(parcelFieldName));
					Geometry g = geom.getAsGeometry();
					Value height = dir.getFieldValue(parcels.getFieldIndexByName("hauteur"));
					System.out.println(height);
					double hei = Double.parseDouble(height.toString());
					if (g.intersects(cell)) {
						double lenght =cell.getLength();
						double area = cell.getArea();
						totalVolume+=(hei*lenght)/area;
					number++;
					}
				}
				resultDs.insertFilledRow(new Value[]{t,
						ValueFactory.createValue(totalVolume/number)});
			}

			resultDs.commit();
			grid.cancel();
			parcels.cancel();
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (FreeingResourcesException e) {
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			e.printStackTrace();
		}
		return resultDs;
		// call AVERAGEBUILDHEIGHT from landcover2000, gdbms1182439943162 values ('the_geom', 'the_geom');

	}

	public String getName() {
		return "BALANCEDBUILDVOLUME";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSqlOrder() {
		// TODO Auto-generated method stub
		return null;
	}
}