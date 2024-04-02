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
package de.poiu.nbee;

import de.poiu.nbee.parser.ParseException;
import de.poiu.nbee.parser.CmdlineParser;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class CmdlineParserTest {

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {"vim"                                                                                      , new String[]{},
         new String[]{"vim"}                                                                                                          , null                , null} ,
      {"vim someFile.txt"                                                                         , new String[]{},
         new String[]{"vim" , "someFile.txt"}                                                                                         , null                , null} ,
      {"vim /path/to/someFile.txt"                                                                , new String[]{},
         new String[]{"vim" , "/path/to/someFile.txt"}                                                                                , null                , null} ,
      {"vim someFile.txt \"+normal GW\""                                                          , new String[]{},
         new String[]{"vim" , "someFile.txt" , "+normal GW"}                                                                          , null                , null} ,
      {"vim someFile.txt '+normal GW'"                                                            , new String[]{},
         new String[]{"vim" , "someFile.txt" , "+normal GW"}                                                                          , null                , null} ,
      {"vim someFile.txt +normal\\ GW"                                                            , new String[]{},
         new String[]{"vim" , "someFile.txt" , "+normal GW"}                                                                          , null                , null} ,
      {"vim someFileWith\\\\Backslash.txt"                                                        , new String[]{},
         new String[]{"vim" , "someFileWith\\Backslash.txt"}                                                                          , null                , null} ,
      {"vim someFileWith\\\"doubleQuotes.txt"                                                     , new String[]{},
         new String[]{"vim" , "someFileWith\"doubleQuotes.txt"}                                                                       , null                , null} ,
      {"vim someFileWith\\'singleQuotes.txt"                                                      , new String[]{},
         new String[]{"vim" , "someFileWith'singleQuotes.txt"}                                                                        , null                , null} ,
      {"vim someFileWithUnnecessary\\Backslash.txt"                                               , new String[]{},
         new String[]{"vim" , "someFileWithUnnecessaryBackslash.txt"}                                                                 , null                , null} ,
      {"vim someFile.txt \"+normal i\\\"GW\""                                                     , new String[]{},
         new String[]{"vim" , "someFile.txt" , "+normal i\"GW"}                                                                       , null                , null} ,
      {"vim someFile.txt \"+normal '\\'\""                                                        , new String[]{},
         new String[]{"vim" , "someFile.txt" , "+normal ''"}                                                                          , null                , null} ,
      {"vim someFile.txt \"+normal '\\\\'\""                                                      , new String[]{},
         new String[]{"vim" , "someFile.txt" , "+normal '\\'"}                                                                        , null                , null} ,
      {"vim   someFile.txt    \"+normal     GW\""                                                 , new String[]{},
         new String[]{"vim" , "someFile.txt" , "+normal     GW"}                                                                      , null                , null} ,
      {"vim   someFile.txt    \"+normal     GW"                                                   , new String[]{},
         null                                                                                                                         , ParseException.class, "Unclosed quote: \""} ,
      {"vim   someFile.txt    +normal\\ GW\\"                                                     , new String[]{},
         null                                                                                                                         , ParseException.class, "Escape char at end of string"} ,
      {"vim ${file} \"+${line}G${column0}l\""                                                     , new String[]{"${file}", "/path/to/file", "${line}", "23", "${column0}", "29"},
         new String[]{"vim", "/path/to/file", "+23G29l"}                                                                               , null, null},
      {"\"C:\\\\Program Files\\\\Notepad++\\\\notepad++.exe\" -multiInst -n${line} -c${column} \"${file}\"", new String[]{"${file}", "c:\\projects\\project1\\file.txt", "${line}", "23", "${column}", "29"},
         new String[]{"C:\\Program Files\\Notepad++\\notepad++.exe", "-multiInst", "-n23", "-c29", "c:\\projects\\project1\\file.txt"}, null, null},
      {"\"C:/Program Files/Notepad++/notepad++.exe\" -multiInst -n${line} -c${column} \"${file}\"", new String[]{"${file}", "c:\\projects\\project1\\file.txt", "${line}", "23", "${column}", "29"},
         new String[]{"C:/Program Files/Notepad++/notepad++.exe", "-multiInst", "-n23", "-c29", "c:\\projects\\project1\\file.txt"}   , null, null},
      {"vim literal\\${file}"                                                                     , new String[]{"${file}", "/path/to/file", "${line}", "23", "${column0}", "29"},
         new String[]{"vim", "literal${file}"}                                                                                        , null, null},
      {"vim   ${file ${line}"                                                                     , new String[]{},
         null                                                                                                                         , ParseException.class, "Invalid character $ found in placeholder ${file"} ,
      {"vim   \\${file ${line}"                                                                   , new String[]{"${file}", "/path/to/file", "${line}", "35"},
         new String[]{"vim", "${file", "35"}                                                                                          , null                , null} ,
      {"vim   +$"                                                                                 , new String[]{},
         new String[]{"vim" , "+$"}                                                                                                   , null                , null} ,
    });
  }


  private final String cmdLine;
  private final String[] placeholders;
  private final String[] expectedOutcome;
  private final Class<? extends Exception> expectedException;
  private final String expectedExceptionMsg;

  @Rule
  public ExpectedException thrown = ExpectedException.none();


  public CmdlineParserTest(final String cmdLine,
                           final String[] placeholders,
                           final String[] expectedOutcome,
                           final Class<? extends Exception> expectedException,
                           final String expectedExceptionMsg) {
    if (placeholders != null && placeholders.length % 2 != 0) {
      throw new IllegalArgumentException("Placeholders must contain an even number of values (placeholder-replacement pairs)");
    }
    this.cmdLine= cmdLine;
    this.placeholders= placeholders;
    this.expectedOutcome= expectedOutcome;
    this.expectedException= expectedException;
    this.expectedExceptionMsg= expectedExceptionMsg;
  }


  @org.junit.Test
  public void testParseCmdline() {
    // setup expected exception
    if (this.expectedException != null) {
      thrown.expect(this.expectedException);
      thrown.expectMessage(this.expectedExceptionMsg);
    }

    // setup replacement maps
    final Map<String, String> replacementMap= new HashMap<>();
    if (this.placeholders != null) {
      for (int i=0; i < this.placeholders.length; i+=2) {
        replacementMap.put(this.placeholders[i], this.placeholders[i+1]);
      }
    }

    final CmdlineParser cmdlineParser= new CmdlineParser(replacementMap);

    assertArrayEquals(this.expectedOutcome, cmdlineParser.parse(this.cmdLine));
  }

}
