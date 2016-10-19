/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsgroovyapi.attributes

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Attributes of the descriptive elements of processes, inputs and outputs.
 * Other descriptive information shall be recorded in the Metadata element in the form of simple links with an
 * appropriate role identifier.
 *
 * The following fields must be defined (mandatory) :
 *  - title : String
 *       Title of a process, input, and output. Normally available for display to a human.
 *
 * The following fields can be defined (optional) :
 *  - translatedTitles : LanguageString[]
 *      List of LanguageString containing the translated titles.
 *  - resume : String
 *      Brief narrative description of a process, input, and output. Normally available for display to a human.
 *  - translatedResumes : LanguageString[]
 *      List of LanguageString containing the translated description.
 *  - keywords : String
 *      Array of keywords that characterize a process, its inputs, and outputs.
 *  - translatedKeywords : Keyword[]
 *      List of Keyword containing the keywords translations.
 *  - identifier : String
 *      Unambiguous identifier of a process, input, and output. It should be a valid URI.
 *  - metadata : MetaData[]
 *      Reference to additional metadata about this item.
 *
 * @author Sylvain PALOMINOS
 */
@Retention(RetentionPolicy.RUNTIME)
@interface DescriptionTypeAttribute {

    /** Title of a process, input, and output. Normally available for display to a human. */
    String title()

    /** List of LanguageString containing the translated titles. */
    LanguageString[] translatedTitles() default []

    /** Brief narrative description of a process, input, and output. Normally available for display to a human. */
    String resume() default ""

    /** List of LanguageString containing the translated description. */
    LanguageString[] translatedResumes() default []

    /** Array of keywords that characterize a process, its inputs, and outputs. */
    String[] keywords() default []

    /** List of Keyword containing the keywords translations. */
    TranslatableString[] translatedKeywords() default []

    /** Unambiguous identifier of a process, input, and output. */
    String identifier() default ""

    /** Reference to additional metadata about this item. */
    MetadataAttribute[] metadata() default []



    /********************/
    /** default values **/
    /********************/
    public static final LanguageString[] defaultTranslatedTitles = []
    public static final String defaultResume = ""
    public static final LanguageString[] defaultTranslatedResumes = []
    public static final String[] defaultKeywords = []
    public static final TranslatableString[] defaultTranslatedKeywords = []
    public static final String defaultIdentifier = ""
    public static final MetadataAttribute[] defaultMetadata = []
}