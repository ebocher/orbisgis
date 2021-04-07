package org.orbisgis.coreutils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SQLToExpressionTest {

    @Test
    public void selectExpressionTobeSupported()  {
        SQLToExpression sqlToExpression = new SQLToExpression();
        String expression = "CASE\n" +
                "    WHEN type > 300 THEN 1\n" +
                "    WHEN type = 30 THEN 2\n" +
                "    ELSE 3\n" +
                "END";
        try {
            sqlToExpression.parseSingleExpression(expression);
        }catch (RuntimeException ex){
            System.out.println("--------\nExpression : \n"+ expression + "\n must be supported in the future");
        }
    }

    @Test
    public void convertToExpression()  {
        SQLToExpression sqlToExpression = new SQLToExpression();
        String expression = "the_geom";
        assertEquals("the_geom", sqlToExpression.parseSingleExpression(expression).toString());
        expression = "AREA(the_geom)";
        assertEquals("Area([the_geom])", sqlToExpression.parseSingleExpression(expression).toString());
        expression = "ST_AREA(the_geom)";
        assertEquals("Area([the_geom])", sqlToExpression.parseSingleExpression(expression).toString());
        expression = "1 + 2";
        assertEquals("(1+2)", sqlToExpression.parseSingleExpression(expression).toString());
        expression = "1 + 2 * 3";
        assertEquals("(1+(2*3))", sqlToExpression.parseSingleExpression(expression).toString());
        expression = "(1 + 2) * 3";
        assertEquals("((1+2)*3)", sqlToExpression.parseSingleExpression(expression).toString());
        expression = "AREA(the_geom)+(1 - GEOMLENGTH(AREA))/12";
        assertEquals("(Area([the_geom])+((1-geomLength([AREA]))/12))", sqlToExpression.parseSingleExpression(expression).toString());
        expression = "the_geom = 'orbisgis'";
        assertNull( sqlToExpression.parseSingleExpression(expression));
        expression = "AREA(the_geom), the_geom";
        assertNull( sqlToExpression.parseSingleExpression(expression));
        expression = "AREA(the_geom) as area";
        assertNull( sqlToExpression.parseSingleExpression(expression));
        expression = "CASE WHEN AREA(the_geom) > 0 THEN 'OK' ELSE 'NO' END";
        assertEquals("if_then_else([greaterThan([Area([the_geom])], [0])], [OK], [NO])", sqlToExpression.parseSingleExpression(expression).toString());
    }

    @Test
    public void convertToExpressionWithAlias()  {
        SQLToExpression sqlToExpression = new SQLToExpression();
        String expression = "the_geom as geom";
        assertEquals("the_geom", sqlToExpression.parseSingleExpression(expression, true).toString());
        expression = "AREA(the_geom) as geom";
        assertEquals("Area([the_geom])", sqlToExpression.parseSingleExpression(expression, true).toString());
    }

}
