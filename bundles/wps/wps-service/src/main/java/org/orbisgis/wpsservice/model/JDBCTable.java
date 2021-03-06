/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBCTable represent a data source which can be an SQL table, a JSON file, a Shape file ...
 *
 * @author Sylvain PALOMINOS
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JDBCTable", propOrder = {"dataTypeList", "excludedTypeList", "listJDBCTableField", "defaultValue"})
public class JDBCTable extends ComplexDataType {
    /** List of field type that should be contained by the JDBCTable.*/
    @XmlElement(name = "JDBCTableType", namespace = "http://orbisgis.org")
    private List<DataType> dataTypeList;
    /** List of field type forbidden for the JDBCTable. If the JDBCTable contains the type, it won't be available.*/
    @XmlElement(name = "ExcludedType", namespace = "http://orbisgis.org")
    private List<DataType> excludedTypeList;
    /** List of JDBCTableField liked to the JDBCTable */
    @XmlElement(name = "JDBCTableField", namespace = "http://orbisgis.org")
    private List<JDBCTableField> listJDBCTableField;
    /** Default values of the JDBCTable. */
    @XmlAttribute(name = "defaultValue")
    private String defaultValue;
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(JDBCTable.class);

    /**
     * Main constructor
     * @param formatList List of formats accepted.
     * @throws MalformedScriptException
     */
    public JDBCTable(List<Format> formatList) throws MalformedScriptException {
        format = formatList;
        dataTypeList = new ArrayList<>();
        excludedTypeList = new ArrayList<>();
        listJDBCTableField = new ArrayList<>();
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected JDBCTable(){
        super();
        listJDBCTableField = new ArrayList<>();
    }

    /**
     * Adds a JDBCTableField as a 'child' of the JDBCTable.
     * @param jdbcTableField JDBCTableField to add.
     */
    public void addJDBCTableField(JDBCTableField jdbcTableField){
        this.listJDBCTableField.add(jdbcTableField);
    }

    /**
     * Return the list of 'child' JDBCTableField.
     * @return List of JDBCTableField.
     */
    public List<JDBCTableField> getListJDBCTableField(){
        return listJDBCTableField;
    }

    /**
     * Sets the list of types that should be contained by the JDBCTable.
     * @param dataTypeList List of DataType.
     * @throws org.orbisgis.wpsservice.model.MalformedScriptException
     */
    public void setDataTypeList(List<DataType> dataTypeList) throws MalformedScriptException {
        for(DataType dataType : dataTypeList){
            for(DataType excludedType : excludedTypeList){
                if(dataType.equals(excludedType)){
                    throw new MalformedScriptException(JDBCTableField.class, "dataTypeList", I18N.tr("A same DataType is" +
                            " accepted and excluded."));
                }
            }
        }
        this.dataTypeList = dataTypeList;
    }

    /**
     * Returns the list of types that should be contained by the JDBCTable.
     * @return List of DataType.
     */
    public List<DataType> getDataTypeList() {
        return dataTypeList;
    }

    /**
     * Sets the list of excluded type for the JDBCTable.
     * @param excludedTypeList List of excluded DataType.
     * @throws org.orbisgis.wpsservice.model.MalformedScriptException
     */
    public void setExcludedTypeList(List<DataType> excludedTypeList) throws MalformedScriptException {
        for(DataType excludedType : excludedTypeList){
            for(DataType dataType : dataTypeList){
                if(excludedType.equals(dataType)){
                    throw new MalformedScriptException(JDBCTableField.class, "excludedTypeList", I18N.tr("A same DataType is" +
                            " accepted and excluded."));
                }
            }
        }
        this.excludedTypeList = excludedTypeList;
    }

    /**
     * Returns the list of excluded type for the field.
     * @return List of excluded DataType.
     */
    public List<DataType> getExcludedTypeList() {
        return excludedTypeList;
    }

    /**
     * Sets the default value of the geometry.
     * @param defaultValue Default value of the geometry.
     */
    public void setDefaultValue(String defaultValue){
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value of the geometry.
     * @return The default value of the geometry.
     */
    public String getDefaultValue(){
        return defaultValue;
    }
}
