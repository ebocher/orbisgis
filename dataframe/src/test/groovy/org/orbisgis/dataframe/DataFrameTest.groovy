package org.orbisgis.dataframe

import org.geotools.data.FeatureSource
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.orbisgis.datastore.h2gis.H2GIS
import smile.base.cart.SplitRule
import smile.classification.RandomForest
import smile.data.DataFrame
import smile.data.formula.Formula
import smile.data.type.StructField
import smile.data.type.StructType
import smile.data.vector.BaseVector
import smile.validation.Validation
import smile.data.Tuple
import smile.data.vector.IntVector
import smile.data.vector.StringVector
import static smile.data.formula.Terms.floor
import smile.data.type.DataType
import java.util.stream.IntStream

import static org.junit.jupiter.api.Assertions.*;

class DataFrameTest {

    private DataFrame dataFrame;
    private static def h2gis;

    @BeforeAll
    static void beforeAll() {
        h2gis = H2GIS.open("./target/mydataframe_db");
    }

    @BeforeEach
    void beforeEach() {
        try {
            h2gis.execute("DROP TABLE IF EXISTS mydataframe");
            h2gis.execute("CREATE TABLE mydataframe(col1 int, col2 varchar, col3 boolean, col4 char, col5 INT, col6 INT, " +
                    "col7 INT8, col8 REAL, col9 double, col10 time, col11 date, col12 timestamp, col13 DECIMAL(20, 2))");
            h2gis.execute("INSERT INTO mydataframe VALUES (0, 'val0', true , 0, 0, 0, 0, 0.5, 0.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 0)");
            h2gis.execute("INSERT INTO mydataframe VALUES (1, 'val1', false, 1, 1, 1, 1, 1.5, 1.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 1)");
            h2gis.execute("INSERT INTO mydataframe VALUES (2, null, true , 2, 2, 2, 2, 2.5, 2.0, null, null, null, null)");
            h2gis.execute("INSERT INTO mydataframe VALUES (3, 'val3', false, 3, 3, 3, 3, 3.5, 3.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 3)");
            h2gis.execute("INSERT INTO mydataframe VALUES (4, 'val4', true , 4, 4, 4, 4, 4.5, 4.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 4)");
            dataFrame = h2gis.getFeatureSource("MYDATAFRAME").toDataFrame()
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Tests the {@link Formula} method.
     */
    @Test
    void applyTest(){
        DataFrame df = null;
        Formula formula = Formula.rhs(floor("COL8"))
        FeatureSource fs = h2gis.getFeatureSource("MYDATAFRAME");
        assert fs!=null;
        df = fs.toDataFrame()
        assert df!=null
        def floor = formula.frame(df)
        assertNotNull(floor);
        assertEquals("floor(COL8)", floor.column(0).name());
        assertTrue(DataType.isDouble(floor.column(0).type()));
        assertEquals(0.0, floor.get(0).get(0));
        assertEquals(1.0, floor.get(1).get(0));
        assertEquals(2.0, floor.get(2).get(0));
        assertEquals(3.0, floor.get(3).get(0));
        assertEquals(4.0, floor.get(4).get(0));
    }

    /**
     * Tests the {@link DataFrame#vector(int)}, {@link DataFrame#intVector(int)},
     * {@link DataFrame#stringVector(int)}, {@link DataFrame#booleanVector(int)}, {@link DataFrame#charVector(int)},
     * {@link DataFrame#byteVector(int)}, {@link DataFrame#shortVector(int)}, {@link DataFrame#longVector(int)},
     * {@link DataFrame#floatVector(int)}, {@link DataFrame#doubleVector(int)} methods test.
     */
    @Test
    void typeVectorTest(){
        assertEquals(5, dataFrame.vector(1).size());
        assertEquals(5, dataFrame.vector(0).size());
        assertEquals(5, dataFrame.stringVector(1).size());
        assertEquals(5, dataFrame.vector(2).size());
        assertEquals(5, dataFrame.stringVector(3).size());
        assertEquals(5, dataFrame.vector(4).size());
        assertEquals(5, dataFrame.vector(5).size());
        assertEquals(5, dataFrame.vector(6).size());
        assertEquals(5, dataFrame.vector(7).size());
        assertEquals(5, dataFrame.vector(8).size());
    }

    /**
     * Tests the {@link DataFrame#select(int...)}, {@link DataFrame#select(String...)},
     * {@link DataFrame#merge(smile.data.DataFrame...)}, {@link DataFrame#merge(BaseVector[])},
     * {@link DataFrame#drop(int...)}, {@link DataFrame#drop(String...)}
     */
    @Test
    void testSelect() {
        DataFrame df01 = dataFrame.select(0, 1, 2, 3, 4);
        assertEquals(5, df01.ncols());
        assertEquals(5, df01.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5"}, df01.names());

        String[] cols = new String[]{"COL6", "COL7", "COL8", "COL9"};
        DataFrame df02 = dataFrame.select(cols);
        assertEquals(4, df02.ncols());
        assertEquals(5, df02.nrows());
        assertArrayEquals(cols, df02.names());

        DataFrame df03 = df01.merge(df02);
        assertEquals(9, df03.ncols());
        assertEquals(5, df03.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df03.names());

        DataFrame df06 = df02.drop(0, 1, 2);
        assertEquals(1, df06.ncols());
        assertEquals(5, df06.nrows());
        assertArrayEquals(new String[]{"COL9"}, df06.names());

        DataFrame df04 = dataFrame.select("COL9");
        assertEquals(1, df04.ncols());
        assertEquals(5, df04.nrows());
        assertArrayEquals(new String[]{"COL9"}, df04.names());

        DataFrame df05 = df01.merge(df02.column(0), df02.column(1), df02.column(2)).merge(df04);
        assertEquals(9, df05.ncols());
        assertEquals(5, df05.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df05.names());

        DataFrame df07 = df05.union(df03, dataFrame.select(0, 1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(9, df07.ncols());
        assertEquals(15, df07.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df07.names());

        DataFrame df08 = df07.drop("COL1", "COL2", "COL4");
        assertEquals(6, df08.ncols());
        assertEquals(15, df08.nrows());
        assertArrayEquals(new String[]{"COL3", "COL5", "COL6", "COL7", "COL8", "COL9"}, df08.names());
    }

    /**
     * Tests the {@link DataFrame#toArray()}, {@link DataFrame#toMatrix()} methods.
     */
    @Test
    void toArrayMatrixTest() {
        String[] cols = new String[]{"COL6", "COL7", "COL8", "COL9"};
        DataFrame df = dataFrame.select(cols);
        assertEquals(5, df.toArray().length);
        assertEquals(4, df.toArray()[0].length);
        assertEquals(4, df.toArray()[1].length);
        assertEquals(4, df.toArray()[2].length);
        assertEquals(4, df.toArray()[3].length);
        assertEquals(4, df.toArray()[4].length);
        assertNotNull(df.toMatrix());
    }

    /**
     * Tests the wrapping of a spatial table into a {@link DataFrame}.
     *
     */
    @Test
    void testDataFrameFromSpatialTable() {
        h2gis.execute("DROP TABLE IF EXISTS h2gis;" +
                "CREATE TABLE h2gis (id INT, the_geom1 GEOMETRY(GEOMETRY), the_geom2 GEOMETRY(GEOMETRYCOLLECTION), " +
                "the_geom3 GEOMETRY(MULTIPOLYGON), the_geom4 GEOMETRY(POLYGON), the_geom5 GEOMETRY(MULTILINESTRING)," +
                " the_geom6 GEOMETRY(LINESTRING), the_geom7 GEOMETRY(MULTIPOINT), the_geom8 GEOMETRY(POINT));" +
                "INSERT INTO h2gis VALUES " +
                "(1, 'POINT(10 10)'::GEOMETRY, 'GEOMETRYCOLLECTION (POINT(10 10), POINT(20 20))'::GEOMETRY, " +
                "'MULTIPOLYGON (((10 10,20 10,20 20,10 20, 10 10)),((50 50,60 50,60 60,50 60, 50 50)))'::GEOMETRY, " +
                "'POLYGON ((30 30,40 30,40 40,30 40,30 30))'::GEOMETRY, 'MULTILINESTRING((20 20,30 30,40 40), (50 50,60 60,70 70))'::GEOMETRY, " +
                "'LINESTRING(80 80,90 90,100 100)'::GEOMETRY, 'MULTIPOINT((20 20),(30 30))'::GEOMETRY, 'POINT(40 40)'::GEOMETRY);" +
                "INSERT INTO h2gis VALUES " +
                "(2, 'POINT(11 11)'::GEOMETRY, 'GEOMETRYCOLLECTION (POINT(11 11), POINT(21 21))'::GEOMETRY, " +
                "'MULTIPOLYGON (((11 11,21 11,21 21,11 21, 11 11)),((51 51,61 51,61 61,51 61, 51 51)))'::GEOMETRY, " +
                "'POLYGON ((31 31,41 31,41 41,31 41,31 31))'::GEOMETRY, 'MULTILINESTRING((21 21,31 31,41 41), (51 51,61 61,71 71))'::GEOMETRY, " +
                "'LINESTRING(81 81,91 91,111 111)'::GEOMETRY, 'MULTIPOINT((21 21),(31 31))'::GEOMETRY, 'POINT(41 41)'::GEOMETRY);");
        DataFrame df = h2gis.getFeatureSource("H2GIS").toDataFrame();
        assertNotNull(df);
        assertNotNull(df.schema());
        assertEquals(9, df.schema().length());
        assertEquals(9, df.ncols());
        assertEquals(0, df.columnIndex("ID"));
        assertEquals(1, df.columnIndex("THE_GEOM1"));
        assertEquals(2, df.columnIndex("THE_GEOM2"));
        assertEquals(3, df.columnIndex("THE_GEOM3"));
        assertEquals(4, df.columnIndex("THE_GEOM4"));
        assertEquals(5, df.columnIndex("THE_GEOM5"));
        assertEquals(6, df.columnIndex("THE_GEOM6"));
        assertEquals(7, df.columnIndex("THE_GEOM7"));
        assertEquals(8, df.columnIndex("THE_GEOM8"));
        assertEquals(2, df.vector(0).size());
        assertEquals(2, df.vector(1).size());
        assertEquals(2, df.vector(2).size());
        assertEquals(2, df.vector(3).size());
        assertEquals(2, df.vector(4).size());
        assertEquals(2, df.vector(5).size());
        assertEquals(2, df.vector(6).size());
        assertEquals(2, df.vector(7).size());
        assertEquals(2, df.vector(8).size());

        df = DataFrame.of(h2gis.getConnection().createStatement().executeQuery("SELECT id FROM h2gis"));
        assertNotNull(df);
        assertNotNull(df.schema());
    }

    @Test
    void testRandomForestNominalValuesInResult() {
        // Dataset used
        BaseVector[] bv = new BaseVector[]{
                IntVector.of("LCZ", new int[]{105, 107, 106, 105, 105, 106, 107, 106, 106, 107, 107, 107, 107}),
                StringVector.of("TYPE", new String[]{"grass", "corn", "corn", "grass", "corn", "grass", "forest", "grass", "grass", "corn", "corn", "corn", "corn"}),
                IntVector.of("TEMPERATURE", new int[]{12, 12, 18, 12, 16, 12, 12, 12, 16, 16, 2, 16, 18}),
                IntVector.of("WIND", new int[]{20, 30, 20, 30, 20, 20, 20, 20, 20, 20, 20, 50, 40}),
        };
        DataFrame df = DataFrame.of(bv);

        // Now we need to factorize the columns "LCZ" and "TYPE" in order to use the random forest
        DataFrame dfFactorized = df.factorize("TYPE");

        // Then we define the characteristics of the randomForest:
        Formula formula = Formula.lhs("LCZ");
        SplitRule splitRule = SplitRule.valueOf("GINI");
        int ntrees = 2;
        int mtry = 2;
        int maxDepth = 2;
        int maxNodes = 5;
        int nodeSize = 3;
        double subsample = 1.0;

        // At this point we have the "LCZ" column as predicted variable, defined as nominal. No problem here since we still have the correspondence between our "LCZ" values (105, 106, 107) and 0, 1, 2.
        // Then we create the randomForest
        RandomForest model = RandomForest.fit(formula, dfFactorized, ntrees, mtry, splitRule, maxDepth, maxNodes, nodeSize, subsample);

        // Finally, when we apply the random forest
        int[] prediction = Validation.test(model, dfFactorized);
        assertTrue(IntStream.of(prediction).filter(it -> it >100).toArray().length>0);
    }

    @Test
    void testRandomForestNominalValuesInResult2() {
        // Dataset used
        List<Tuple> data = new ArrayList<>();
        List<StructField> fields = new ArrayList<>();
        fields.add(new StructField("LCZ", DataType.of(Integer.class)));
        fields.add(new StructField("TYPE", DataType.of(String.class)));
        fields.add(new StructField("TEMPERATURE", DataType.of(Integer.class)));
        fields.add(new StructField("WIND", DataType.of(Integer.class)));
        StructType structType = new StructType(fields);
        data.add(Tuple.of(new Object[]{105, "grass", 12, 20}, structType));
        data.add(Tuple.of(new Object[]{107, "corn", 12, 30}, structType));
        data.add(Tuple.of(new Object[]{106, "corn", 18, 20}, structType));
        data.add(Tuple.of(new Object[]{105, "grass", 12, 30}, structType));
        data.add(Tuple.of(new Object[]{105, "corn", 16, 20}, structType));
        data.add(Tuple.of(new Object[]{106, "grass", 12, 20}, structType));
        data.add(Tuple.of(new Object[]{107, "forest" ,12, 20}, structType));
        data.add(Tuple.of(new Object[]{106, "grass" ,12, 20}, structType));
        data.add(Tuple.of(new Object[]{106, "grass" ,16, 20}, structType));
        data.add(Tuple.of(new Object[]{107, "corn", 16, 20}, structType));
        data.add(Tuple.of(new Object[]{107, "corn", 2, 20}, structType));
        data.add(Tuple.of(new Object[]{107, "corn", 16, 50}, structType));
        data.add(Tuple.of(new Object[]{107, "corn", 18, 40}, structType));

        DataFrame df = DataFrame.of(data, structType);

        // Now we need to factorize the columns "LCZ" and "TYPE" in order to use the random forest
        DataFrame dfFactorized = df.factorize("TYPE");

        // Then we define the characteristics of the randomForest:
        Formula formula = Formula.lhs("LCZ");
        SplitRule splitRule = SplitRule.valueOf("GINI");
        int ntrees = 2;
        int mtry = 2;
        int maxDepth = 2;
        int maxNodes = 5;
        int nodeSize = 3;
        double subsample = 1.0;

        // At this point we have the "LCZ" column as predicted variable, defined as nominal. No problem here since we still have the correspondence between our "LCZ" values (105, 106, 107) and 0, 1, 2.
        // Then we create the randomForest
        RandomForest model = RandomForest.fit(formula, dfFactorized, ntrees, mtry, splitRule, maxDepth, maxNodes, nodeSize, subsample);

        // Finally, when we apply the random forest
        int[] prediction = Validation.test(model, dfFactorized);
        assertTrue(IntStream.of(prediction).filter(it -> it >100).toArray().length>0);
    }
}
