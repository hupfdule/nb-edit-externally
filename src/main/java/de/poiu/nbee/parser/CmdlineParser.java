/*
 * Copyright 2019 Marco Herrn.
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

import java.util.List;
import java.util.ArrayList;


/**
 * A simple command line parser that allows correct tokenizing of command line paramters.
 * Usually the command line string is split on whitespace characters. Multiple consecutive
 * whitespace characters are handled like a single one.
 * To combine multiple whitespace-separated strings into a single token, quotes can be used to
 * group the strings together.
 * Also a backslash can be used to escape a single character.
 * <p>
 * Backslashes have preference before quotes. That means that a backslash inside a quoted string
 * can be used to escape a verbatim character inside the quoted string.
 * <p>
 * Recognized whitespace characters:
 * <ul>
 *   <li>normal space (ord 32)</li>
 *   <li>tab (ord 9)</li>
 *   <li>line feed (ord 10)</li>
 *   <li>carriage return (ord 13)</li>
 * </ul> *
 * Recognized quote characters:
 * <ul>
 *   <li>single quotes (ord 34)</li>
 *   <li>double quotes (ord 39)</li>
 * </ul>
 *
 * @author Marco Herrn
 */
public class CmdlineParser {

  /**
   * Parse a command line string into a String array suitable to be feeded to
   * {@link Runtime#exec(java.lang.String[])} for execution.
   *
   * @param cmdLine the command line string to pares
   * @return the parsed command line string
   * @throws ParseException if the given string cannot be parsed in as a valid command line
   */
  public static String[] parse(final CharSequence cmdLine) {
    final List<String> parsedCmdLine= new ArrayList<String>();

    final StringBuilder sb= new StringBuilder();
    Character quoteChar= null;

    for (int i= 0; i< cmdLine.length(); i++) {
      final char c= cmdLine.charAt(i);

      switch (c) {
        case '\\':
          if (cmdLine.length() < i + 2) {
            throw new ParseException("Escape char at end of string", cmdLine);
          }
          sb.append(cmdLine.charAt(++i));
          break;
        case '"':
        case '\'':
          if (quoteChar != null) {
            if (quoteChar.charValue() == c) {
              quoteChar= null;
            } else {
              sb.append(c);
            }
          } else {
            quoteChar= c;
          }
          break;
        case ' ':
        case '\t':
        case '\n':
        case '\r':
          if (quoteChar != null) {
            sb.append(c);
          } else {
            if (sb.length() > 0) {
              parsedCmdLine.add(sb.toString());
              sb.delete(0, sb.length());
            } else {
              // ignore multiple consecutive whitespaces
            }
          }
          break;
        default:
          sb.append(c);
      }
    }

    // an unclosed quote means we could not parse the commandline correctly
    if (quoteChar != null) {
      throw new ParseException("Unclosed quote: "+quoteChar.charValue(), cmdLine);
    }

    if (sb.length() > 0) {
      parsedCmdLine.add(sb.toString());
    }

    return parsedCmdLine.toArray(new String[parsedCmdLine.size()]);
  }
}
