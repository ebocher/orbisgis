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

package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.Format;
import net.opengis.wps._2_0.LiteralDataType.LiteralDataDomain;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.model.*;
import org.orbisgis.wpsservice.model.TranslatableString;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Class able to convert annotation into object and object into annotation.
 *
 * @author Sylvain PALOMINOS
 **/

public class ObjectAnnotationConverter {

    public static void annotationToObject(DescriptionTypeAttribute descriptionTypeAttribute,
                                          DescriptionType descriptionType){
        if(descriptionTypeAttribute.translatedTitles().length != DescriptionTypeAttribute.defaultTranslatedTitles.length){
            List<LanguageStringType> titleList = new ArrayList<>();
            for(LanguageString languageString : descriptionTypeAttribute.translatedTitles()){
                LanguageStringType title = new LanguageStringType();
                title.setValue(languageString.value());
                title.setLang(languageString.lang());
                titleList.add(title);
            }
            descriptionType.getTitle().clear();
            descriptionType.getTitle().addAll(titleList);
        }
        else if(!descriptionTypeAttribute.title().equals("")){
            List<LanguageStringType> titleList = new ArrayList<>();
            LanguageStringType title = new LanguageStringType();
            title.setValue(descriptionTypeAttribute.title());
            titleList.add(title);
            descriptionType.getTitle().clear();
            descriptionType.getTitle().addAll(titleList);
        }

        if(descriptionTypeAttribute.translatedResumes().length != DescriptionTypeAttribute.defaultTranslatedResumes.length) {
            List<LanguageStringType> resumeList = new ArrayList<>();
            for(LanguageString languageString : descriptionTypeAttribute.translatedResumes()){
                LanguageStringType resume = new LanguageStringType();
                resume.setValue(languageString.value());
                resume.setLang(languageString.lang());
                resumeList.add(resume);
            }
            descriptionType.getAbstract().clear();
            descriptionType.getAbstract().addAll(resumeList);
        }
        else if(!descriptionTypeAttribute.resume().equals(DescriptionTypeAttribute.defaultResume)){
            List<LanguageStringType> resumeList = new ArrayList<>();
            LanguageStringType resume = new LanguageStringType();
            resume.setValue(descriptionTypeAttribute.resume());
            resumeList.add(resume);
            descriptionType.getAbstract().clear();
            descriptionType.getAbstract().addAll(resumeList);
        }
        if(!descriptionTypeAttribute.identifier().equals(DescriptionTypeAttribute.defaultIdentifier)){
            CodeType codeType = new CodeType();
            codeType.setValue(descriptionTypeAttribute.identifier());
            descriptionType.setIdentifier(codeType);
        }

        if(descriptionTypeAttribute.translatedKeywords().length !=
                DescriptionTypeAttribute.defaultTranslatedKeywords.length) {
            List<KeywordsType> keywordTypeList = new ArrayList<>();
            for(org.orbisgis.wpsgroovyapi.attributes.TranslatableString keyword : descriptionTypeAttribute.translatedKeywords()) {
                KeywordsType keywordsType = new KeywordsType();
                List<LanguageStringType> keywordList = new ArrayList<>();
                for (LanguageString languageString : keyword.translatableStrings()) {
                    LanguageStringType keywordString = new LanguageStringType();
                    keywordString.setValue(languageString.value());
                    keywordString.setLang(languageString.lang());
                    keywordList.add(keywordString);
                }
                keywordsType.getKeyword().clear();
                keywordsType.getKeyword().addAll(keywordList);
                keywordTypeList.add(keywordsType);
            }
            descriptionType.getKeywords().clear();
            descriptionType.getKeywords().addAll(keywordTypeList);
        }
        else if(descriptionTypeAttribute.keywords().length != DescriptionTypeAttribute.defaultKeywords.length){
            List<KeywordsType> keywordList = new ArrayList<>();
            for(String key : descriptionTypeAttribute.keywords()){
                KeywordsType keyword = new KeywordsType();
                List<LanguageStringType> stringList = new ArrayList<>();
                LanguageStringType string = new LanguageStringType();
                string.setValue(key);
                stringList.add(string);
                keyword.getKeyword().clear();
                keyword.getKeyword().addAll(stringList);
                keywordList.add(keyword);
            }
            descriptionType.getKeywords().clear();
            descriptionType.getKeywords().addAll(keywordList);
        }
        if(descriptionTypeAttribute.metadata().length != DescriptionTypeAttribute.defaultMetadata.length){
            List<MetadataType> metadataList = new ArrayList<>();
            for(MetadataAttribute metadataAttribute : descriptionTypeAttribute.metadata()){
                MetadataType metadataType = new MetadataType();
                metadataType.setHref(metadataAttribute.href());
                metadataType.setRole(metadataAttribute.role());
                metadataType.setTitle(metadataAttribute.title());
                metadataList.add(metadataType);
            }
            descriptionType.getMetadata().clear();
            descriptionType.getMetadata().addAll(metadataList);
        }
    }

    public static Format annotationToObject(FormatAttribute formatAttribute){
        Format format = new Format();
        format.setMimeType(formatAttribute.mimeType());
        format.setSchema(URI.create(formatAttribute.schema()).toString());
        format.setDefault(formatAttribute.isDefaultFormat());
        if(formatAttribute.maximumMegaBytes() == FormatAttribute.defaultMaximumMegaBytes) {
            format.setMaximumMegabytes(null);
        }
        else{
            format.setMaximumMegabytes(BigInteger.valueOf(formatAttribute.maximumMegaBytes()));
        }
        format.setEncoding(formatAttribute.encoding());
        return format;
    }

    public static MetadataType annotationToObject(MetadataAttribute descriptionTypeAttribute){
        URI href = URI.create(descriptionTypeAttribute.href());
        URI role = URI.create(descriptionTypeAttribute.role());
        String title = descriptionTypeAttribute.title();

        MetadataType metadata = new MetadataType();
        metadata.setHref(href.toString());
        metadata.setRole(role.toString());
        metadata.setTitle(title);

        return metadata;
    }

    public static Object annotationToObject(ValuesAttribute valueAttribute){
        if(valueAttribute.type().toUpperCase().equals(ValuesType.VALUE.name())){
            if(valueAttribute.value().equals(ValuesAttribute.defaultValue)){
                return null;
            }
            ValueType value = new ValueType();
            value.setValue(valueAttribute.value());
            return value;
        }
        else if(valueAttribute.type().toUpperCase().equals(ValuesType.RANGE.name())){
            RangeType range = new RangeType();
            if(!valueAttribute.spacing().equals(ValuesAttribute.defaultSpacing)) {
                ValueType spacing = new ValueType();
                spacing.setValue(valueAttribute.spacing());
                range.setSpacing(spacing);
            }
            if(!valueAttribute.maximum().equals(ValuesAttribute.defaultMaximum)){
                ValueType max = new ValueType();
                max.setValue(valueAttribute.maximum());
                range.setMaximumValue(max);
            }
            if(!valueAttribute.minimum().equals(ValuesAttribute.defaultMinimum)){
                ValueType min = new ValueType();
                min.setValue(valueAttribute.minimum());
                range.setMinimumValue(min);
            }
            return range;
        }
        return null;
    }

    public static LiteralDataDomain annotationToObject(LiteralDataDomainAttribute literalDataDomainAttribute){
        LiteralDataDomain literalDataDomain = new LiteralDataDomain();
        literalDataDomain.setDefault(literalDataDomainAttribute.isDefault());
        if(!literalDataDomainAttribute.uom().isEmpty()){
            DomainMetadataType uom = new DomainMetadataType();
            URI uomUri = URI.create(literalDataDomainAttribute.uom());
            uom.setValue(uomUri.getPath());
            uom.setReference(uomUri.toString());
            literalDataDomain.setUOM(uom);
        }
        DataType dataType = DataType.valueOf(literalDataDomainAttribute.dataType());
        DomainMetadataType domainDataType = new DomainMetadataType();
        domainDataType.setReference(dataType.getUri().toString());
        domainDataType.setValue(dataType.name());
        literalDataDomain.setDataType(domainDataType);

        Object value = ObjectAnnotationConverter.annotationToObject(literalDataDomainAttribute.possibleLiteralValues());
        if(value instanceof AllowedValues){
            literalDataDomain.setAllowedValues((AllowedValues)value);
        }
        else if(value instanceof AnyValue){
            literalDataDomain.setAnyValue((AnyValue) value);
        }
        else if(value instanceof ValuesReference){
            literalDataDomain.setValuesReference((ValuesReference) value);
        }
        ValueType defaultValue = new ValueType();
        defaultValue.setValue(literalDataDomainAttribute.defaultValue());
        literalDataDomain.setDefaultValue(defaultValue);

        return literalDataDomain;
    }

    public static LiteralDataType annotationToObject(LiteralDataAttribute literalDataAttribute,
                                                     LiteralDataDomain defaultDomain) {
        LiteralDataType literalDataType = new LiteralDataType();

        List<Format> formatList = new ArrayList<>();
        if(literalDataAttribute.formats().length == 0){
            formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
            formatList.get(0).setDefault(true);
        }
        else {
            boolean isDefault = false;
            for (FormatAttribute formatAttribute : literalDataAttribute.formats()) {
                Format format = ObjectAnnotationConverter.annotationToObject(formatAttribute);
                if(format.isDefault()){
                    isDefault = true;
                }
                formatList.add(format);
            }
            if(!isDefault){
                formatList.get(0).setDefault(true);
            }
        }
        literalDataType.getFormat().clear();
        literalDataType.getFormat().addAll(formatList);

        List<LiteralDataType.LiteralDataDomain> lddList = new ArrayList<>();
        if(literalDataAttribute.validDomains().length == 0){
            if(defaultDomain != null){
                lddList.add(defaultDomain);
            }
            else {
                LiteralDataDomain literalDataDomain = new LiteralDataDomain();
                literalDataDomain.setDefault(true);
                AnyValue anyValue = new AnyValue();
                literalDataDomain.setAnyValue(anyValue);
                ValueType defaultValue = new ValueType();
                defaultValue.setValue("");
                literalDataDomain.setDefaultValue(defaultValue);
                DomainMetadataType domainMetadataType = new DomainMetadataType();
                domainMetadataType.setReference(DataType.STRING.getUri().toString());
                domainMetadataType.setValue(DataType.STRING.name());
                literalDataDomain.setDataType(domainMetadataType);
                lddList.add(literalDataDomain);
            }
        }
        else {
            boolean isDefault = false;
            for (LiteralDataDomainAttribute literalDataDomainAttribute : literalDataAttribute.validDomains()) {
                LiteralDataDomain ldd = ObjectAnnotationConverter.annotationToObject(literalDataDomainAttribute);
                if(ldd.isDefault()){
                    isDefault = true;
                }
                lddList.add(ldd);
            }
            if(!isDefault){
                lddList.get(0).setDefault(true);
            }
        }
        literalDataType.getLiteralDataDomain().clear();
        literalDataType.getLiteralDataDomain().addAll(lddList);

        return literalDataType;
    }

    public static RawData annotationToObject(RawDataAttribute rawDataAttribute, Format format) {
        try {
            //Instantiate the RawData
            format.setDefault(true);
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            RawData rawData = new RawData(formatList);
            rawData.setFile(rawDataAttribute.isFile());
            rawData.setDirectory(rawDataAttribute.isDirectory());
            rawData.setMultiSelection(rawDataAttribute.multiSelection());
            return rawData;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static void annotationToObject(InputAttribute inputAttribute, InputDescriptionType input){
        input.setMaxOccurs(""+inputAttribute.maxOccurs());
        input.setMinOccurs(BigInteger.valueOf(inputAttribute.minOccurs()));
    }

    public static Object annotationToObject(
            PossibleLiteralValuesChoiceAttribute possibleLiteralValuesChoiceAttribute){
        if(possibleLiteralValuesChoiceAttribute.anyValues() ||
                (possibleLiteralValuesChoiceAttribute.allowedValues().length != 0 &&
                        !possibleLiteralValuesChoiceAttribute.reference().isEmpty())){
            return new AnyValue();
        }
        else if(possibleLiteralValuesChoiceAttribute.allowedValues().length != 0){
            AllowedValues allowedValues = new AllowedValues();
            List<Object> valueList = new ArrayList<>();
            for(ValuesAttribute va : possibleLiteralValuesChoiceAttribute.allowedValues()){
                valueList.add(ObjectAnnotationConverter.annotationToObject(va));
            }
            allowedValues.getValueOrRange().clear();
            allowedValues.getValueOrRange().addAll(valueList);
            return allowedValues;
        }
        else if (!possibleLiteralValuesChoiceAttribute.reference().isEmpty()){
            ValuesReference valuesReference = new ValuesReference();
            URI uri = URI.create(possibleLiteralValuesChoiceAttribute.reference());
            valuesReference.setValue(uri.getPath());
            valuesReference.setReference(uri.toString());
            return valuesReference;
        }
        return new AnyValue();
    }

    public static void annotationToObject(ProcessAttribute processAttribute, ProcessOffering processOffering){
        processOffering.getProcess().setLang(Locale.forLanguageTag(processAttribute.language()).toString());
        processOffering.setProcessVersion(processAttribute.version());
    }

    public static DataStore annotationToObject(DataStoreAttribute dataStoreAttribute, List<Format> formatList) {
        try {
            DataStore dataStore = new DataStore(formatList);
            List<DataType> dataTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataStoreAttribute.dataStoreTypes())){
                dataTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            dataStore.setDataStoreTypeList(dataTypeList);
            List<DataType> excludedTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataStoreAttribute.excludedTypes())){
                excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            dataStore.setExcludedTypeList(excludedTypeList);
            return dataStore;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static DataField annotationToObject(DataFieldAttribute dataFieldAttribute, Format format, URI dataStoreUri) {
        try {
            format.setDefault(true);
            List<DataType> dataTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataFieldAttribute.fieldTypes())){
                dataTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<DataType> excludedTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataFieldAttribute.excludedTypes())){
                excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            DataField dataField = new DataField(formatList, dataTypeList, dataStoreUri);
            dataField.setExcludedTypeList(excludedTypeList);
            dataField.setMultiSelection(dataFieldAttribute.multiSelection());
            return dataField;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static FieldValue annotationToObject(FieldValueAttribute fieldvalueAttribute, Format format, URI dataFieldUri) {
        try {
            format.setDefault(true);
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            return new FieldValue(formatList, dataFieldUri, fieldvalueAttribute.multiSelection());
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static Enumeration annotationToObject(EnumerationAttribute enumAttribute, Format format) {
        try{
            format.setDefault(true);
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            Enumeration enumeration = new Enumeration(formatList, enumAttribute.values(), enumAttribute.selectedValues());
            enumeration.setEditable(enumAttribute.isEditable());
            enumeration.setMultiSelection(enumAttribute.multiSelection());
            enumeration.setValuesNames(enumAttribute.names());
            List<TranslatableString> translatedNameList = new ArrayList<>();
            for(org.orbisgis.wpsgroovyapi.attributes.TranslatableString translatableString : enumAttribute.translatedNames()){
                List<LanguageStringType> translatedName = new ArrayList<>();
                for(LanguageString languageString : translatableString.translatableStrings()) {
                    LanguageStringType language = new LanguageStringType();
                    language.setLang(languageString.lang());
                    language.setValue(languageString.value());
                    translatedName.add(language);
                }
                translatedNameList.add(new TranslatableString(translatedName.toArray(new LanguageStringType[]{})));
            }
            enumeration.setTranslatedNames(translatedNameList.toArray(new TranslatableString[]{}));
            return enumeration;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static GeometryData annotationToObject(GeometryAttribute geometryAttribute, Format format) {
        try{
            format.setDefault(true);
            List<DataType> geometryTypeList = new ArrayList<>();
            //For each field type value from the groovy annotation, test if it is contain in the FieldType enumeration.
            for(String type : Arrays.asList(geometryAttribute.geometryTypes())){
                geometryTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<DataType> excludedTypeList = new ArrayList<>();
            //For each excluded type value from the groovy annotation, test if it is contain in the FieldType enumeration.
            for(String type : Arrays.asList(geometryAttribute.excludedTypes())){
                excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            GeometryData geometryData = new GeometryData(formatList, geometryTypeList);
            geometryData.setDimension(geometryAttribute.dimension());
            geometryData.setExcludedTypeList(excludedTypeList);
            return geometryData;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }
}
