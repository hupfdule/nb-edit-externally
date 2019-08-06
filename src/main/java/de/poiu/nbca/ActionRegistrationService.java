/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;


/**
 *
 * @author wwinder
 */
@ServiceProvider(service=ActionRegistrationService.class)
public class ActionRegistrationService {

    public List<Action> findActions(final String category) throws IOException {
      final List<Action> actions= new ArrayList<Action>();

      final FileObject in = getFolderAt("Actions/" + category + "/Nbca");
      final FileObject[] instanceFiles = in.getChildren();
      for (final FileObject obj : instanceFiles) {
        // FIXME: check type
        System.out.println("instanceClass: "+obj.getAttribute("instanceClass"));
        System.out.println("instanceCreate: "+obj.getAttribute("instanceCreate"));
        actions.add((Action) obj.getAttribute("instanceCreate"));
      }

      return actions;
    }


    /**
     * Registers an action with the platform along with optional shortcuts and
     * menu items.
     * @param name Display name of the action.
     * @param category Category in the Keymap tool.
     * @param shortcut Default shortcut, use an empty string or null for none.
     * @param menuPath Menu location starting with "Menu", like "Menu/File"
     * @param action an action object to attach to the action entry.
     * @throws IOException
     */
    public void registerAction(String name, String category, String shortcut, String menuPath, Action action) throws IOException {
        ///////////////////////
        // Add/Update Action //
        ///////////////////////
        final String originalFile = "Actions/" + category + "/Nbca/" + name + ".instance";
        FileObject in = getFolderAt("Actions/" + category + "/Nbca");
        FileObject obj = in.getFileObject(name, "instance");
        if (obj == null) {
            obj = in.createData(name, "instance");
        }
        action.putValue(Action.NAME, name);
        obj.setAttribute("instanceCreate", action);
        obj.setAttribute("instanceClass", action.getClass().getName());

        /////////////////////
        // Add/Update Menu //
        /////////////////////
        if (menuPath != null) {
          in = getFolderAt(menuPath);
          obj = in.getFileObject(name, "shadow");
          // Create if missing.
          if (obj == null) {
            obj = in.createData(name, "shadow");
            obj.setAttribute("originalFile", originalFile);
          }
        }

        /////////////////////////
        // Add/Update Shortcut //
        /////////////////////////
        if (shortcut != null) {
          in = getFolderAt("Shortcuts");
          obj = in.getFileObject(shortcut, "shadow");
          if (obj == null) {
            obj = in.createData(shortcut, "shadow");
            obj.setAttribute("originalFile", originalFile);
          }
        }
    }


    private FileObject getFolderAt(String inputPath) throws IOException {
        final String parts[] = inputPath.split("/");
        final FileObject existing = FileUtil.getConfigFile(inputPath);
        if (existing != null)
            return existing;

        FileObject base = FileUtil.getConfigFile(parts[0]);
        if (base == null) return null;

        for (int i = 1; i < parts.length; i++) {
            final String path = joinPath(Arrays.copyOfRange(parts,0,i+1));
            FileObject next = FileUtil.getConfigFile(path);
            if (next == null) {
                next = base.createFolder(parts[i]);
            }
            base = next;
        }

        return FileUtil.getConfigFile(inputPath);
    }


    protected static String joinPath(final Object[] array) {
      if (array == null) {
        return null;
      }

      if (array.length == 0) {
        return "";
      }

      final StringBuilder sb= new StringBuilder();
      for (int i= 0; i < array.length - 1; i++) {
        sb.append(array[i]);
        sb.append("/");
      }
      sb.append(array[array.length-1]);

      return sb.toString();
    }
}