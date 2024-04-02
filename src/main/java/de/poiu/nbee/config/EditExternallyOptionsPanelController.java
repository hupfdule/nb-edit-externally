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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import static de.poiu.nbee.config.Prefs.NETBEANS_PREFS_ID;

/**
 * The controller for the configuration panel for the Edit Externally Plugin.
 * @author Marco Herrn
 */
@OptionsPanelController.SubRegistration(
  id = NETBEANS_PREFS_ID,
  displayName = "#AdvancedOption_DisplayName_EditExternally",
  keywords = "#AdvancedOption_Keywords_EditExternally",
  keywordsCategory = "Advanced/EditExternally"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_EditExternally=Edit Externally", "AdvancedOption_Keywords_EditExternally=edit externally"})
public final class EditExternallyOptionsPanelController extends OptionsPanelController {

  private EditExternallyPanel panel;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private boolean changed;


  @Override
  public void update() {
    getPanel().load();
    changed = false;
  }


  @Override
  public void applyChanges() {
    SwingUtilities.invokeLater(() -> {
      getPanel().store();
      changed = false;
    });
  }


  @Override
  public void cancel() {
    // need not do anything special, if no changes have been persisted yet
  }


  @Override
  public boolean isValid() {
    return getPanel().valid();
  }


  @Override
  public boolean isChanged() {
    return changed;
  }


  @Override
  public HelpCtx getHelpCtx() {
    return new HelpCtx("de.poiu.nbee.nb.editexternally.about");
//    return null; // new HelpCtx("...ID") if you have a help set
  }


  @Override
  public JComponent getComponent(Lookup masterLookup) {
    return getPanel();
  }


  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }


  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }


  private EditExternallyPanel getPanel() {
    if (panel == null) {
      panel = new EditExternallyPanel(this);
    }
    return panel;
  }


  void changed() {
    if (!changed) {
      changed = true;
      pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
    }
    pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
  }

}
