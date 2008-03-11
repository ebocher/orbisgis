package org.gdms.data.indexes;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.rtree.DiskRTree;
import org.gdms.data.indexes.rtree.RTree;
import org.gdms.data.values.Value;
import org.gdms.source.SourceManager;

import com.vividsolutions.jts.geom.Geometry;

public class RTreeTest extends TestCase {

	private File indexFile;
	private DataSourceFactory dsf;

	private void checkLookUp(RTree tree, DataSource ds, int fieldIndex)
			throws Exception {
		tree.checkTree();
		assertTrue(tree.size() == tree.getAllValues().length);
		Geometry[] keys = tree.getAllValues();
		for (int i = 0; i < keys.length; i++) {
			int[] indexes = tree.getRow(keys[i].getEnvelopeInternal());
			assertTrue(contains(indexes, ds, fieldIndex, keys[i]));
		}

	}

	private boolean contains(int[] indexes, DataSource ds, int fieldIndex,
			Geometry geometry) throws Exception {
		for (int i : indexes) {
			if (ds.getFieldValue(i, fieldIndex).getAsGeometry()
					.equals(geometry)) {
				return true;
			}
		}

		return false;
	}

	public void testIndexPoints() throws Exception {
		testIndexPoints("points", 16, 1024, 1000.0);
	}

	public void testIndexPointsWithSmallN() throws Exception {
		testIndexPoints("points", 3, 32, 1000.0);
	}

	public void testIndexLines() throws Exception {
		testIndexPoints("lines", 16, 1024, 100.0);
	}

	public void testIndexLinesBigN() throws Exception {
		testIndexPoints("lines", 256, 1024, 100.0);
	}

	public void testIndexLinesSmallN() throws Exception {
		testIndexPoints("lines", 3, 1024, 100.0);
	}

	public void testIndexPolygons() throws Exception {
		testIndexPoints("pols", 16, 1024, 1000.0);
	}

	public void testIndexPolygonsBigN() throws Exception {
		testIndexPoints("pols", 256, 1024, 2000.0);
	}

	public void testIndexPolygonsSmallN() throws Exception {
		testIndexPoints("pols", 3, 1024, 400.0);
	}

	private void testIndexPoints(String source, int n, int blockSize,
			double checkPeriod) throws Exception {
		RTree tree = new DiskRTree(n, blockSize);
		DataSource ds = dsf.getDataSource(source);
		String fieldName = "the_geom";

		ds.open();
		tree.newIndex(indexFile);
		int fieldIndex = ds.getFieldIndexByName(fieldName);
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / (int) checkPeriod == i / checkPeriod) {
				System.out.println(i);
				tree.checkTree();
				tree.close();
				tree.openIndex(indexFile);
				tree.checkTree();
				checkLookUp(tree, ds, fieldIndex);
			}
			Geometry value = ds.getFieldValue(i, fieldIndex).getAsGeometry();
			tree.insert(value, i);
		}
		long t2 = System.currentTimeMillis();
		System.out.println(((t2 - t1) / 1000.0) + " secs");
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / (int) checkPeriod == i / checkPeriod) {
				System.out.println(i);
				tree.checkTree();
				tree.save();
				tree.checkTree();
				checkLookUp(tree, ds, fieldIndex);
			}
			Value value = ds.getFieldValue(i, fieldIndex);
			tree.delete(value.getAsGeometry(), i);
		}

		ds.cancel();
		tree.close();
	}

	@Override
	protected void setUp() throws Exception {
		indexFile = new File(SourceTest.backupDir, "rtreetest.idx");
		if (indexFile.exists()) {
			if (!indexFile.delete()) {
				throw new IOException("Cannot delete the index file");
			}
		}

		dsf = new DataSourceFactory();
		dsf.setTempDir(SourceTest.backupDir.getAbsolutePath());

		SourceManager sm = dsf.getSourceManager();
		sm.register("points", new File("src/test/resources/points.shp"));
		sm.register("lines", new File(BaseTest.externalData
				+ "shp/mediumshape2D/hedgerow.shp"));
		sm.register("pols", new File(BaseTest.externalData
				+ "shp/bigshape2D/cantons.shp"));
	}
}
