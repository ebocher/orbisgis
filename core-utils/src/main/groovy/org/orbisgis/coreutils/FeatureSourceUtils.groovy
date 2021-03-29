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

import org.geotools.data.FeatureSource
import org.geotools.data.Query
import org.geotools.data.simple.SimpleFeatureSource
import org.geotools.data.transform.Definition
import org.geotools.data.transform.TransformFactory
import org.geotools.feature.FeatureCollection
import org.geotools.filter.text.cql2.CQL
import org.geotools.filter.text.ecql.ECQL
import org.opengis.feature.Feature
import org.opengis.feature.type.FeatureType
import org.opengis.filter.Filter
import org.apache.commons.io.FilenameUtils
import org.geotools.data.DataStoreFinder


/**
 * Utility script used as extension module adding methods to {@link org.geotools.data.FeatureSource} class.
 *
 * @author Erwan Bocher (CNRS 2020)
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
 * Iterate over all features with options
 * expression and filter
 *
 * @param fs
 * @param closure
 */
static void eachFeature(FeatureSource fs, Map expressions =null, def filter=null , Closure closure) {
    Filter filter_gt
    if(filter){
         filter_gt = CQL.toFilter(filter);
    }
    if(expressions){
        List<Definition> definitions = new ArrayList<Definition>();
        expressions.each {entry ->
            if(entry.key && entry.value){
                definitions.add(new Definition(entry.key, ECQL.toExpression(entry.value)))
            }
        }
        if(definitions){
        SimpleFeatureSource transformed = TransformFactory.transform(fs, fs.getName(), definitions)

        def featureIterator = transformed.getFeatures(filter_gt).getFeatureIterator()
        try {
            while(featureIterator.hasNext()) {
                Feature f = featureIterator.next()
                closure.call(f)
            }
        } finally {
            featureIterator.close()
        }
        }else{
            return
        }
    }
    def featureIterator = fs.getFeatures(Query.ALL).getFeatureIterator()
    try {
        while(featureIterator.hasNext()) {
            Feature f = featureIterator.next()
            closure.call(f)
        }
    } finally {
        featureIterator.close()
    }
}