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
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.DrawingException;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

/**
 * Get the distance of a drawn line.
 */
public class MesureLineTool extends AbstractLineTool {
    protected static Logger GUI_LOGGER = LoggerFactory.getLogger("gui."+MesureLineTool.class);

    @Override
	public void update(Observable o, Object arg) {
		//PlugInContext.checkTool(this);
	}

        @Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return vc.getLayerModel().getLayerCount() > 0;
	}

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

        @Override
	protected void lineDone(LineString ls, MapContext vc, ToolManager tm)
			throws TransitionException {
                GUI_LOGGER.info(i18n.tr("Distance : {0}",getLength(ls)));
	}

	private String getLength(Geometry ls) {
		return new DecimalFormat("0.000").format(ls.getLength());
	}

        @Override
	public void drawIn_Point(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
		super.drawIn_Point(g, vc, tm);
		Geometry geom = getCurrentLineString(vc, tm);
		tm.addTextToDraw(getLength(geom) + "");
	}

        @Override
        public ImageIcon getImageIcon() {
            return MapEditorIcons.getIcon("mesurelength");
        }

        @Override
        public String getTooltip() {
            return i18n.tr("This tool mesure the length on the map");
        }

	public String getName() {
		return i18n.tr("Mesure length");
	}
}
