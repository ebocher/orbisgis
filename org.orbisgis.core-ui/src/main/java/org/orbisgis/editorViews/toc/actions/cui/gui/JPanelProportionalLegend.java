/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/*
 *
 * Created on 27 de febrero de 2008, 18:20
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.Canvas;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.ProportionalLegend;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.sif.UIFactory;

/**
 * 
 * @author david
 */
public class JPanelProportionalLegend extends javax.swing.JPanel implements
		ILegendPanelUI {

	private int constraint = 0;
	private ILayer layer = null;
	private ProportionalLegend leg = null;
	private Canvas canvas = null;

	private LegendListDecorator dec = null;

	public JPanelProportionalLegend(Legend leg, int constraint,
			ILayer layer) {
		this.constraint = constraint;
		this.layer = layer;
		this.leg = (ProportionalLegend)leg;
		initComponents();
		initCombo();
		setCanvas();
		refreshCanvas();
	}

	private void setCanvas() {
		canvas = new Canvas();
		jPanel1.add(canvas);
	}

	private void refreshCanvas() {
		Symbol sym = createDefaultSymbol();
		canvas.setLegend(sym, constraint);
		canvas.validate();
		canvas.repaint();

		if (dec != null) {
			dec.setLegend(getLegend());
		}
	}

	private void initCombo() {

		ArrayList<String> comboValuesArray = new ArrayList<String>();
		try {
			int numFields = layer.getDataSource().getFieldCount();
			for (int i = 0; i < numFields; i++) {
				int fieldType = layer.getDataSource().getFieldType(i)
						.getTypeCode();
				if (fieldType == Type.BYTE || fieldType == Type.SHORT
						|| fieldType == Type.INT || fieldType == Type.LONG
						|| fieldType == Type.FLOAT || fieldType == Type.DOUBLE) {
					comboValuesArray.add(layer.getDataSource().getFieldName(i));
				}
			}
		} catch (DriverException e) {
			System.out.println("Driver Exception: " + e.getMessage());
		}

		String[] comboValues = new String[comboValuesArray.size()];

		comboValues = comboValuesArray.toArray(comboValues);

		jComboBoxClasificationField.setModel(new DefaultComboBoxModel(
				comboValues));
		jComboBoxMethod.setModel(new DefaultComboBoxModel());

		DefaultComboBoxModel modelType = (DefaultComboBoxModel) jComboBoxMethod
				.getModel();

		modelType.addElement("Linear");
		modelType.addElement("Logarithmic");
		modelType.addElement("Square");

		modelType.setSelectedItem("Linear");

		String field = leg.getClassificationField();
		jComboBoxClasificationField.setSelectedItem(field);

		jButtonFirstColor.setBackground(leg.getOutlineColor());
		jButtonSecondColor.setBackground(leg.getFillColor());

	}

	private JPanelProportionalLegend(int constraint, ILayer layer) {
		this(LegendFactory.createProportionalLegend(), constraint, layer);
	}

	public Legend getLegend() {
		ProportionalLegend leg = LegendFactory.createProportionalLegend();

		leg.setFillColor(jButtonSecondColor.getBackground());
		leg.setOutlineColor(jButtonFirstColor.getBackground());
		leg.setMinSymbolArea(Integer.parseInt(jTextFieldArea.getText()));
		try {
			int method = jComboBoxMethod.getSelectedIndex();
			switch (method) {
			case 0:
				leg.setLinearMethod();
				break;
			case 1:
				leg.setLogarithmicMethod();
				break;
			case 2:
				// TODO what is the Sqr Factor???
				leg.setSquareMethod(2);
				break;
			}

			leg.setClassificationField((String) jComboBoxClasificationField
					.getSelectedItem());
		} catch (DriverException e) {
			System.out.println("Driver Exception");
		}

		return leg;
	}

	protected Symbol createDefaultSymbol() {
		Symbol s;

		Color outline = jButtonFirstColor.getBackground();
		Color fillColor = jButtonSecondColor.getBackground();
		int size = 25;
		s = SymbolFactory.createCirclePointSymbol(outline, fillColor, size);

		return s;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBoxClasificationField = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxMethod = new javax.swing.JComboBox();
        jButtonSecondColor = new javax.swing.JButton();
        jButtonFirstColor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldArea = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Classification field:");

        jComboBoxClasificationField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxClasificationFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Method:");

        jComboBoxMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMethodActionPerformed(evt);
            }
        });

        jButtonSecondColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSecondColorActionPerformed(evt);
            }
        });

        jButtonFirstColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFirstColorActionPerformed(evt);
            }
        });

        jLabel3.setText("Line color:");

        jLabel4.setText("Fill color:");

        jLabel5.setText("Area: ");

        jTextFieldArea.setText("1000");
        jTextFieldArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldAreaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 102, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxClasificationField, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonFirstColor, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSecondColor, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldArea, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxClasificationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonFirstColor, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonSecondColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextFieldArea, 0, 0, Short.MAX_VALUE)
                            .addComponent(jLabel5)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void jComboBoxClasificationFieldActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxClasificationFieldActionPerformed
		if (dec != null)
			dec.setLegend(getLegend());
	}// GEN-LAST:event_jComboBoxClasificationFieldActionPerformed

	private void jComboBoxMethodActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxMethodActionPerformed
		if (dec != null)
			dec.setLegend(getLegend());
	}// GEN-LAST:event_jComboBoxMethodActionPerformed

	private void jTextFieldAreaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldAreaActionPerformed
		if (dec != null)
			dec.setLegend(getLegend());
	}// GEN-LAST:event_jTextFieldAreaActionPerformed

	private void jButtonSecondColorActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSecondColorActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonSecondColor.setBackground(color);
			refreshCanvas();
		}
	}// GEN-LAST:event_jButtonSecondColorActionPerformed

	private void jButtonFirstColorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFirstColorActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonFirstColor.setBackground(color);
			refreshCanvas();
		}
	}// GEN-LAST:event_jButtonFirstColorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonFirstColor;
    private javax.swing.JButton jButtonSecondColor;
    private javax.swing.JComboBox jComboBoxClasificationField;
    private javax.swing.JComboBox jComboBoxMethod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldArea;
    // End of variables declaration//GEN-END:variables
//	public String toString() {
//		// return "Unique symbol";
//		return identity;
//	}

	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

//	public String getInfoText() {
//		// TODO Auto-generated method stub
//		return "Set proportional legend to the selected layer";
//	}
//
//	public String getTitle() {
//		// TODO Auto-generated method stub
//		return "Proportional legend";
//	}
//
//	public void setIdentity(String id) {
//		identity = id;
//	}
//
//	public String getIdentity() {
//		return identity;
//	}

	public void setDecoratorListener(LegendListDecorator dec) {
		this.dec = dec;
	}

}