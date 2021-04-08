package org.orbisgis.datastore.jdbcutils

import groovy.sql.OutParameter
import java.sql.Types

/**
 * Class declaring {@link OutParameter} used for dedicated methods in {@link JDBCDataStoreUtils}.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class OutParameters {
        public static final OutParameter ARRAY = new OutParameter ( ) { int getType ( ) { return Types.ARRAY } }
        public static final OutParameter BIGINT = new OutParameter ( ) { int getType ( ) { return Types.BIGINT } }
        public static final OutParameter BINARY = new OutParameter ( ) { int getType ( ) { return Types.BINARY } }
        public static final OutParameter BIT = new OutParameter ( ) { int getType ( ) { return Types.BIT } }
        public static final OutParameter BLOB = new OutParameter ( ) { int getType ( ) { return Types.BLOB } }
        public static final OutParameter BOOLEAN = new OutParameter ( ) { int getType ( ) { return Types.BOOLEAN } }
        public static final OutParameter CHAR = new OutParameter ( ) { int getType ( ) { return Types.CHAR } }
        public static final OutParameter CLOB = new OutParameter ( ) { int getType ( ) { return Types.CLOB } }
        public static final OutParameter DATALINK = new OutParameter ( ) { int getType ( ) { return Types.DATALINK } }
        public static final OutParameter DATE = new OutParameter ( ) { int getType ( ) { return Types.DATE } }
        public static final OutParameter DECIMAL = new OutParameter ( ) { int getType ( ) { return Types.DECIMAL } }
        public static final OutParameter DISTINCT = new OutParameter ( ) { int getType ( ) { return Types.DISTINCT } }
        public static final OutParameter DOUBLE = new OutParameter ( ) { int getType ( ) { return Types.DOUBLE } }
        public static final OutParameter FLOAT = new OutParameter ( ) { int getType ( ) { return Types.FLOAT } }
        public static final OutParameter INTEGER = new OutParameter ( ) { int getType ( ) { return Types.INTEGER } }
        public static final OutParameter JAVA_OBJECT = new OutParameter ( ) { int getType ( ) { return Types.JAVA_OBJECT } }
        public static final OutParameter LONGVARBINARY = new OutParameter ( ) { int getType ( ) { return Types.LONGVARBINARY } }
        public static final OutParameter LONGVARCHAR = new OutParameter ( ) { int getType ( ) { return Types.LONGVARCHAR } }
        public static final OutParameter NULL = new OutParameter ( ) { int getType ( ) { return Types.NULL } }
        public static final OutParameter NUMERIC = new OutParameter ( ) { int getType ( ) { return Types.NUMERIC } }
        public static final OutParameter OTHER = new OutParameter ( ) { int getType ( ) { return Types.OTHER } }
        public static final OutParameter REAL = new OutParameter ( ) { int getType ( ) { return Types.REAL } }
        public static final OutParameter REF = new OutParameter ( ) { int getType ( ) { return Types.REF } }
        public static final OutParameter SMALLINT = new OutParameter ( ) { int getType ( ) { return Types.SMALLINT } }
        public static final OutParameter STRUCT = new OutParameter ( ) { int getType ( ) { return Types.STRUCT } }
        public static final OutParameter TIME = new OutParameter ( ) { int getType ( ) { return Types.TIME } }
        public static final OutParameter TIMESTAMP = new OutParameter ( ) { int getType ( ) { return Types.TIMESTAMP } }
        public static final OutParameter TINYINT = new OutParameter ( ) { int getType ( ) { return Types.TINYINT } }
        public static final OutParameter VARBINARY = new OutParameter ( ) { int getType ( ) { return Types.VARBINARY } }
        public static final OutParameter VARCHAR = new OutParameter ( ) { int getType ( ) { return Types.VARCHAR } }
}
