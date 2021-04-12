package org.orbisgis

import groovy.sql.Sql

/*
* SQLWrapper class to declare static sql functions
 */
class SQLWrapper {

    Sql sql
    String query

    SQLWrapper(Sql sql, def query){
        this.sql =sql
        this.query=query
    }

    /**
     * Return the rows as list
     * @param closure
     * @return
     */
    def rows(Closure closure){
        sql.rows(query, closure)
    }

    /**
     * Return the rows as list
     * @param closure
     * @return
     */
    def rows(){
        sql.rows(query)
    }

    /**
     * Iterate over all rows
     * @param closure
     * @return
     */
    def eachRow(Closure closure){
        sql.eachRow(query, closure)
    }

    /**
     * Get the first of the operation
     * @return
     */
    def firstRow(){
        sql.firstRow(query)
    }

}