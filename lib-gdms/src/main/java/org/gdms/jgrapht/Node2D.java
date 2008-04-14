package org.gdms.jgrapht;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * This class is a wrapper around a Coordinate object which acts as a Node of 
 * the JGraphT library
 * 0.1 (2006-06-01) : initial release<br>
 * 0.2 (2007-04-20) : use INode static DEFAULT_GEOMETRY_FACTORY<br>
 * 0.3 (2007-05-28) : add a getGeometry method taking a GeometryFactory parameter
 * @author Michael Michaud
 * @version 0.3 (2007-05-28)
 */
public class Node2D implements INode {
    
    Coordinate c;
    
    public Node2D(Coordinate c) {this.c = c;}
    
   /**
    * Return the coordinate of this Node.
    */
    public Coordinate getCoordinate() {return c;}
    
   /**
    * Return a Geometry representing this Node.
    */
    public Geometry getGeometry() {return DEFAULT_GEOMETRY_FACTORY.createPoint(c);}
    
   /**
    * Return a Geometry representing this Node.
    */
    public Geometry getGeometry(GeometryFactory factory) {return factory.createPoint(c);}
    
   /**
    * Return true if obj is a Node2D with the same 2D coordinate as this node.
    */
    public boolean equals(Object obj){
        if (obj instanceof Node2D) {return c.equals(((Node2D)obj).c);}
        else return false;
    }
    
    public int hashCode(){return c.hashCode();}
    
    public String toString() {return "Node2D " + c.toString();}
}

