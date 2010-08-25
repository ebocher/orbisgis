//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.07.22 at 02:44:26 PM CEST 
//


package org.orbisgis.core.renderer.legend.carto.persistence;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.orbisgis.core.renderer.legend.carto.persistence package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _UniqueSymbolLegend_QNAME = new QName("org.orbisgis.legend", "unique-symbol-legend");
    private final static QName _IntervalLegend_QNAME = new QName("org.orbisgis.legend", "interval-legend");
    private final static QName _UniqueValueLegend_QNAME = new QName("org.orbisgis.legend", "unique-value-legend");
    private final static QName _ProportionalLegend_QNAME = new QName("org.orbisgis.legend", "proportional-legend");
    private final static QName _RasterLegend_QNAME = new QName("org.orbisgis.legend", "raster-legend");
    private final static QName _LabelLegend_QNAME = new QName("org.orbisgis.legend", "label-legend");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.orbisgis.core.renderer.legend.carto.persistence
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniqueSymbolLegendType }
     * 
     */
    public UniqueSymbolLegendType createUniqueSymbolLegendType() {
        return new UniqueSymbolLegendType();
    }

    /**
     * Create an instance of {@link IntervalClassification }
     * 
     */
    public IntervalClassification createIntervalClassification() {
        return new IntervalClassification();
    }

    /**
     * Create an instance of {@link ClassifiedLegendType }
     * 
     */
    public ClassifiedLegendType createClassifiedLegendType() {
        return new ClassifiedLegendType();
    }

    /**
     * Create an instance of {@link ValueClassification }
     * 
     */
    public ValueClassification createValueClassification() {
        return new ValueClassification();
    }

    /**
     * Create an instance of {@link ProportionalLegendType }
     * 
     */
    public ProportionalLegendType createProportionalLegendType() {
        return new ProportionalLegendType();
    }

    /**
     * Create an instance of {@link LabelLegendType }
     * 
     */
    public LabelLegendType createLabelLegendType() {
        return new LabelLegendType();
    }

    /**
     * Create an instance of {@link LegendType }
     * 
     */
    public LegendType createLegendType() {
        return new LegendType();
    }

    /**
     * Create an instance of {@link UniqueValueLegendType }
     * 
     */
    public UniqueValueLegendType createUniqueValueLegendType() {
        return new UniqueValueLegendType();
    }

    /**
     * Create an instance of {@link RasterLegendType }
     * 
     */
    public RasterLegendType createRasterLegendType() {
        return new RasterLegendType();
    }

    /**
     * Create an instance of {@link LegendContainer }
     * 
     */
    public LegendContainer createLegendContainer() {
        return new LegendContainer();
    }

    /**
     * Create an instance of {@link IntervalLegendType }
     * 
     */
    public IntervalLegendType createIntervalLegendType() {
        return new IntervalLegendType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniqueSymbolLegendType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "org.orbisgis.legend", name = "unique-symbol-legend")
    public JAXBElement<UniqueSymbolLegendType> createUniqueSymbolLegend(UniqueSymbolLegendType value) {
        return new JAXBElement<UniqueSymbolLegendType>(_UniqueSymbolLegend_QNAME, UniqueSymbolLegendType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntervalLegendType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "org.orbisgis.legend", name = "interval-legend")
    public JAXBElement<IntervalLegendType> createIntervalLegend(IntervalLegendType value) {
        return new JAXBElement<IntervalLegendType>(_IntervalLegend_QNAME, IntervalLegendType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniqueValueLegendType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "org.orbisgis.legend", name = "unique-value-legend")
    public JAXBElement<UniqueValueLegendType> createUniqueValueLegend(UniqueValueLegendType value) {
        return new JAXBElement<UniqueValueLegendType>(_UniqueValueLegend_QNAME, UniqueValueLegendType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProportionalLegendType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "org.orbisgis.legend", name = "proportional-legend")
    public JAXBElement<ProportionalLegendType> createProportionalLegend(ProportionalLegendType value) {
        return new JAXBElement<ProportionalLegendType>(_ProportionalLegend_QNAME, ProportionalLegendType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RasterLegendType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "org.orbisgis.legend", name = "raster-legend")
    public JAXBElement<RasterLegendType> createRasterLegend(RasterLegendType value) {
        return new JAXBElement<RasterLegendType>(_RasterLegend_QNAME, RasterLegendType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LabelLegendType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "org.orbisgis.legend", name = "label-legend")
    public JAXBElement<LabelLegendType> createLabelLegend(LabelLegendType value) {
        return new JAXBElement<LabelLegendType>(_LabelLegend_QNAME, LabelLegendType.class, null, value);
    }

}
