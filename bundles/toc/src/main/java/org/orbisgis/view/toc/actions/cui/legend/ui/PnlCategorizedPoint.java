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
package org.orbisgis.view.toc.actions.cui.legend.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.coremap.renderer.se.CompositeSymbolizer;
import org.orbisgis.coremap.renderer.se.Rule;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.model.KeyEditorCategorizedPoint;
import org.orbisgis.view.toc.actions.cui.legend.model.ParametersEditorCategorizedPoint;
import org.orbisgis.view.toc.actions.cui.legend.model.TableModelCatPoint;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;

/**
 * "Interval classification - Point" UI.
 *
 * @author Alexis Guéganno
 * @author Adam Gouge
 */
public final class PnlCategorizedPoint extends PnlAbstractCategorized<PointParameters>{
    public static final Logger LOGGER = LoggerFactory.getLogger(PnlCategorizedPoint.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlCategorizedPoint.class);

    /**
     * Builds a panel with a new legend.
     *
     * @param lc     LegendContext
     */
    public PnlCategorizedPoint(LegendContext lc) {
        this(lc, new CategorizedPoint());
    }

    /**
     * Builds a panel based on the given legend.
     *
     * @param lc     LegendContext
     * @param legend Legend
     */
    public PnlCategorizedPoint(LegendContext lc, CategorizedPoint legend) {
        super(lc, legend);
        initPreview();
        buildUI();
    }

    @Override
    public CategorizedPoint getLegend() {
        return (CategorizedPoint) super.getLegend();
    }

    /**
     * This methods is called by EventHandler when the user clicks on the fall back's preview. It opens an UI that lets
     * the user edit the parameters of the fall back configuration and that apply it if the user clicks OK.
     * @param me The MouseEvent that caused the call to this method.
     */
    public void onEditFallback(MouseEvent me){
        getLegend().setFallbackParameters(editCanvas(fallbackPreview));
    }

    /**
     * Builds a SIF dialog used to edit the given PointParameters.
     * @param cse The canvas we want to edit
     * @return The PointParameters that must be used at the end of the edition.
     */
    private PointParameters editCanvas(CanvasSE cse){
        CategorizedPoint leg = getLegend();
        PointParameters lps = leg.getFallbackParameters();
        UniqueSymbolPoint usa = new UniqueSymbolPoint(lps);
        if(leg.isStrokeEnabled()){
            usa.setStrokeUom(leg.getStrokeUom());
        } else {
            usa.setPenStroke(new NullPenStrokeLegend());
        }
        PnlUniquePointSE pls = new PnlUniquePointSE(usa, leg.isStrokeEnabled());
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usa = pls.getLegend();
            PointParameters nlp = usa.getPointParameters();
            cse.setSymbol(usa.getSymbolizer());
            return nlp;
        } else {
            return lps;
        }
    }

    @Override
    public void initPreview() {
        fallbackPreview = new CanvasSE(getFallbackSymbolizer());
        MouseListener l = EventHandler.create(MouseListener.class, this, "onEditFallback", "", "mouseClicked");
        fallbackPreview.addMouseListener(l);
    }

    @Override
    public PointParameters getColouredParameters(PointParameters f, Color c) {
        return new PointParameters(f.getLineColor(), f.getLineOpacity(),f.getLineWidth(),f.getLineDash(),
                c,f.getFillOpacity(),
                f.getWidth(), f.getHeight(), f.getWkn());
    }

    @Override
    public CategorizedPoint getEmptyAnalysis() {
        return new CategorizedPoint();
    }

    @Override
    public AbstractTableModel getTableModel() {
        return new TableModelCatPoint(getLegend());
    }

    @Override
    public TableCellEditor getPreviewCellEditor() {
        return new ParametersEditorCategorizedPoint();
    }

    @Override
    public TableCellEditor getKeyCellEditor() {
        return new KeyEditorCategorizedPoint();
    }

    @Override
    public void setLegend(Legend legend) {
        if (legend instanceof CategorizedPoint) {
            if(getLegend() != null){
                Rule rule = getLegend().getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            setLegendImpl((CategorizedPoint)legend);
            this.buildUI();
        } else {
            throw new IllegalArgumentException(I18N.tr("You must use recognized RecodedLine instances in "
                    + "this panel."));
        }
    }

}
