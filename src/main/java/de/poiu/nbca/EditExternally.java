/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

import static de.poiu.nbca.Prefs.OPTION_EDIT_EXTERNALLY_CMD;
import static de.poiu.nbca.Prefs.PREFS_PREFIX;


@ActionID(
  category = "Tools",
  id = "de.poiu.nbca.EditExternally"
)
@ActionRegistration(
  iconBase = "de/poiu/nbca/knode.png",
  displayName = "#CTL_EditExternally"
)
@ActionReferences({
  @ActionReference(path = "Menu/Tools/Custom Actions", position = 0),
  @ActionReference(path = "Toolbars/File", position = 1000),
  @ActionReference(path = "Editors/Toolbars/Default", position = 1000),
  @ActionReference(path = "Editors/Popup", position = 1425, separatorBefore = 1422),
  @ActionReference(path = "Editors/TabActions"),
  @ActionReference(path = "UI/ToolActions/Files")
})
@Messages("CTL_EditExternally=Edit Externally")
public final class EditExternally implements ActionListener {


  @Override
  public void actionPerformed(ActionEvent ev) {
//    final Cmd cmd= new Cmd("Edit in Vim", "urxvt -e nvim ${file} \"+call cursor(${line}, ${column})\"");

    final String cmdLine= NbPreferences.forModule(Prefs.class).get(PREFS_PREFIX + OPTION_EDIT_EXTERNALLY_CMD, "");

    if (cmdLine == null || cmdLine.trim().isEmpty()) {
      final String msg= "<html>No command to open file externally is defined yet.<br/>Open configuration panel now?</html>";
      final NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
      final Object result = DialogDisplayer.getDefault().notify(nd);
      if (NotifyDescriptor.YES_OPTION == result) {
        OptionsDisplayer.getDefault().open("Advanced"+ "/de.poiu.nbca.CustomActionsOptions");
      }
    } else {
      final Cmd cmd= new Cmd("Edit externally", cmdLine);
      new Executor().execute(cmd);
    }
  }
}
