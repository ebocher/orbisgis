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
 * info_at_ org.orbisgis.org
 */
package org.orbisgis.coreutils

import org.geotools.data.DataStore
import org.geotools.data.DefaultTransaction
import org.geotools.data.FeatureSource
import org.geotools.data.FileDataStoreFactorySpi
import org.geotools.data.FileDataStoreFinder
import org.geotools.data.Query
import org.geotools.data.Transaction
import org.geotools.data.simple.SimpleFeatureCollection
import org.geotools.data.simple.SimpleFeatureSource
import org.geotools.data.simple.SimpleFeatureStore
import org.geotools.data.transform.Definition
import org.geotools.data.transform.TransformFactory
import org.geotools.feature.FeatureCollection
import org.geotools.filter.text.cql2.CQL
import org.geotools.util.URLs
import org.opengis.feature.Feature
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.type.FeatureType
import org.opengis.feature.type.Name
import org.opengis.filter.Filter
import org.apache.commons.io.FilenameUtils
import org.geotools.data.DataStoreFinder


/**
 * Utility script used as extension module adding methods to {@link org.geotools.data.FeatureSource} class.
 *
 * @author Erwan Bocher (CNRS 2020 - 2021)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/**
 * Give to {@link FeatureSource#getFeatures()} a more readable name.
 * @param fs FeatureSource to query.
 * @return A FeatureCollection.
 */
static <T extends FeatureType, F extends Feature> FeatureCollection<T, F> getFeatureCollection(FeatureSource<T, F> fs) {
    fs.getFeatures()
}

/**
 * Give to {@link FeatureSource#getFeatures(Query)} a more readable name.
 * @param fs    FeatureSource to query.
 * @param query Query used to get the features.
 * @return A FeatureCollection.
 */
static <T extends FeatureType, F extends Feature> FeatureCollection<T, F> getFeatureCollection(FeatureSource<T, F> fs, Query query) {
    fs.getFeatures(query)
}

/**
 * Give to {@link FeatureSource#getFeatures(Filter)} a more readable name.
 * @param fs     FeatureSource to query.
 * @param filter Filter used to get the features.
 * @return A FeatureCollection.
 */
static <T extends FeatureType, F extends Feature> FeatureCollection<T, F>  getFeatureCollection(FeatureSource<T, F> fs, Filter filter) {
    fs.getFeatures(filter)
}


/**
 * Returns the FeatureSource from the given file path.
 *
 * @param path Path to the file to open as a {@link FeatureSource}
 * @return A {@link FeatureSource} created from the given path.
 */
static FeatureSource toFeatureSource(String path) {
    DataStoreFinder.getDataStore([url: new File(path).toURI().toURL()])."${FilenameUtils.getBaseName(path)}"
}

/**
 * Returns the FeatureSource from the given file url.
 *
 * @param url Url to the file to open as a {@link FeatureSource}
 * @return A {@link FeatureSource} created from the given url.
 */
static FeatureSource toFeatureSource(URL url) {
    DataStoreFinder.getDataStore([url: url])."${FilenameUtils.getBaseName(url.path)}"
}

/**
 * Returns the FeatureSource from the given file.
 *
 * @param file File to open as a {@link FeatureSource}
 * @return A {@link FeatureSource} created from the given file.
 */
static FeatureSource toFeatureSource(File file) {
    DataStoreFinder.getDataStore([url: file.toURI().toURL()])."${FilenameUtils.getBaseName(file.path)}"
}

/**
 * Returns the FeatureSource from the given URI.
 *
 * @param uri URI to the file to open as a {@link FeatureSource}
 * @return A {@link FeatureSource} created from the given URI.
 */
static FeatureSource toFeatureSource(URI uri) {
    DataStoreFinder.getDataStore([url: uri.toURL()])."${FilenameUtils.getBaseName(uri.path)}"
}

/**
 * Returns the number of features
 * @param fs the {@link FeatureSource}
 * @return the number of features
 */
static int getCount(FeatureSource fs){
    return fs.getCount(Query.ALL);
}

/**
 * Returns the epsg code of the {@link FeatureSource}
 * @param fs the {@link FeatureSource}
 * @return the number of features
 */
static int getSrid(FeatureSource fs){
    return fs.getSchema().getCoordinateReferenceSystem().getIdentifiers().first().getCode() as int
}


/**
 * Build a new {@link FeatureSource} from a SELECT like expression
 *
 * e.g ST_Buffer(the_geom, 10) as the_geom, gid / 12 as new_gid
 *
 * @param fs input {@link FeatureSource}
 * @param expression SELECT expression
 */
static FeatureSource withExpression(FeatureSource fs, String expression) {
    if(expression) {
        List<Definition> definitions = SQLToExpression.convertToDefinition(expression)
        if (definitions) {
            return TransformFactory.transform(fs, fs.getName(), definitions)
        }
    }
    return fs
}

/**
 * Iterate over all features with a filter
 *
 * @param fs input {@link FeatureSource}
 * @param filter expressed with the ECQL syntax
 * @param closure
 */
static void eachFeature(FeatureSource fs, String filter = null , Closure closure) {
    if (filter) {
        def featureIterator = fs.getFeatures(CQL.toFilter(filter)).getFeatureIterator()
        try {
            while (featureIterator.hasNext()) {
                Feature f = featureIterator.next()
                closure.call(f)
            }
        } finally {
            featureIterator.close()
        }
    } else {
        def featureIterator = fs.getFeatures(Query.ALL).getFeatureIterator()
        try {
            while (featureIterator.hasNext()) {
                Feature f = featureIterator.next()
                closure.call(f)
            }
        } finally {
            featureIterator.close()
        }
    }
}


/**
 * Save a FeatureSource to a file
 * @param fs
 * @param path
 * @param delete
 */
static void save(FeatureSource fs, String path, boolean delete =false){
    if(fs==null){
        warn("The featureSource is null")
        return
    }
    File file =  new File(path)
    String extension = FilenameUtils.getExtension(path)
    Map<String, java.io.Serializable> creationParams = new HashMap<>()
    creationParams.put("url", URLs.fileToUrl(file))
    FileDataStoreFactorySpi factory = FileDataStoreFinder.getDataStoreFactory(extension)
    if(factory==null){
        warn("Cannot find a driver to save the file ${path}")
        return
    }
    DataStore dataStore = factory.createNewDataStore(creationParams)
    Name typeName = dataStore.getNames()[0]
    SimpleFeatureType outSchema
    try{
     outSchema = dataStore.getSchema(typeName)
    }catch (Exception ex) {
        //Nothing to do
    }
    if(outSchema!=null){
        if(delete){
        dataStore.removeSchema(typeName)
        dataStore.createSchema(fs.getSchema())
        }else {
            warn("The output file ${path} already exists")
            return
        }
    }else {
        dataStore.createSchema(fs.getSchema())
    }
    try (Transaction t = new DefaultTransaction()) {
        SimpleFeatureCollection collection = fs.getFeatures()
        SimpleFeatureSource featureSourceOut =  dataStore.getFeatureSource(typeName)
        if(!SimpleFeatureStore.class.isInstance(featureSourceOut)) {
            throw new Exception(typeName + " does not support read/write access");
        } else {
            SimpleFeatureStore featureStore =  (SimpleFeatureStore) featureSourceOut
            try {
                    featureStore.addFeatures(collection)
                    t.commit()
            } catch (IOException eek) {
                eek.printStackTrace()
                try {
                    t.rollback();
                } catch (IOException doubleEeek) {
                    // rollback failed?
                }
            }

        }
    }
}