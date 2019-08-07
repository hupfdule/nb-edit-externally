/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import de.poiu.nbca.config.CustomActions2OptionsPanelController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.OnStart;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;


/**
 *
 * @author mherrn
 */
@OnStart
public class Startup implements Runnable {

  private static final Logger LOGGER= Logger.getLogger(Startup.class.getName());

  private static final String PREFS_PREFIX= "CustomAction-";

  @Override
  public void run() {
    //TODO: Register actions in IDE
    String msg = "Mir Startet... ";//+CustomActions2OptionsPanelController.SubRegistration.id();
    NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
    DialogDisplayer.getDefault().notify(nd);
//
//    final ActionRegistrationService ars= Lookup.getDefault().lookup(ActionRegistrationService.class);
//
//    final Cmd cmd= new Cmd("Die Edith", "urxvt -e nvim ${file} \"+call cursor(${line}, ${colunn})\"");
//    if (!cmd.isEmpty()) {
//      NbPreferences.forModule(Startup.class).put(PREFS_PREFIX + cmd.getTitle(), cmd.getCmdLine());
//
//      try {
//        final NbcaAction action= new NbcaAction(cmd);
//        LOGGER.log(Level.INFO, "Registering (or updating) action for Cmd {0}", action.getCmd());
//        ars.registerAction(cmd.getTitle(), "Tools", "CA-E", "Menu/Tools/Custom Actions", action);
//      } catch (IOException ex) {
//        Exceptions.printStackTrace(ex);
//      }
//    }

  }

}