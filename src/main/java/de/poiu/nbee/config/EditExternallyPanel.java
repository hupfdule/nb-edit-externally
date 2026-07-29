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
import de.poiu.nbee.parser.Placeholders;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

import static de.poiu.nbee.config.Prefs.CmdType.EDIT_EXTERNALLY_CMD;
import static de.poiu.nbee.config.Prefs.CmdType.OPEN_EXTERNALLY_CMD;


/**
 * The configuration panel for the Edit Externally Plugin.
 *
 * @author Marco Herrn
 */
@Messages({
  "# {0} - the comma-separated list of unknown placeholders found",
  "MSG_UnknownPlaceholders=Unknown placeholder(s), will be included literally: {0}"})
final class EditExternallyPanel extends javax.swing.JPanel {

  private static final Logger LOGGER= Logger.getLogger(EditExternallyPanel.class.getName());

  /** Text color for real syntax errors (a command that can't be parsed at all). */
  private static final String COLOR_ERROR= "#CC0000";

  /**
   * Text color for warnings (a command that parses fine but contains an unknown placeholder,
   * which will simply be included literally when the command is actually executed).
   */
  private static final String COLOR_WARNING= "#B8860B";

  private final EditExternallyOptionsPanelController controller;

  /**
   * Parser to use for validating the command strings.
   * <p>
   * This is only ever used to check for syntax errors (via {@link CmdlineParser#parse}), never
   * to build an actually executable command. So every known placeholder (see
   * {@link Placeholders}) is mapped to an arbitrary dummy value here. Otherwise every valid,
   * known placeholder would be logged as "unmapped" on every keystroke, drowning out the one
   * case that log message is actually meant to catch: a real, misspelled placeholder in the
   * command as configured for actual execution in {@code EditExternally}.
   */
  private final CmdlineParser cmdlineParser= newValidatingCmdlineParser();

  private static CmdlineParser newValidatingCmdlineParser() {
    final CmdlineParser parser= new CmdlineParser();
    for (final String placeholder : Placeholders.ALWAYS_AVAILABLE) {
      parser.replace(placeholder, "dummyValue");
    }
    for (final String placeholder : Placeholders.EDITOR_ONLY) {
      parser.replace(placeholder, "dummyValue");
    }
    return parser;
  }


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
    final List<String> messages= new ArrayList<>();
    for (final JTextComponent c : new JTextComponent[]{
      this.tfEditExternallyCmd,
      this.tfOpenExternallyCmd,
    }) {
      try {
        final CmdlineParser.ParseResult result= this.cmdlineParser.parseDetailed(c.getText().trim());
        if (!result.unmappedPlaceholders().isEmpty()) {
          messages.add(colored(COLOR_WARNING, Bundle.MSG_UnknownPlaceholders(String.join(", ", result.unmappedPlaceholders()))));
        }
      } catch (Exception ex) {
        messages.add(colored(COLOR_ERROR, ex.getMessage()));
      }
    }

    if (messages.isEmpty()) {
      this.lblErrorMessages.setText("");
    } else {
      this.lblErrorMessages.setText("<html>" + String.join("<br/>", messages) + "</html>");
    }
  }


  private static String colored(final String color, final String message) {
    return "<span style=\"color:" + color + ";\">" + message + "</span>";
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
      this.cmdlineParser.parse(this.tfEditExternallyCmd.getText().trim());
      this.cmdlineParser.parse(this.tfOpenExternallyCmd.getText().trim());
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
