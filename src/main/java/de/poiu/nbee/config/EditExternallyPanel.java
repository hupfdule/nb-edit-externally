/*
 * Copyright 2019-2024 Marco Herrn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.poiu.nbee.config;

import de.poiu.nbee.parser.CmdlineParser;
import de.poiu.nbee.parser.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.openide.util.Lookup;

import static de.poiu.nbee.config.Prefs.CmdType.EDIT_EXTERNALLY_CMD;
import static de.poiu.nbee.config.Prefs.CmdType.OPEN_EXTERNALLY_CMD;


/**
 * The configuration panel for the Edit Externally Plugin.
 *
 * @author Marco Herrn
 */
final class EditExternallyPanel extends javax.swing.JPanel {

  private static final Logger LOGGER= Logger.getLogger(EditExternallyPanel.class.getName());

  private final EditExternallyOptionsPanelController controller;

  /** Parser to use for validating the command strings. */
  private final CmdlineParser cmdlineParser= new CmdlineParser();


  EditExternallyPanel(EditExternallyOptionsPanelController controller) {
    this.controller = controller;
    initComponents();
    // listen to changes in form fields and call controller.changed()
    addListeners();
  }

  private void addListeners() {
    for (final JTextComponent c : new JTextComponent[]{
      this.tfEditExternallyCmd,
      this.tfOpenExternallyCmd,
    }) {
      c.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          controller.changed();
          updateErrorMessages();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          controller.changed();
          updateErrorMessages();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          controller.changed();
          updateErrorMessages();
        }
      });
    }
  }


  private void updateErrorMessages() {
    //FIXME: Run error checks in background and trigger validation here?
    final List<String> errorMessages= new ArrayList<>();
    for (final JTextComponent c : new JTextComponent[]{
      this.tfEditExternallyCmd,
      this.tfOpenExternallyCmd,
    }) {
      try {
        this.cmdlineParser.parse(c.getText().trim());
      } catch (Exception ex) {
        errorMessages.add(ex.getMessage());
      }

      if (errorMessages.isEmpty()) {
        this.lblErrorMessages.setText("");
      } else {
        final StringBuilder sb= new StringBuilder();
        sb.append("<html>");
        for (final String errorMsg : errorMessages) {
          sb.append(errorMsg);
          sb.append("<br/>");
        }
        sb.delete(sb.length() - 5, sb.length()); // delete the last <br/>
        sb.append("</html>");
        this.lblErrorMessages.setText(sb.toString());
      }
    }
  }


  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    lblEditExternallyCmd = new javax.swing.JLabel();
    tfEditExternallyCmd = new javax.swing.JTextField();
    lblErrorMessages = new javax.swing.JLabel();
    lblOpenExternallyCmd = new javax.swing.JLabel();
    tfOpenExternallyCmd = new javax.swing.JTextField();

    org.openide.awt.Mnemonics.setLocalizedText(lblEditExternallyCmd, org.openide.util.NbBundle.getMessage(EditExternallyPanel.class, "EditExternallyPanel.lblEditExternallyCmd.text")); // NOI18N

    tfEditExternallyCmd.setText(org.openide.util.NbBundle.getMessage(EditExternallyPanel.class, "EditExternallyPanel.tfEditExternallyCmd.text")); // NOI18N

    lblErrorMessages.setForeground(new java.awt.Color(255, 0, 0));
    org.openide.awt.Mnemonics.setLocalizedText(lblErrorMessages, org.openide.util.NbBundle.getMessage(EditExternallyPanel.class, "EditExternallyPanel.lblErrorMessages.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(lblOpenExternallyCmd, org.openide.util.NbBundle.getMessage(EditExternallyPanel.class, "EditExternallyPanel.lblOpenExternallyCmd.text")); // NOI18N

    tfOpenExternallyCmd.setText(org.openide.util.NbBundle.getMessage(EditExternallyPanel.class, "EditExternallyPanel.tfOpenExternallyCmd.text")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(lblErrorMessages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(lblOpenExternallyCmd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblEditExternallyCmd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(tfEditExternallyCmd, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
              .addComponent(tfOpenExternallyCmd))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lblEditExternallyCmd)
          .addComponent(tfEditExternallyCmd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lblOpenExternallyCmd)
          .addComponent(tfOpenExternallyCmd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
        .addComponent(lblErrorMessages)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents


  void load() {
    final Prefs prefs= Lookup.getDefault().lookup(Prefs.class);
    this.tfEditExternallyCmd.setText(prefs.load(EDIT_EXTERNALLY_CMD));
    this.tfOpenExternallyCmd.setText(prefs.load(OPEN_EXTERNALLY_CMD));
  }


  void store() {
    final Prefs prefs= Lookup.getDefault().lookup(Prefs.class);
    prefs.store(EDIT_EXTERNALLY_CMD, this.tfEditExternallyCmd.getText());
    prefs.store(OPEN_EXTERNALLY_CMD, this.tfOpenExternallyCmd.getText());
  }


  boolean valid() {
    try {
      this.cmdlineParser.parse(this.tfEditExternallyCmd.getText());
      this.cmdlineParser.parse(this.tfOpenExternallyCmd.getText());
      return true;
    } catch (ParseException ex) {
      return false;
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel lblEditExternallyCmd;
  private javax.swing.JLabel lblErrorMessages;
  private javax.swing.JLabel lblOpenExternallyCmd;
  private javax.swing.JTextField tfEditExternallyCmd;
  private javax.swing.JTextField tfOpenExternallyCmd;
  // End of variables declaration//GEN-END:variables

}
