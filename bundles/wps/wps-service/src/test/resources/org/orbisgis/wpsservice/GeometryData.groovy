package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.GeometryInput
import org.orbisgis.wpsgroovyapi.output.GeometryOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * Test script for the Geometry
 * @author Sylvain PALOMINOS
 */
@Process(title = "GeometryTest",
        translatedTitles = [
                @LanguageString(value = "Geometry test", lang = "en"),
                @LanguageString(value = "Test du Geometry", lang = "fr")
        ],
        resume = "Test script using the Geometry ComplexData.",
        translatedResumes = [
                @LanguageString(value = "Test script using the Geometry ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData Geometry.", lang = "fr")
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
        identifier = "orbisgis:test:geometry",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
    geometryOutput = inputGeometry;
}


/****************/
/** INPUT Data **/
/****************/

/** This Geometry is the input data source. */
@GeometryInput(
        title = "Input Geometry",
        translatedTitles = [
                @LanguageString(value = "Input Geometry", lang = "en"),
                @LanguageString(value = "Entrée Geometry", lang = "fr")
        ],
        resume = "A Geometry input.",
        translatedResumes = [
                @LanguageString(value = "A Geometry input.", lang = "en"),
                @LanguageString(value = "Une entrée Geometry.", lang = "fr")
        ],
        keywords = ["input"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        dimension = 3,
        excludedTypes = ["MULTIPOINT", "POINT"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:geometry:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String inputGeometry

/*****************/
/** OUTPUT Data **/
/*****************/

/** This Geometry is the output data source. */
@GeometryOutput(
        title="Output Geometry",
        translatedTitles = [
                @LanguageString(value = "Output Geometry", lang = "en"),
                @LanguageString(value = "Sortie Geometry", lang = "fr")
        ],
        resume="A Geometry output",
        translatedResumes = [
                @LanguageString(value = "A Geometry output.", lang = "en"),
                @LanguageString(value = "Une sortie Geometry.", lang = "fr")
        ],
        keywords = ["output"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        dimension = 2,
        geometryTypes = ["POLYGON", "POINT"],
        identifier = "orbisgis:test:geometry:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String geometryOutput

