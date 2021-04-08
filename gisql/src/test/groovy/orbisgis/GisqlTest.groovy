package orbisgis

import groovy.sql.Sql
import org.h2gis.functions.factory.H2GISDBFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.sql.Connection

/**
 * Test class dedicated to {@link orbisgis.Gisql}.
 *
 * @author Erwan Bocher (CNRS 2021)
 */
class GisqlTest {


    static Connection h2gis

    @BeforeAll
    static void beforeAll() {
        h2gis = H2GISDBFactory.createSpatialDataBase("./target/gisql_db")
    }

    @Test
    void queryTest() {
        def str = "";
        h2gis.query("SELECT * FROM elements WHERE id > 1")
                {while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.query("SELECT * FROM elements WHERE id > ?", [1])
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.query("SELECT * FROM elements WHERE id > :id", [id:1])
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.query([id:1], "SELECT * FROM elements WHERE id > :id")
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        def id = 1
        h2gis.query("SELECT * FROM elements WHERE id > $id")
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str
    }
}
