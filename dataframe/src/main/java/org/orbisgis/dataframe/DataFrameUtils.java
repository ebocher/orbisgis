package org.orbisgis.dataframe;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.DataType;
import smile.data.type.StructField;
import smile.data.type.StructType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DataFrameUtils {


    /**
     * Convert a SimpleFeatureSource to a Smile dataframe
     * @param fs
     * @return
     */
    public static DataFrame toDataFrame(SimpleFeatureSource fs){
        DataFrame df=null;
        SimpleFeatureType schema = fs.getSchema();
        int columnCount = schema.getAttributeCount();
        StructField[] fields = new StructField[columnCount];
        for (int i = 0; i < columnCount; i++) {
            AttributeType type = schema.getType(i);
            DataType dataType =null;
            if (type.getBinding().isAssignableFrom(Geometry.class)) {
                dataType=DataType.of(String.class);
            }
            else {
                dataType = DataType.of(type.getBinding());
            }
            fields[i] = new StructField(type.getName().getLocalPart(), dataType);
        }
        StructType structType = new StructType(fields);

        if(structType==null){
            return null;
        }
        SimpleFeatureIterator featureIterator=null;
        try {
            ArrayList<Tuple> rows = new ArrayList<>();
            featureIterator = fs.getFeatures().features();
            while (featureIterator.hasNext()) {
                Object[] row = new Object[columnCount];
                SimpleFeature f = (SimpleFeature) featureIterator.next();
                for (int i = 0; i < columnCount; i++) {
                    Object value =  f.getAttribute(i);
                    row[i]=value;
                }
                    // Object value = f.getAttribute(name)
                   // atts[name] = (value instanceof JtsGeometry) ? Geometry.wrap((JtsGeometry) value) : value
                
                rows.add(Tuple.of(row, structType));
            }
            df  =DataFrame.of(rows);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read the feature source");
        } finally {
            if(featureIterator!=null) {
                featureIterator.close();
            }
        }
        return df;
    }


    public static DataFrame of(List<Tuple> data) {
        return smile.data.DataFrame.of(data);
    }


    public static DataFrame of(List<Tuple> data, StructType schema) {
        return smile.data.DataFrame.of(data, schema);
    }


    public static DataFrame of(Collection<Map<String, Object>> data, StructType schema) {
        return smile.data.DataFrame.of(data, schema);
    }
}
