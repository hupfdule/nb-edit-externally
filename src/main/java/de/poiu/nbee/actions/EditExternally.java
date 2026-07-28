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
package de.poiu.nbee.actions;

import de.poiu.nbee.config.Prefs;
import de.poiu.nbee.config.Prefs.CmdType;
import de.poiu.nbee.parser.CmdlineParser;
import de.poiu.nbee.parser.ParseException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

import static de.poiu.nbee.config.Prefs.CmdType.EDIT_EXTERNALLY_CMD;
import static de.poiu.nbee.config.Prefs.CmdType.OPEN_EXTERNALLY_CMD;
import static de.poiu.nbee.config.Prefs.NETBEANS_PREFS_ID;


/**
 * An action to edit the current file in an external editor.
 * <p>
 * This calls the command line the user has configured for this action.
 * If the command line is not configured yet, the user is informed and gets the opportunity to open
 * the configuration dialog for configuring it.
 * <p>
 * Two types of command need to be configured. The first one, "edit externally", is used when the
 * current editor is available and the current location of the cursor in this is known.
 * If the cursor position is not known for the current file, the "open externally" command is used
 * that doesn't support location information to be configured.
 * <p>
 * Implementation note: this uses NetBeans' declarative context-action recipe (a constructor
 * taking a {@code List<DataObject>} plus implementing plain {@link ActionListener}) instead of
 * hand-rolling {@code AbstractAction implements ContextAwareAction, LookupListener}. The
 * {@code @ActionRegistration} annotation processor recognizes this constructor shape and
 * registers the action via {@link org.openide.awt.Actions#context}, which already tracks
 * {@code Utilities.actionsGlobalContext()} correctly for every placement (toolbar, menu, and
 * popup alike) on its own. This sidesteps the previous problem where NetBeans bound an eagerly
 * instantiated, no-arg-constructed action instance directly to the toolbar/menu placements
 * without ever calling {@code createContextAwareInstance(Lookup)}. That special case no longer
 * needs to be worked around by hand, since there is no no-arg-constructed instance in this recipe
 * to begin with. The (at least) one selected {@code DataObject} required for this action to even
 * be enabled is delivered directly as this constructor's argument.
 *
 * @author Marco Herrn
 */
@ActionID(
  category = "Tools",
  id = "de.poiu.nbee.EditExternally"
)
@ActionRegistration(
  iconBase = "de/poiu/nbee/icons/edit-externally.png",
  displayName = "#CTL_EditExternally"
)
@ActionReferences({
//  @ActionReference(path = "Menu/Tools", position = 0), //redundant if "UI/ToolActions/Files" is registered
  @ActionReference(path = "Toolbars/File", position = 1002),
  @ActionReference(path = "Editors/Toolbars/Default", position = 10002),
  @ActionReference(path = "Editors/Popup", position = 1425, separatorBefore = 1422),
//  @ActionReference(path = "Editors/TabActions"), // disabled. Always opens the _current_ editor, not the clicked one. Same problem with 'Copy file path'
  @ActionReference(path = "UI/ToolActions/Files", position = 2502), // Menu/Tools _and_ Tools popup menu"?
})
@Messages({
  "CTL_EditExternally=Edit Externally",
  "# {0} - the file to be edited",
  "CTL_Editing_Status=Editing file {0} in external editor",
  "# {0} - the file to be opened",
  "# {1} - the reason for error",
  "CTL_Editing_Error=Error opening external editor for {0}: {1}",
  "MSG_NoCommand_Edit=<html>No command to edit file externally is defined yet.<br/>Open configuration panel now?</html>",
  "MSG_NoCommand_Open=<html>No command to open file externally is defined yet.<br/>Open configuration panel now?</html>",
  "# {0} - the reason the configured command could not be parsed",
  "MSG_InvalidCommand_Edit=<html>The configured command to edit file externally is invalid:<br/>{0}<br/>Open configuration panel now?</html>",
  "# {0} - the reason the configured command could not be parsed",
  "MSG_InvalidCommand_Open=<html>The configured command to open file externally is invalid:<br/>{0}<br/>Open configuration panel now?</html>"})
public final class EditExternally implements ActionListener {

  private static final Logger LOGGER= Logger.getLogger(EditExternally.class.getName());

  /**
   * The currently selected DataObjects this action was invoked on. NetBeans only enables (and
   * thus only ever invokes) this action when this list is non-empty; see the class-level note.
   */
  private final List<DataObject> context;


  public EditExternally(final List<DataObject> context) {
    this.context= context;
  }


  @Override
  public void actionPerformed(ActionEvent ev) {
    LOGGER.entering("EditExternally", "actionPerformed", ev);

    final DataObject dataObject= this.context.isEmpty() ? null : this.context.get(0);
    final FileObject file      = this.getFileObjectFrom(dataObject);
    if (file == null) {
      LOGGER.log(Level.INFO, "Ignoring execution request, since no current file was found");
      return;
    }

    final JTextComponent editor= getCurrentEditor(dataObject);
    final StyledDocument sdocument = editor != null ? (StyledDocument) editor.getDocument() : null;

    final CmdType cmdType;
    if (editor == null) {
      LOGGER.log(Level.INFO, "Calling 'open external' command since no current editor was found.");
      cmdType= OPEN_EXTERNALLY_CMD;
    } else if (sdocument == null) {
      LOGGER.log(Level.INFO, "Calling 'open external' command since the current editor's document is not a StyledDocument.");
      cmdType= OPEN_EXTERNALLY_CMD;
    } else {
      LOGGER.log(Level.INFO, "Calling 'edit external' command with location information of current editor");
      cmdType= EDIT_EXTERNALLY_CMD;
    }

    final Prefs prefs= Lookup.getDefault().lookup(Prefs.class);
    final String cmdLine= prefs.load(cmdType.name());

    if (cmdLine == null || cmdLine.trim().isEmpty()) {
      this.openOptionsPanel(cmdType);
    } else {
      final File actualFile= FileUtil.toFile(file);

      final CmdlineParser cmdlineParser= new CmdlineParser();

      cmdlineParser
        .replace("${file}", actualFile.getAbsolutePath())
        .replace("${fileName}", file.getNameExt())
        .replace("${fileBasename}", file.getName())
        .replace("${fileExt}", file.getExt())
        ;

      if (cmdType == EDIT_EXTERNALLY_CMD) {
        final int caret= editor.getCaretPosition();
        final int line0= NbDocument.findLineNumber(sdocument, caret);
        final int column0= NbDocument.findLineColumn(sdocument, caret);
        final String selectedText= editor.getSelectedText();

        cmdlineParser
          .replace("${line0}", String.valueOf(line0))
          .replace("${line}", String.valueOf(line0 + 1))
          .replace("${column0}", String.valueOf(column0))
          .replace("${column}", String.valueOf(column0 + 1))
          .replace("${selectedText}", selectedText != null ? selectedText : "")
          .replace("${selectionStart}", String.valueOf(editor.getSelectionStart()))
          .replace("${selectionEnd}", String.valueOf(editor.getSelectionEnd()))
          ;
      }

      final String[] command;
      try {
        command= cmdlineParser.parse(cmdLine.trim());
      } catch (ParseException ex) {
        this.openOptionsPanelForInvalidCommand(cmdType, ex);
        return;
      }

      LOGGER.log(Level.INFO, "Calling command {0}", Arrays.toString(command));

      try {
        final String msg= Bundle.CTL_Editing_Status(file.getPath());
        StatusDisplayer.getDefault().setStatusText(msg);

        // Use ProcessBuilder instead of Runtime.exec() and explicitly discard stdout/stderr.
        // Otherwise, if the started editor writes a nontrivial amount of output (e.g. a
        // terminal-based editor), the OS pipe buffer can fill up and block the child process
        // without any indication to the user, since nothing in NetBeans ever reads that output.
        //
        // The working directory is explicitly set to the edited file's directory instead of
        // leaving it at NetBeans' own (platform-dependent, effectively undefined) working directory.
        new ProcessBuilder(command)
          .directory(actualFile.getParentFile())
          .redirectOutput(ProcessBuilder.Redirect.DISCARD)
          .redirectError(ProcessBuilder.Redirect.DISCARD)
          .start();
      } catch (IOException ex) {
        final String msg = Bundle.CTL_Editing_Error(file.getPath(), ex.getLocalizedMessage());
        StatusDisplayer.getDefault().setStatusText(msg);
        Exceptions.printStackTrace(ex);
      }
    }
  }


  /**
   * Opens the Options Panel for configuring this plugin.
   * <p>
   * This should be called if a certain <code>CmdType</code> is not configured yet.
   * <p>
   * It informs the user of the missing configuration and provides the opportunity to open the
   * Options Panel for this plugin.
   *
   * @param cmdType the command type that is not configured yet
   */
  private void openOptionsPanel(final CmdType cmdType) {
    final String msg= cmdType == EDIT_EXTERNALLY_CMD
      ? Bundle.MSG_NoCommand_Edit()
      : Bundle.MSG_NoCommand_Open();
    this.confirmAndOpenOptionsPanel(msg);
  }


  /**
   * Opens the Options Panel for configuring this plugin.
   * <p>
   * This should be called if the configured command for a certain <code>CmdType</code> could
   * not be parsed.
   * <p>
   * It informs the user of the invalid configuration (including the parse error), logs the
   * exception, and provides the opportunity to open the Options Panel for this plugin.
   *
   * @param cmdType the command type whose configured command is invalid
   * @param ex the exception describing why the configured command could not be parsed
   */
  private void openOptionsPanelForInvalidCommand(final CmdType cmdType, final ParseException ex) {
    LOGGER.log(Level.WARNING, "Configured command for " + cmdType + " could not be parsed", ex);
    final String msg= cmdType == EDIT_EXTERNALLY_CMD
      ? Bundle.MSG_InvalidCommand_Edit(ex.getLocalizedMessage())
      : Bundle.MSG_InvalidCommand_Open(ex.getLocalizedMessage());
    this.confirmAndOpenOptionsPanel(msg);
  }


  /**
   * Asks the user (via the given, already fully formatted HTML message) whether to open the
   * Options Panel for this plugin now, and opens it if confirmed.
   *
   * @param htmlMessage the fully formatted (including surrounding <code>&lt;html&gt;</code> tags)
   *                     confirmation message to show to the user
   */
  private void confirmAndOpenOptionsPanel(final String htmlMessage) {
    final NotifyDescriptor nd = new NotifyDescriptor.Confirmation(htmlMessage, NotifyDescriptor.YES_NO_OPTION);
    final Object result       = DialogDisplayer.getDefault().notify(nd);
    if (NotifyDescriptor.YES_OPTION == result) {
      OptionsDisplayer.getDefault().open("Advanced"+ "/" + NETBEANS_PREFS_ID);
    }
  }


  /**
   * Returns the actual <code>FileObject</code> for the given <code>DataObject</code>
   * @param dataObject the <code>DataObject</code> for which to return the <code>FileObject</code>
   * @return the <code>FileObject</code> for the given <code>DataObject</code>
   */
  private FileObject getFileObjectFrom(DataObject dataObject) {
    if (dataObject == null) {
      return null;
    }

    if (dataObject instanceof DataShadow){
      dataObject = ((DataShadow) dataObject).getOriginal();
    }

    return dataObject.getPrimaryFile();
  }


  /**
   * Tries to find the current editor for the given <code>DataObject</code>.
   * <p>
   * If no current editor can be found, <code>null</code> is returned.
   *
   * @param dataObject the <code>DataObject</code> for which to find the current editor
   * @return the current editor or <code>null</code> if no editor could be found.
   */
  private JTextComponent getCurrentEditor(final DataObject dataObject) {
    final EditorCookie ec= dataObject.getLookup().lookup(EditorCookie.class);
    if (ec == null) {
      LOGGER.log(Level.INFO, "No EditorCookie found for the current DataObject");
      return null;
    }

    final JEditorPane[] editorPanes = ec.getOpenedPanes();
    if (editorPanes == null || editorPanes.length == 0) {
      return null;
    } else if (editorPanes.length == 1) {
      return editorPanes[0];
    } else {
      LOGGER.log(Level.WARNING, "More than one editor pane found for current editor ({0} panes)", editorPanes.length);
      return editorPanes[0];
    }
  }
}
