/*
 * Copyright 2019-2026 Marco Herrn.
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;


/**
 * Theme-aware colors and icons for displaying validation-style error/warning messages (see
 * {@link EditExternallyPanel}), consistent with NetBeans' own Look-and-Feel-dependent
 * conventions rather than hardcoded literals.
 *
 * @author Marco Herrn
 */
final class MessageStyle {

  /**
   * Text color for real syntax errors (a command that can't be parsed at all).
   * <p>
   * Resolved via the {@code nb.errorForeground} UIManager key that NetBeans' own
   * "Look &amp; Feel Customization" library ({@code LFCustoms}) installs per active Look and
   * Feel at startup — the same key (and fallback pattern) {@code org-openide-dialogs} itself
   * uses internally. This keeps the color legible across different themes (including dark
   * themes), instead of a color literal that might clash with an arbitrary theme's background.
   */
  static final Color COLOR_ERROR= resolveColor("nb.errorForeground", new Color(0xCC0000));

  /**
   * Text color for warnings (a command that parses fine but contains an unknown placeholder,
   * which will simply be included literally when the command is actually executed).
   * <p>
   * See {@link #COLOR_ERROR} for how and why this is resolved via UIManager.
   */
  static final Color COLOR_WARNING= resolveColor("nb.warningForeground", new Color(0xB8860B));

  /**
   * The standard error/warning icons of the active Look and Feel (the same ones used by
   * {@code JOptionPane}), scaled down to 16x16 — NetBeans' usual small-icon size, since the
   * native {@code JOptionPane} icons are sized for a dialog's large icon area, not for an
   * inline message row.
   */
  static final Icon ICON_ERROR  = scaled(UIManager.getIcon("OptionPane.errorIcon"), 16);
  static final Icon ICON_WARNING= scaled(UIManager.getIcon("OptionPane.warningIcon"), 16);



  private MessageStyle() {
  }

  

  private static Color resolveColor(final String uiManagerKey, final Color fallback) {
    final Color color= UIManager.getColor(uiManagerKey);
    return color != null ? color : fallback;
  }


  /**
   * Renders {@code icon} onto an offscreen image and returns a copy scaled down to a
   * {@code size}x{@code size} square. Rendering (rather than casting to {@code ImageIcon} and
   * scaling its image directly) works regardless of the concrete {@link Icon} implementation
   * the active Look and Feel happens to use.
   */
  private static Icon scaled(final Icon icon, final int size) {
    if (icon == null) {
      return null;
    }
    final BufferedImage buffer= new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g= buffer.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();
    return new ImageIcon(buffer.getScaledInstance(size, size, Image.SCALE_SMOOTH));
  }
}
