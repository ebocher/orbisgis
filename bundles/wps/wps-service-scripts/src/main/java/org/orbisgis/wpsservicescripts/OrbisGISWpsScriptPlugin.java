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
package org.orbisgis.wpsservicescripts;

import org.orbisgis.wpsservice.WpsServer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.ArrayList;

/**
 * Main class of the plugin which declares the scripts to add, their locations in the process tree and the icons
 * associated.
 * In the WpsService, the script are organized in a tree, which has the WpsService as root.
 *
 * Scripts can be add to the tree under a specific node path with custom icon with the following method :
 *      localWpsService.addLocalScript('processFile', 'icon', 'boolean', 'nodePath');
 * with the following parameter :
 *      processFile : The File object corresponding to the script. Be careful, the plugin resource files can't be
 *              accessed from the outside of the plugin. So you have to copy it (in a temporary file as example) before
 *              adding it to the WpsService.
 *      icon : Array of Icon object to use for the WpsClient tree containing the processes. The first icon will be used
 *              for the first node of the path, the second icon for the second node ... If the node already exists,
 *              its icon won't be changed. If there is less icon than node, the last icon will be used for the others.
 *              If no icon are specified, the default one from the WpsClient will be used.
 *      boolean : it SHOULD be true. Else the used will be able to remove the process from the WpsClient without
 *              deactivating the plugin.
 *      nodePath : Path to the node where the process should be add. If nodes of the path doesn't exists, they will be
 *              created.
 * This add method return a ProcessIdentifier object which give all the information needed to identify a process. It
 * should be kept to be able to remove it later.
 *
 *
 * There is two method already implemented in this class to add the processes :
 *      Default method : the 'defaultLoadScript('nodePath')' methods which take as argument the nodePath and adds all
 *              the scripts from the 'resources' folder, keeping the file tree structure. All the script have the same
 *              icons.
 *      Custom method : the 'customLoadScript()' method load the scripts one by one under different file path with
 *              different icons.
 *
 *
 * When the plugin is launched , the 'activate()' method is call. This method load the scripts in the
 * WpsService and refresh the WpsClient.
 * When the plugin is stopped or uninstalled, the 'deactivate()' method is called. This method removes the loaded script
 * from the WpsService and refreshes the WpsClient.
 *
 */
@Component(immediate = true)
public class OrbisGISWpsScriptPlugin extends WpsScriptsPackage {

    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(OrbisGISWpsScriptPlugin.class);

    /**
     * OSGI method used to give to the plugin the WpsService. (Be careful before any modification)
     * @param wpsServer
     */
    @Reference
    public void setWpsServer(WpsServer wpsServer) {
        this.wpsServer = wpsServer;
    }

    /**
     * OSGI method used to remove from the plugin the WpsService. (Be careful before any modification)
     * @param wpsServer
     */
    public void unsetWpsServer(WpsServer wpsServer) {
        this.wpsServer = null;
    }

    /**
     * This methods is called once the plugin is loaded.
     *
     * It first check if the WpsService is ready.
     * If it is the case:
     *      Load the processes in the WpsService and save their identifier in the 'listIdProcess' list.
     *      Check if the WpsClient is ready.
     *      If it is the case :
     *          Refresh the WpsClient to display the processes.
     *      If not :
     *          Warn the user in the log that the WpsClient could not be found.
     * If not :
     *      Log the error and skip the process loading.
     *
     * In this class there is two methods to add the scripts :
     * The default one :
     *      This method adds all the scripts of the contained by the 'scripts' resources folder under the specified
     *      'nodePath' in the WpsClient. It keeps the file tree structure.
     * The custom one :
     *      This methods adds each script one by one under a specific node for each one.
     */
    @Activate
    public void activate(){
        listIdProcess = new ArrayList<>();
        //Check the WpsService
        if(wpsServer != null){
            //Default method to load the scripts
            String[] icons = new String[]{loadIcon("orbisgis.png")};
            customLoadScript("scripts/Network/createGraph.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Network"));
            customLoadScript("scripts/Table/deleteRows.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Table"));
            customLoadScript("scripts/Table/describeColumns.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Table"));
            customLoadScript("scripts/Table/insertValues.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Table"));
            customLoadScript("scripts/Table/joinTables.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Table"));
            customLoadScript("scripts/Table/deleteColumns.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Table"));
            customLoadScript("scripts/Geometry2D/Convert/extractCenter.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Convert"));
            customLoadScript("scripts/Geometry2D/Create/createGridOfPoints.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Create"));
            customLoadScript("scripts/Geometry2D/Create/createGridOfPolygons.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Create"));
            customLoadScript("scripts/Geometry2D/Create/fixedExtrudePolygons.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Create"));
            customLoadScript("scripts/Geometry2D/Create/variableExtrudePolygons.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Create"));
            customLoadScript("scripts/Geometry2D/Buffer/fixedDistanceBuffer.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Buffer"));
            customLoadScript("scripts/Geometry2D/Buffer/variableDistanceBuffer.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Buffer"));
            customLoadScript("scripts/Geometry2D/Properties/geometryProperties.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Properties"));
            customLoadScript("scripts/Geometry2D/Transform/reprojectGeometries.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Geometry2D")+"/"+I18N.tr("Transform"));
            
            //Data importer
            customLoadScript("scripts/Import/importCSVFile.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            customLoadScript("scripts/Import/importDBFFile.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            customLoadScript("scripts/Import/importShapeFile.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            customLoadScript("scripts/Import/importGeoJsonFile.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            customLoadScript("scripts/Import/importOSMFile.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            customLoadScript("scripts/Import/importGPXFile.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            customLoadScript("scripts/Import/importTSVFile.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            customLoadScript("scripts/Import/csvToPointsTable.groovy", icons, I18N.tr("OrbisGIS")+"/"+I18N.tr("Import"));
            
            //Data exporter
            customLoadScript("scripts/Export/exportCSVFile.groovy", icons, I18N.tr("OrbisGIS") + "/" + I18N.tr("Export"));
            customLoadScript("scripts/Export/exportTSVFile.groovy", icons, I18N.tr("OrbisGIS") + "/" + I18N.tr("Export"));
            customLoadScript("scripts/Export/exportDBFFile.groovy", icons, I18N.tr("OrbisGIS") + "/" + I18N.tr("Export"));
            customLoadScript("scripts/Export/exportGeoJsonFile.groovy", icons, I18N.tr("OrbisGIS") + "/" + I18N.tr("Export"));
            customLoadScript("scripts/Export/exportShapeFile.groovy", icons, I18N.tr("OrbisGIS") + "/" + I18N.tr("Export"));
            customLoadScript("scripts/Export/exportKMLFile.groovy", icons, I18N.tr("OrbisGIS") + "/" + I18N.tr("Export"));
        }
        else{
            LoggerFactory.getLogger(WpsScriptsPackage.class).error(
                    I18N.tr("Unable to retrieve the WpsService from OrbisGIS.\n" +
                            "The processes won't be loaded."));
        }
    }

    /**
     * This method is called when the plugin is deactivated.
     * If the WpsService is ready, removes all the previously loaded scripts.
     * If not, log the error and skip the process removing.
     * Then if the WpsClient is ready, refresh it.
     */
    @Deactivate
    public void deactivate(){
        if(wpsServer != null) {
            removeAllScripts();
        }
        else{
            LoggerFactory.getLogger(WpsScriptsPackage.class).error(
                    I18N.tr("Unable to retrieve the WpsService from OrbisGIS.\n" +
                            "The processes won't be removed."));
        }
    }
}
