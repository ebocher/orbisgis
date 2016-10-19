package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * This process joins two tables.
 * @return A database table or a file.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
		translatedTitles = [
				@LanguageString(value = "Table join", lang = "en"),
				@LanguageString(value = "Jointure de table", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Join two tables.", lang = "en"),
				@LanguageString(value = "Jointure de deux tables.", lang = "fr")
		],
		translatedKeywords = [
				@TranslatableString(translatableStrings = [
						@LanguageString(value = "Table", lang = "en"),
						@LanguageString(value = "Table", lang = "fr")
				]),
				@TranslatableString(translatableStrings = [
						@LanguageString(value = "Join", lang = "en"),
						@LanguageString(value = "Jointure", lang = "fr")
				])
		],
		metadata = [
				@MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/"),
				@MetadataAttribute(title="postgis", role ="DBMS", href = "http://postgis.net/")
		])
def processing() {

	if(createIndex!=null && createIndex==true){
		sql.execute "create index on "+ rightDataStore + "("+ rightField[0] +")"
		sql.execute "create index on "+ leftDataStore + "("+ leftField[0] +")"
	}

	String query = "CREATE TABLE "+outputTableName+" AS SELECT * FROM "

	if(operation.equals("left")){
		query += leftDataStore + "JOIN " + rightDataStore + " ON " + leftDataStore+ "."+ leftField[0]+ "="+ rightDataStore+"."+ rightField[0];
	}
	else if (operation.equals("left")){

	}
	//Execute the query
	sql.execute(query);

	//SELECT *
	//FROM A
	//LEFT JOIN B ON A.key = B.key

	//SELECT *
	//FROM A
	//RIGHT JOIN B ON A.key = B.key

	//INNER JOIN
	//SELECT *
	//FROM A
	//INNER JOIN B ON A.key = B.key
	literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the left data source. */
@DataStoreInput(
		translatedTitles = [
				@LanguageString(value = "Left data source", lang = "en"),
				@LanguageString(value = "Source de données gauche", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The left data source used for the join.", lang = "en"),
				@LanguageString(value = "La source de données gauche utilisée pour la jointure.", lang = "fr")
		])
String leftDataStore

/** This DataStore is the right data source. */
@DataStoreInput(
		translatedTitles = [
				@LanguageString(value = "Right data source", lang = "en"),
				@LanguageString(value = "Source de données droite", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The right data source used for the join.", lang = "en"),
				@LanguageString(value = "La source de données droite utilisée pour la jointure.", lang = "fr")
		])
String rightDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the identifier field of the left dataStore. */
@DataFieldInput(
		translatedTitles = [
				@LanguageString(value = "Left field(s)", lang = "en"),
				@LanguageString(value = "Champ(s) gauche(s)", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The field identifier of the left data source.", lang = "en"),
				@LanguageString(value = "L'identifiant du/des champ(s) de la source de données gauche.", lang = "fr")
		],
        variableReference = "leftDataStore",
        excludedTypes = ["GEOMETRY"])
String[] leftField

/** Name of the identifier field of the right dataStore. */
@DataFieldInput(
		translatedTitles = [
				@LanguageString(value = "Right field(s)", lang = "en"),
				@LanguageString(value = "Champ(s) droit(s)", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The field identifier of the right data source.", lang = "en"),
				@LanguageString(value = "L'identifiant du/des champ(s) de la source de données droite.", lang = "fr")
		],
        variableReference = "rightDataStore",
        excludedTypes = ["GEOMETRY"])
String[] rightField


@EnumerationInput(
		translatedTitles = [
				@LanguageString(value = "Operation", lang = "en"),
				@LanguageString(value = "Opération", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Types of join.", lang = "en"),
				@LanguageString(value = "Type de jointure.", lang = "fr")
		],
        values=["left","right", "union"],
        names=["Left join","Right join", "Union join" ],
        selectedValues = "left",
multiSelection = false)
String operation


@LiteralDataInput(
		translatedTitles = [
				@LanguageString(value = "Create indexes", lang = "en"),
				@LanguageString(value = "Création d'indexes", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Create an index on each field identifiers to perform the join.", lang = "en"),
				@LanguageString(value = "Création d'un index sur chacun des identifiants des champs avant la jointure.", lang = "fr")
		],
		minOccurs = 0)
Boolean createIndex


@LiteralDataInput(
		translatedTitles = [
				@LanguageString(value = "Output table name", lang = "en"),
				@LanguageString(value = "Nom de la table de sortie", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Name of the table containing the result of the process.", lang = "en"),
				@LanguageString(value = "Nom de la table contenant le résultat de la jointure.", lang = "fr")
		])
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
		translatedTitles = [
				@LanguageString(value = "Output message", lang = "en"),
				@LanguageString(value = "Message de sortie", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The output message.", lang = "en"),
				@LanguageString(value = "Le message de sortie.", lang = "fr")
		])
String literalOutput

