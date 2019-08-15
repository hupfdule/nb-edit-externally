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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


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
 * <p>
 * Certain placeholders can be replaced in the given command line string. The placeholders must be
 * enclosed in <code>${</code> and <code>}</code> characters. If the given command line string
 * contains placeholders for which no replacement string was defined, it will be included literally
 * in the parsed command line.
 *
 * @author Marco Herrn
 */
public class CmdlineParser {

  private static final Logger LOGGER= Logger.getLogger(CmdlineParser.class.toString());

  private static enum Mode {
    /** The normal parse mode. */
    PARSE,
    /** A placeholder was found. While in this mode the placeholder will be read until its end is found. */
    REPLACE,
    ;
  }


  /** The mappings to use to replace placeholders. */
  private final Map<String, String> replacements= new HashMap<String, String>();

  // start in parse mode
  private Mode mode= Mode.PARSE;



  /**
   * Constructs a new CmdlineParser without any replacement mappings.
   */
  public CmdlineParser() {
  }


  /**
   * Constructs a new CmdlineParser with the given replacement mappings.
   * <p>
   * The given map must contain the placeholders as values and the replacement strings as values.
   * <p>
   * The placeholder <i>must</i> contain the surrounding <code>${}</code>. Therefore set it as
   * <code>replacementMap.put("file", "/path/to/file");<code>
   * but instead
   * <code>replacementMap.put("${file}", "/path/to/file");</code>   *
   *
   * @param replacements the replements to use when parsing to command line
   */
  public CmdlineParser(final Map<String, String> replacements) {
    if (replacements != null) {
      this.replacements.putAll(replacements);
    }
  }


  /**
   * Specifies a replacement string for a given placeholder.
   * <p>
   * The placeholder <i>must</i> contain the surrounding <code>${}</code>. Therefore don't call
   * <code>cmdlineParser.replace("file", "/path/to/file");<code>
   * but instead
   * <code>cmdlineParser.replace("${file}", "/path/to/file");</code>
   * <p>
   * For convenience this method returns this CmdlineParser instance to be able to chain the
   * method calls:
   * <code>
   * cmdlineParser
   *   .replace("${file}", "/path/to/file")
   *   .replace("${line}", "25")
   * ;
   * </code>
   * <p>
   * If replacement mappings were already given via {@link #CmdlineParser(java.util.Map) constructor}
   * this replacement will be added to the already existing mappings or overwrite the existing one
   * for the same placeholder.
   *
   * @param placeholder the placeholder to replace
   * @param replacement the replacement string
   * @return this CmdlineParser
   */
  public CmdlineParser replace(final String placeholder, final String replacement) {
    Objects.requireNonNull(placeholder);
    Objects.requireNonNull(replacement);
    this.replacements.put(placeholder, replacement);
    return this;
  }



  /**
   * Parse a command line string into a String array suitable to be feeded to
   * {@link Runtime#exec(java.lang.String[])} for execution.
   *
   * @param cmdLine the command line string to pares
   * @return the parsed command line string
   * @throws ParseException if the given string cannot be parsed in as a valid command line
   */
  public String[] parse(final CharSequence cmdLine) {
    final List<String> parsedCmdLine= new ArrayList<String>();

    final StringBuilder sb= new StringBuilder();
    Character quoteChar= null;
    final StringBuilder sbPlaceholder= new StringBuilder();

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
        case '$':
          if (this.mode == Mode.REPLACE) {
            throw new ParseException("Invalid character " + c + " found in placeholder " + sbPlaceholder.toString(), cmdLine);
          } else if (cmdLine.length() < i + 2) {
            // a single $ at the end of the command line is used literally
            sb.append(c);
          } else if (cmdLine.charAt(i + 1) == '{') {
            // switch to REPLACE mode
            this.mode= Mode.REPLACE;
            sbPlaceholder
              .append(c)
              .append(cmdLine.charAt(++i));
          }
          break;
        case '}':
          if (this.mode == Mode.REPLACE) {
            // end REPLACE mode
            sbPlaceholder.append(c);
            this.mode= Mode.PARSE;
            sb.append(replace(sbPlaceholder.toString()));
            sbPlaceholder.delete(0, sbPlaceholder.length());
          } else {
            sb.append(c);
          }
          break;
        default:
          if (this.mode == Mode.REPLACE) {
            sbPlaceholder.append(c);
          } else {
            sb.append(c);
          }
      }
    }

    // if a placeholder wasn't closed, it means we could not parse the commandline correctly
    if (sbPlaceholder.length() != 0) {
      throw new ParseException("Unclosed placeholder: "+sb.toString(), cmdLine);
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


  /**
   * Returns the replacement string for the given placeholder.
   * <p>
   * If no replacement is defined in the {@link #replacements replacements map} of this
   * CmdlineParser, the placeholder will be returned as given (to be included literally in the
   * parsed command line.
   *
   * @param placeholder the placeholder for which to return the replacement string
   * @return the replacement string or the placeholder itself if not replacement mapping is defined
   */
  private CharSequence replace(final String placeholder) {
    if (this.replacements.containsKey(placeholder)) {
      return this.replacements.get(placeholder);
    } else {
      LOGGER.log(Level.INFO, "No replacement mapping found for placeholder {0}. Including it literally in the command.", placeholder);
      return placeholder;
    }
  }
}
