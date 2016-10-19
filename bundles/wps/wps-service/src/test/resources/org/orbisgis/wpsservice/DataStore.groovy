package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * Test script for the DataStore
 * @author Sylvain PALOMINOS
 */
@Process(title = "DataStoreTest",
        translatedTitles = [
                @LanguageString(value = "DataStore test", lang = "en"),
                @LanguageString(value = "Test du DataStore", lang = "fr")
        ],
        resume = "Test script using the DataStore ComplexData.",
        translatedResumes = [
                @LanguageString(value = "Test script using the DataStore ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData DataStore.", lang = "fr")
        ],
        keywords = ["test", "script", "wps"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "test", lang = "en"),
                        @LanguageString(value = "test", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "script", lang = "en"),
                        @LanguageString(value = "scripte", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "wps", lang = "en"),
                        @LanguageString(value = "wps", lang = "fr")
                ])
        ],
        identifier = "orbisgis:test:datastore",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
    dataStoreOutput = inputDataStore;
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        title = "Input DataStore",
        translatedTitles = [
                @LanguageString(value = "Input DataStore", lang = "en"),
                @LanguageString(value = "Entrée DataStore", lang = "fr")
        ],
        resume = "A DataStore input.",
        translatedResumes = [
                @LanguageString(value = "A DataStore input.", lang = "en"),
                @LanguageString(value = "Une entrée DataStore.", lang = "fr")
        ],
        keywords = ["input"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        dataStoreTypes = ["GEOMETRY"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:datastore:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String inputDataStore

/*****************/
/** OUTPUT Data **/
/*****************/

/** This DataStore is the output data source. */
@DataStoreOutput(
        title="Output DataStore",
        translatedTitles = [
                @LanguageString(value = "Output DataStore", lang = "en"),
                @LanguageString(value = "Sortie DataStore", lang = "fr")
        ],
        resume="A DataStore output",
        translatedResumes = [
                @LanguageString(value = "A DataStore output.", lang = "en"),
                @LanguageString(value = "Une sortie DataStore.", lang = "fr")
        ],
        keywords = ["output"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        identifier = "orbisgis:test:datastore:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String dataStoreOutput

