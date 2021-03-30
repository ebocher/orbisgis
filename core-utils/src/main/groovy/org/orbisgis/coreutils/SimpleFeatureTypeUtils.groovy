package org.orbisgis.coreutils

import org.geotools.data.DataUtilities
import org.geotools.data.FeatureSource
import org.geotools.feature.NameImpl
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.type.AttributeDescriptor
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.geotools.referencing.CRS

/**
 * Create a new Schema with a name and a String containing a comma delimited
 * list of fields.
 * <p><code>
 * Schema s = new Schema("widgets","geom:Point:srid=4326,name:String,price:float")
 * </code></p>
 * @param name The Schema name
 * @param fields a string representation of the columns.
 * @param uri The namespace uri
 */
static SimpleFeatureType toSimpleFeatureType(String name, String fields, String uri = "http://orbisgis.org/feature"){
   return DataUtilities.createType(uri, name, fields)
}

/**
 * Build a SimpleFeatureType from the name and a List of colums properties.
 * @param name The name
 * @param fields A List of Fields or Strings
 * @param uri The namespace uri
 * @return {@link SimpleFeatureType}
 * @author Jared Erickson, source : https://github.com/geoscript/geoscript-groovy
 */
static SimpleFeatureType toSimpleFeatureType(String name, def fields, String uri = "http://orbisgis.org/feature"){
    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder()
    builder.setName(new NameImpl(name))
    if (uri != null) {
        builder.namespaceURI = uri
    }
    fields.each{field ->
        Class c = Class.forName(lookUpBinding(field.type))
        if (field.srid != null) {
            try {
                CoordinateReferenceSystem crs = CRS.decode("EPSG:" + field.srid)
                builder.setCRS(crs)
                builder.add(field.name, c, field.srid)
            } catch (Exception ex) {
                error("Error decoding srs: " + srid)
                return
            }
        }
        else {
            builder.add(field.name, c)
        }
    }
    return builder.buildFeatureType()
}


/**
 * Look up a GeoTools binding for the alias.
 * For example: "geoscript.geom.LinearRing" => "org.locationtech.jts.geom.LinearRing"
 * @param alias The alias
 * @return The GeoTools binding class name
 * @author Jared Erickson, source : https://github.com/geoscript/geoscript-groovy
 */
static String lookUpBinding(String alias) {
    Map map = [
            ("geoscript.geom.LinearRing".toLowerCase()) : "org.locationtech.jts.geom.LinearRing",
            ("LinearRing".toLowerCase()) : "org.locationtech.jts.geom.LinearRing",
            ("geoscript.geom.LineString".toLowerCase()) : "org.locationtech.jts.geom.LineString",
            ("LineString".toLowerCase()) : "org.locationtech.jts.geom.LineString",
            ("geoscript.geom.MultiLineString".toLowerCase()) : "org.locationtech.jts.geom.MultiLineString",
            ("MultiLineString".toLowerCase()) : "org.locationtech.jts.geom.MultiLineString",
            ("geoscript.geom.MultiPoint".toLowerCase()) : "org.locationtech.jts.geom.MultiPoint",
            ("MultiPoint".toLowerCase()) : "org.locationtech.jts.geom.MultiPoint",
            ("geoscript.geom.MultiPolygon".toLowerCase()) : "org.locationtech.jts.geom.MultiPolygon",
            ("MultiPolygon".toLowerCase()) : "org.locationtech.jts.geom.MultiPolygon",
            ("geoscript.geom.Point".toLowerCase()) : "org.locationtech.jts.geom.Point",
            ("Point".toLowerCase()) : "org.locationtech.jts.geom.Point",
            ("geoscript.geom.Polygon".toLowerCase()) : "org.locationtech.jts.geom.Polygon",
            ("Polygon".toLowerCase()) : "org.locationtech.jts.geom.Polygon",
            ("geoscript.geom.Geometry".toLowerCase()) : "org.locationtech.jts.geom.Geometry",
            ("Geometry".toLowerCase()) : "org.locationtech.jts.geom.Geometry",
            ("geoscript.geom.CircularRing".toLowerCase()) : "org.geotools.geometry.jts.CircularRing",
            ("CircularRing".toLowerCase()) : "org.geotools.geometry.jts.CircularRing",
            ("geoscript.geom.CircularString".toLowerCase()) : "org.geotools.geometry.jts.CircularString",
            ("CircularString".toLowerCase()) : "org.geotools.geometry.jts.CircularString",
            ("geoscript.geom.CompoundCurve".toLowerCase()) : "org.geotools.geometry.jts.CompoundCurve",
            ("CompoundCurve".toLowerCase()) : "org.geotools.geometry.jts.CompoundCurve",
            ("geoscript.geom.CompoundRing".toLowerCase()) : "org.geotools.geometry.jts.CompoundRing",
            ("CompoundRing".toLowerCase()) : "org.geotools.geometry.jts.CompoundRing",
            ("String".toLowerCase()) : "java.lang.String",
            ("Str".toLowerCase()) : "java.lang.String",
            ("Float".toLowerCase()) : "java.lang.Float",
            ("Int".toLowerCase()) : "java.lang.Integer",
            ("Integer".toLowerCase()) : "java.lang.Integer",
            ("Short".toLowerCase()) : "java.lang.Integer",
            ("Long".toLowerCase()) : "java.lang.Long",
            ("Double".toLowerCase()) : "java.lang.Double",
            ("Date".toLowerCase()) : "java.util.Date"
    ]
    map.get(alias.toLowerCase(), alias)
}

/**
 * Return true if the {@link SimpleFeatureType} contains a column with the given name with the given type (case sensible).
 *
 * @param schema input {@link SimpleFeatureType}
 * @param name Name of the column to check.
 * @param clazz      Class of the column to check.
 * @return True if the column is found, false otherwise.
 */
static boolean has(SimpleFeatureType schema, String name, Class<?> clazz ){
    def descriptor = schema.getDescriptor(name)
    return descriptor?clazz.isAssignableFrom(descriptor.type.binding):false
}

/**
 * Return true if the {@link SimpleFeatureType} contains a column with the given name with the given type (case sensible).
 *
 * @param schema input {@link SimpleFeatureType}
 * @param name Name of the column to check.
 * @return True if the column is found, false otherwise.
 */
static boolean has(SimpleFeatureType schema, String name){
    return schema.getDescriptor(name)!=null
}



/**
 * Return true if the {@link SimpleFeatureType} contains all the column describes in the given {@link Map} (case sensible).
 *
 * @param schema input {@link SimpleFeatureType}
 * @param columnMap {@link Map} containing the columns with the column name as key and the column type as value.
 * @return True if the columns are found, false otherwise.
 */
static boolean has(SimpleFeatureType schema, Map<String, Class<?>> columnMap) {
    return columnMap.entrySet().stream().allMatch(entry -> has(schema,entry.getKey(), entry.getValue()));
}

/**
 * Method called when the asked property is missing and returns the AttributeDescriptor corresponding to the given name.
 *
 * @param simpleFeatureType SimpleFeatureType to use.
 * @param name Name of the property/simpleFeatureType to get.
 */
static AttributeDescriptor propertyMissing(SimpleFeatureType schema, String name) {
    return schema.getDescriptor(name)
}