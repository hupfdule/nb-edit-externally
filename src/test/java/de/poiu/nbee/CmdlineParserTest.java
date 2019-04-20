/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbee;

import de.poiu.nbee.parser.ParseException;
import de.poiu.nbee.parser.CmdlineParser;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
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
      {"vim"                                        , new String[]{"vim"}                                          , null                , null} ,
      {"vim someFile.txt"                           , new String[]{"vim" , "someFile.txt"}                         , null                , null} ,
      {"vim /path/to/someFile.txt"                  , new String[]{"vim" , "/path/to/someFile.txt"}                , null                , null} ,
      {"vim someFile.txt \"+normal GW\""            , new String[]{"vim" , "someFile.txt" , "+normal GW"}          , null                , null} ,
      {"vim someFile.txt '+normal GW'"              , new String[]{"vim" , "someFile.txt" , "+normal GW"}          , null                , null} ,
      {"vim someFile.txt +normal\\ GW"              , new String[]{"vim" , "someFile.txt" , "+normal GW"}          , null                , null} ,
      {"vim someFileWith\\\\Backslash.txt"          , new String[]{"vim" , "someFileWith\\Backslash.txt"}          , null                , null} ,
      {"vim someFileWith\\\"doubleQuotes.txt"       , new String[]{"vim" , "someFileWith\"doubleQuotes.txt"}       , null                , null} ,
      {"vim someFileWith\\'singleQuotes.txt"        , new String[]{"vim" , "someFileWith'singleQuotes.txt"}        , null                , null} ,
      {"vim someFileWithUnnecessary\\Backslash.txt" , new String[]{"vim" , "someFileWithUnnecessaryBackslash.txt"} , null                , null} ,
      {"vim someFile.txt \"+normal i\\\"GW\""       , new String[]{"vim" , "someFile.txt" , "+normal i\"GW"}       , null                , null} ,
      {"vim someFile.txt \"+normal '\\'\""          , new String[]{"vim" , "someFile.txt" , "+normal ''"}          , null                , null} ,
      {"vim someFile.txt \"+normal '\\\\'\""        , new String[]{"vim" , "someFile.txt" , "+normal '\\'"}        , null                , null} ,
      {"vim   someFile.txt    \"+normal     GW\""   , new String[]{"vim" , "someFile.txt" , "+normal     GW"}      , null                , null} ,
      {"vim   someFile.txt    \"+normal     GW"     , null                                                         , ParseException.class, "Unclosed quote: \""} ,
      {"vim   someFile.txt    +normal\\ GW\\"       , null                                                         , ParseException.class, "Escape char at end of string"} ,
    });
  }


  private final String cmdLine;
  private final String[] expectedOutcome;
  private final Class<? extends Exception> expectedException;
  private final String expectedExceptionMsg;

  @Rule
  public ExpectedException thrown = ExpectedException.none();


  public CmdlineParserTest(final String cmdLine,
                           final String[] expectedOutcome,
                           final Class<? extends Exception> expectedException,
                           final String expectedExceptionMsg) {
    this.cmdLine= cmdLine;
    this.expectedOutcome= expectedOutcome;
    this.expectedException= expectedException;
    this.expectedExceptionMsg= expectedExceptionMsg;
  }


  @org.junit.Test
  public void testParseCmdline() {
    //setup expected exception
    if (this.expectedException != null) {
      thrown.expect(this.expectedException);
      thrown.expectMessage(this.expectedExceptionMsg);
    }

    assertArrayEquals(this.expectedOutcome, CmdlineParser.parse(this.cmdLine));
  }

}
