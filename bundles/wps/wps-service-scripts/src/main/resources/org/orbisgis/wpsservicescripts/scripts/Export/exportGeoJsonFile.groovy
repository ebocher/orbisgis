package org.orbisgis.wpsservicescripts.scripts.Vector.Properties


import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * @author Erwan Bocher
 */
@Process(
        title = ["Export as GeoJSON file","en",
                "Export dans un fichier GeoJSON","fr"],
        description = [
                "Export a geometry table as GeoJSON file.","en",
                "Export d'une table géometrique dans un fichier GeoJSON.","fr"],
        keywords = ["OrbisGIS,Exporter, Fichier, GeoJSON","fr",
                "OrbisGIS,Export, File, GeoJSON","en"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"]
)
def processing() {

    literalOutput = "The GeoJSON file has been imported"
}


/****************/
/** INPUT Data **/
/****************/

@JDBCTableInput(
        title = [
                "Input spatial data","en",
                "Données spatiales d'entrée","fr"],
        description = [
                "The spatial data source to export.","en",
                "La source de données spatiales à exporter.","fr"],
        dataTypes = ["GEOMETRY"])
String inputJDBCTable


@RawDataOutput(
    title = ["Output GeoJSON","en","Fichier GeoJSON","fr"],
    description = ["The Output GeoJSON file to be exported.","en",
                "Selectionner un fichier GeoJSON à exporter.","fr"],
    fileTypes = ["geojson"], multiSelection=false)
String[] fileDataInput



@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput