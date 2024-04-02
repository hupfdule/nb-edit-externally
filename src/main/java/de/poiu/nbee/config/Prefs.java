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

import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;


/**
 * Helper for loading and storing information in the Netbeans properties.
 *
 * @author Marco Herrn
 */
@ServiceProvider(service = Prefs.class)
public class Prefs {

  /** ID for the Netbeans Preferences for this plugin. */
  public static final String NETBEANS_PREFS_ID= "de.poiu.nbee.EditExternallyOptions";

  /** Common prefix for all settings of this plugin */
  public static final String PREFS_PREFIX= "EditExternally-";

  /** The of the command */
  public static enum CmdType {
    /** Command to edit a file with an external editor at a specific cursor location */
    EDIT_EXTERNALLY_CMD,
    /** Command to open a file with an external editor */
    OPEN_EXTERNALLY_CMD,
    ;
  }


  /**
   * Loads the currently stored value of the given key.
   * <p>
   * If the key is not configured yet, this returns an empty string.
   *
   * @param key the key to look up
   * @return the value for the given key or the empty string if not configured.
   */
  public String load(final String key) {
    return load(key, "");
  }


  /**
   * Loads the currently stored value of the given command type.
   * <p>
   * If the given command type is not configured yet, this returns an empty string.
   *
   * @param cmdType the command type to look up
   * @return the value for the given command type or the empty string if not configured.
   */
  public String load(final CmdType cmdType) {
    return load(cmdType.name(), "");
  }


  /**
   * Loads the currently stored value of the given key.
   * <p>
   * If the given key is not configured yet, this returns the given default value.
   *
   * @param key the command type to look up
   * @param defaultValue the default value to return if the given <code>string</code> is not configured
   * @return the value for the given command type or the given default value if not configured.
   */
  public String load(final String key, final String defaultValue) {
    return NbPreferences.forModule(Prefs.class).get(PREFS_PREFIX + key, defaultValue);
  }


  /**
   * Loads the currently stored value of the given command type.
   * <p>
   * If the given command type is not configured yet, this returns the given default value.
   *
   * @param cmdType the command type to look up
   * @param defaultValue the default value to return if the given <code>cmdType</code> is not configured
   * @return the value for the given command type or the given default value if not configured.
   */
  public String load(final CmdType cmdType, final String defaultValue) {
    return NbPreferences.forModule(Prefs.class).get(PREFS_PREFIX + cmdType.name(), defaultValue);
  }


  /**
   * Stores the given value for the given key.
   *
   * @param key the key to store
   * @param value the value store for the given key
   */
  public void store(final String key, final String value) {
    NbPreferences.forModule(Prefs.class).put(PREFS_PREFIX + key, value);
  }


  /**
   * Stores the given value for the given command type.
   *
   * @param cmdType the command type to store
   * @param value the value store for the given command type
   */
  public void store(final CmdType cmdType, final String value) {
    NbPreferences.forModule(Prefs.class).put(PREFS_PREFIX + cmdType.name(), value);
  }
}
