/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.pluginManager.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;

public class Workspace {

	private static Logger logger = Logger.getLogger(Workspace.class);

	private File workspaceFolder;

	/**
	 * Gets a file that doesn't exist inside the workspace folder
	 *
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public File getNewFile(String prefix, String suffix) {
		File ret = null;
		do {
			ret = new File(getMetadataFolder(), prefix
					+ System.currentTimeMillis() + suffix);
		} while (ret.exists());

		return ret;
	}

	/**
	 * Gets the name of a file that doesn't exist inside the workspace folder
	 *
	 * @return
	 */
	public File getNewFile() {
		return getNewFile("orbisgis", "");
	}

	/**
	 * @param name
	 *            relative path inside the workspace metadata base path
	 * @return
	 */
	public File getFile(String name) {
		File ret = new File(getMetadataFolder(), name);
		if (!ret.getParentFile().exists()) {
			ret.getParentFile().mkdirs();
		}

		return ret;
	}

	public String getAbsolutePath(String path) {
		return workspaceFolder.getAbsolutePath() + File.separator + path;
	}

	public String getRelativePath(File file) {
		String relative = "";
		boolean done = false;
		while (!done) {
			if (file.equals(workspaceFolder)) {
				if (relative.length() == 0) {
					relative = ".";
				}
				done = true;
			} else {
				relative = file.getName() + "/" + relative;
				file = file.getParentFile();
				if (file == null) {
					relative = null;
					done = true;
				}
			}
		}
		return relative;
	}

	public void init(boolean clean) throws IOException {
		logger.debug("Initializing workspace");
		while (!validWorkspace()) {
			File currentWorkspace = getCurrentWorkspaceFile();
			if (!currentWorkspace.exists()) {
				WorkspaceFolderFilePanel panel = new WorkspaceFolderFilePanel(
						"Select the workspace folder", PluginManager
								.getHomeFolder().getAbsolutePath());
				boolean accepted = UIFactory.showDialog(panel);
				if (accepted) {
					File folder = panel.getSelectedFile();
					try {
						PrintWriter pw = new PrintWriter(currentWorkspace);
						pw.println(folder.getAbsolutePath());
						pw.close();
					} catch (FileNotFoundException e) {
						throw new RuntimeException("Cannot initialize system",
								e);
					}
				}
			}

			try {
				BufferedReader fileReader = new BufferedReader(new FileReader(
						currentWorkspace));
				String currentDir = fileReader.readLine();
				workspaceFolder = new File(currentDir);
				fileReader.close();
				setWorkspaceFolder(currentDir);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Cannot find the workspace location");
			} catch (IOException e) {
				throw new RuntimeException("Cannot read the workspace location");
			}
		}
		logger.debug("Using workspace " + workspaceFolder.getAbsolutePath());
		if (clean) {
			deleteFile(getMetadataFolder());
		}
		if (!getMetadataFolder().exists()) {
			if (!getMetadataFolder().mkdirs()) {
				throw new IOException("Cannot create metadata directory");
			}
		}
	}

	private File getCurrentWorkspaceFile() {
		return new File(PluginManager.getHomeFolder(), "currentWorkspace.txt");
	}

	/**
	 * This function will recursivly delete directories and files.
	 *
	 * @param path
	 *            File or Directory to be deleted
	 * @return true indicates success.
	 */
	private static boolean deleteFile(File path) {
		if (path.exists()) {
			if (path.isDirectory()) {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteFile(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (path.delete());
	}

	private boolean validWorkspace() {
		if (workspaceFolder == null) {
			return false;
		} else if (!workspaceFolder.exists()) {
			return false;
		} else {
			return workspaceFolder.isDirectory();
		}
	}

	private File getMetadataFolder() {
		return new File(workspaceFolder, ".metadata");
	}

	public void setWorkspaceFolder(String folder) throws IOException {
		workspaceFolder = new File(folder);
		if (!getMetadataFolder().exists()) {
			if (!getMetadataFolder().mkdirs()) {
				throw new IOException("Cannot create metadata directory");
			}
		}

		File file = getCurrentWorkspaceFile();
		PrintWriter pw = new PrintWriter(file);
		pw.println(folder);
		pw.close();

	}

}
