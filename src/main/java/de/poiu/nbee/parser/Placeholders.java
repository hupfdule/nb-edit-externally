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
package de.poiu.nbee.parser;


/**
 * The placeholders this plugin's configured commands may contain.
 * <p>
 * Kept in one single place so that the actual replacement values (set in
 * {@code de.poiu.nbee.actions.EditExternally} when a command is really executed) and the dummy
 * values used for pure syntax validation (in {@code de.poiu.nbee.config.EditExternallyPanel})
 * can't drift out of sync with each other.
 *
 * @author Marco Herrn
 */
public final class Placeholders {

  public static final String FILE                     = "${file}";
  public static final String FILE_NAME                = "${fileName}";
  public static final String FILE_BASENAME            = "${fileBasename}";
  public static final String FILE_EXT                 = "${fileExt}";
  public static final String LINE0                    = "${line0}";
  public static final String LINE                     = "${line}";
  public static final String COLUMN0                  = "${column0}";
  public static final String COLUMN                   = "${column}";
  public static final String SELECTED_TEXT            = "${selectedText}";
  public static final String SELECTION_START0         = "${selectionStart0}";
  public static final String SELECTION_START          = "${selectionStart}";
  public static final String SELECTION_END0           = "${selectionEnd0}";
  public static final String SELECTION_END            = "${selectionEnd}";
  public static final String SELECTION_END_EXCLUSIVE0 = "${selectionEndExclusive0}";
  public static final String SELECTION_END_EXCLUSIVE  = "${selectionEndExclusive}";

  /**
   * Placeholders that are always available, regardless of command type ("edit externally" or
   * "open externally").
   */
  public static final String[] ALWAYS_AVAILABLE= {
    FILE,
    FILE_NAME,
    FILE_BASENAME,
    FILE_EXT,
  };

  /**
   * Placeholders that are only available for the "edit externally" command, since they require
   * a known cursor location in an open editor.
   */
  public static final String[] EDITOR_ONLY= {
    LINE0,
    LINE,
    COLUMN0,
    COLUMN,
    SELECTED_TEXT,
    SELECTION_START0,
    SELECTION_START,
    SELECTION_END0,
    SELECTION_END,
    SELECTION_END_EXCLUSIVE0,
    SELECTION_END_EXCLUSIVE,
  };


  private Placeholders() {
  }
}
