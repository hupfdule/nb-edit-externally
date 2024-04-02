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
 * An exception that indicates that a given string cannot be parsed as a command line.
 * <p>
 * This exception also encapsulates the problematic command line string in the field {@link #cmdLine}
 * that can be requested via {@link #getCmdLine()}.
 *
 * @author Marco Herrn
 */
public class ParseException extends RuntimeException {

  /** The command line that couldn't be parsed */
  private final CharSequence cmdLine;

  /**
   * Creates a new instance of <code>ParseException</code> without detail message.
   * @param cmdLine the commandline that couldn't be parsed
   */
  public ParseException(final CharSequence cmdLine) {
    this.cmdLine= cmdLine;
  }


  /**
   * Constructs an instance of <code>ParseException</code> with the specified detail message.
   *
   * @param msg the detail message.
   * @param cmdLine the commandline that couldn't be parsed
   */
  public ParseException(final String msg, final CharSequence cmdLine) {
    super(msg);
    this.cmdLine= cmdLine;
  }


  /**
   * Returns the command line that couldn't be parsed.
   * @return the command line that couldn't be parsed
   */
  public CharSequence getCmdLine() {
    return cmdLine;
  }
}
