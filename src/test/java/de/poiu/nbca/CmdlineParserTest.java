/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import java.util.Arrays;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;


/**
 *
 * @author mherrn
 */
@RunWith(Parameterized.class)
public class CmdlineParserTest {

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {"vim", new String[]{"vim"}},
      {"vim someFile.txt", new String[]{"vim", "someFile.txt"}},
      {"vim /path/to/someFile.txt", new String[]{"vim", "/path/to/someFile.txt"}},
      {"vim someFile.txt \"+normal GW\"", new String[]{"vim", "someFile.txt", "+normal GW"}},
      {"vim someFile.txt '+normal GW'", new String[]{"vim", "someFile.txt", "+normal GW"}},
      {"vim someFile.txt +normal\\ GW", new String[]{"vim", "someFile.txt", "+normal GW"}},
      {"vim someFileWith\\\\Backslash.txt", new String[]{"vim", "someFileWith\\Backslash.txt"}},
      {"vim someFileWith\\\"doubleQuotes.txt", new String[]{"vim", "someFileWith\"doubleQuotes.txt"}},
      {"vim someFileWith\\'singleQuotes.txt", new String[]{"vim", "someFileWith'singleQuotes.txt"}},
      {"vim someFileWithUnnecessary\\Backslash.txt", new String[]{"vim", "someFileWithUnnecessaryBackslash.txt"}},
      {"vim someFile.txt \"+normal i\\\"GW\"", new String[]{"vim", "someFile.txt", "+normal i\"GW"}},
      {"vim someFile.txt \"+normal '\\'\"", new String[]{"vim", "someFile.txt", "+normal ''"}},
      {"vim someFile.txt \"+normal '\\\\'\"", new String[]{"vim", "someFile.txt", "+normal '\\'"}},
      {"vim   someFile.txt    \"+normal     GW\"", new String[]{"vim", "someFile.txt", "+normal     GW"}},
    });
  }


  private final String cmdLine;
  private final String[] expectedOutcome;

  public CmdlineParserTest(final String cmdLine, final String[] expectedOutcome) {
    this.cmdLine= cmdLine;
    this.expectedOutcome= expectedOutcome;
  }

  @org.junit.Test
  public void testSomeMethod() {
    assertArrayEquals(this.expectedOutcome, CmdlineParser.parse(this.cmdLine));
  }

}
