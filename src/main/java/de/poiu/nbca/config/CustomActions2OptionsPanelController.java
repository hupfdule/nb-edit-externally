/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca.config;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;


@OptionsPanelController.SubRegistration(
  id = "de.poiu.nbca.CustomActionsOptions",
  displayName = "#AdvancedOption_DisplayName_CustomActions2",
  keywords = "#AdvancedOption_Keywords_CustomActions2",
  keywordsCategory = "Advanced/CustomActions2"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_CustomActions2=Custom Actions", "AdvancedOption_Keywords_CustomActions2=custom actions"})
public final class CustomActions2OptionsPanelController extends OptionsPanelController {

  private CustomActions2Panel panel;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private boolean changed;


  public void update() {
    getPanel().load();
    changed = false;
  }


  public void applyChanges() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getPanel().store();
        changed = false;
      }
    });
  }


  public void cancel() {
    // need not do anything special, if no changes have been persisted yet
  }


  public boolean isValid() {
    return getPanel().valid();
  }


  public boolean isChanged() {
    return changed;
  }


  public HelpCtx getHelpCtx() {
    return null; // new HelpCtx("...ID") if you have a help set
  }


  public JComponent getComponent(Lookup masterLookup) {
    return getPanel();
  }


  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }


  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }


  private CustomActions2Panel getPanel() {
    if (panel == null) {
      panel = new CustomActions2Panel(this);
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
