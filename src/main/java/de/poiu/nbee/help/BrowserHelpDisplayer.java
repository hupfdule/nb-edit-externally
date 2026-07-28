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
package de.poiu.nbee.help;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;


/**
 * Displays help for this plugin by opening the online documentation in the system's default
 * web browser instead of relying on JavaHelp.
 * <p>
 * Apache NetBeans dropped JavaHelp support when the project was donated to the ASF (its
 * license is incompatible with the Apache License) and never replaced it with a working
 * alternative. As a result {@link HelpCtx#display()} silently does nothing for a help ID
 * that would previously have been resolved via a bundled JavaHelp help set. This displayer
 * restores a working "Help" button for this plugin by recognizing its own help IDs and
 * pointing to the project's online documentation instead.
 * <p>
 * The URL is opened via the JDK's own {@link Desktop} API rather than NetBeans' built-in
 * {@link HtmlBrowser.URLDisplayer}. The latter is implemented by the {@code extbrowser}
 * module, which on some setups fails to launch a browser (missing native library, stale
 * DDE-based Windows code path, ...) while only logging a warning in the background.
 * From the caller's point of view nothing happens and no exception is thrown. {@link Desktop}
 * talks to the OS directly and reports failure synchronously, so it is used as the primary
 * mechanism; {@link HtmlBrowser.URLDisplayer} remains a best-effort fallback for the rare case
 * that {@link Desktop} itself is unsupported (e.g. some minimal/headless setups).
 *
 * @author Marco Herrn
 */
@Messages({
  "# {0} - the help URL that could not be opened automatically",
  "MSG_CouldNotOpenHelpUrl=<html>The online documentation could not be opened automatically.<br/>Please open the following URL manually:<br/>{0}</html>"})
@ServiceProvider(service = HelpCtx.Displayer.class)
public final class BrowserHelpDisplayer implements HelpCtx.Displayer {

  private static final Logger LOGGER= Logger.getLogger(BrowserHelpDisplayer.class.getName());

  /** The prefix of the help IDs this displayer feels responsible for. */
  private static final String HELP_ID_PREFIX= "de.poiu.nbee.";

  /** The online documentation to open for this plugin's help IDs. */
  private static final String HELP_URL= "https://github.com/hupfdule/nb-edit-externally#configuration";


  @Override
  public boolean display(final HelpCtx helpCtx) {
    final String helpId= helpCtx.getHelpID();
    if (helpId == null || !helpId.startsWith(HELP_ID_PREFIX)) {
      // Not one of our help IDs -- leave it to another (or no) Displayer to handle.
      return false;
    }

    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      try {
        Desktop.getDesktop().browse(new URI(HELP_URL));
        return true;
      } catch (IOException | URISyntaxException ex) {
        // Desktop.browse() reports failure synchronously and reliably, unlike
        // HtmlBrowser.URLDisplayer (see class javadoc). So on failure here we know for
        // certain that no browser opened and can inform the user instead of failing silently.
        LOGGER.log(Level.WARNING, "Could not open help URL " + HELP_URL + " via java.awt.Desktop", ex);
        this.notifyCouldNotOpen();
        return true;
      }
    }

    // Desktop is not supported on this platform at all. Fall back to NetBeans' own
    // mechanism as a best effort. Its success can not be reliably determined from here
    // (see class javadoc), so we neither retry nor show the manual-URL notification for it.
    try {
      HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(HELP_URL));
    } catch (MalformedURLException ex) {
      LOGGER.log(Level.WARNING, "Could not open help URL " + HELP_URL + " via HtmlBrowser.URLDisplayer", ex);
    }
    return true;
  }


  private void notifyCouldNotOpen() {
    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
      Bundle.MSG_CouldNotOpenHelpUrl(HELP_URL), NotifyDescriptor.WARNING_MESSAGE));
  }
}
