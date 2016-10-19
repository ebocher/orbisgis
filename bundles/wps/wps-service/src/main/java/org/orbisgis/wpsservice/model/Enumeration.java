/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice.model;

import net.opengis.ows._2.LanguageStringType;
import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;
import org.orbisgis.wpsgroovyapi.attributes.LanguageString;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration model class
 * @author Sylvain PALOMINOS
 **/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Enumeration",
        propOrder = {"values", "names", "defaultValues", "multiSelection", "isEditable", "translatedNames"})
public class Enumeration extends ComplexDataType implements TranslatableComplexData{

    /** List of values.*/
    @XmlElement(name = "Value", namespace = "http://orbisgis.org")
    private String[] values;
    /** List of values names.*/
    @XmlElement(name = "Name", namespace = "http://orbisgis.org")
    private String[] names;
    /** Default values.*/
    @XmlElement(name = "DefaultValue", namespace = "http://orbisgis.org")
    private String[] defaultValues;
    /** Enable or not the selection of more than one value.*/
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection = false;
    /** Enable or not the user to use its own value.*/
    @XmlAttribute(name = "isEditable")
    private boolean isEditable = false;
    @XmlElement(name = "TranslatedNames", namespace = "http://orbisgis.org")
    //TODO create an marshallable object to handle and array of LanguageString Type
    private TranslatableString[] translatedNames;

    /**
     * Main constructor.
     * @param formatList Formats of the data accepted.
     * @param valueList List of values.
     * @param defaultValues Default value. If null, no default value.
     * @throws MalformedScriptException
     */
    public Enumeration(List<Format> formatList, String[] valueList, String[] defaultValues) throws MalformedScriptException {
        format = formatList;
        this.values = valueList;
        this.defaultValues = defaultValues;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected Enumeration(){
        super();
    }

    /**
     * Returns the list of the enumeration value.
     * @return The list of values.
     */
    public String[] getValues() {
        return values;
    }

    /**
     * Returns the default values.
     * @return The default values.
     */
    public String[] getDefaultValues() {
        return defaultValues;
    }

    /**
     * Returns true if more than one value can be selected, false otherwise.
     * @return True if more than one value can be selected, false otherwise.
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }

    /**
     * Sets if the user can select more than one value.
     * @param multiSelection True if more than one value can be selected, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    /**
     * Returns true if the user can use a custom value, false otherwise.
     * @return True if the user can use a custom value, false otherwise.
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Sets if the user can use a custom value.
     * @param editable True if the user can use a custom value, false otherwise.
     */
    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    /**
     * Sets the names of the values. The names will be only used for the displaying.
     * @param names String array of the names. It should have the same size of the values array.
     */
    public void setValuesNames(String[] names){
        this.names = names;
    }

    /**
     * Returns the array of the values name.
     * @return The array of the values name.
     */
    public String[] getValuesNames(){
        return names;
    }

    /**
     * Sets the translated names of the values. The translated names will be only used for the displaying.
     * @param translatedNames List of the translated names. It should have the same size of the values array.
     */
    public void setTranslatedNames(TranslatableString[] translatedNames){
        this.translatedNames = translatedNames;
    }

    /**
     * Returns the array of the translated name.
     * @return The array of the translated name.
     */
    public TranslatableString[] getTranslatedNames(){
        return translatedNames;
    }

    @Override
    public ComplexDataType getTranslatedData(String serverLanguage, String clientLanguages) {
        try {
            Enumeration enumeration = new Enumeration(format, values, defaultValues);
            enumeration.setEditable(this.isEditable());
            enumeration.setMultiSelection(this.isMultiSelection());
            List<String> translatedNames = new ArrayList<>();
            if(this.translatedNames == null || this.translatedNames.length == 0){
                enumeration.setValuesNames(this.getValuesNames());
            }
            else {
                for (TranslatableString translatableString : this.getTranslatedNames()) {
                    String clientLanguageTranslation = null;
                    String subClientLanguageTranslation = null;
                    String serverLanguageTranslation = null;
                    for (LanguageStringType stringType : translatableString.getStrings()) {
                        if (stringType.getLang().equals(clientLanguages)) {
                            clientLanguageTranslation = stringType.getValue();
                        }
                        if (stringType.getLang().equals(clientLanguages.substring(0, 2))) {
                            subClientLanguageTranslation = stringType.getValue();
                        }
                        if (stringType.getLang().equals(serverLanguage)) {
                            serverLanguageTranslation = stringType.getValue();
                        }
                    }
                    if (clientLanguageTranslation != null) {
                        translatedNames.add(clientLanguageTranslation);
                    } else if (subClientLanguageTranslation != null) {
                        translatedNames.add(subClientLanguageTranslation);
                    } else if (serverLanguageTranslation != null) {
                        translatedNames.add(serverLanguageTranslation);
                    }
                }
                enumeration.setValuesNames(translatedNames.toArray(new String[translatedNames.size()]));
            }
            return enumeration;
        } catch (MalformedScriptException ignored) {}
        return this;
    }
}
