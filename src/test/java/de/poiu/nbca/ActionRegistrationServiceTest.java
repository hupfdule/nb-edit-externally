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

import static org.junit.Assert.assertEquals;



/**
 *
 * @author mherrn
 */
@RunWith(Parameterized.class)
public class ActionRegistrationServiceTest {

  @Parameters(name = "{index}: {1}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {null, null},
      {new String[]{}, ""},
      {new String[]{""}, ""},
      {new String[]{"simple"}, "simple"},
      {new String[]{"one", "two"}, "one/two"},
      {new String[]{"one", "two", "three"}, "one/two/three"},
    });
  }


  private final String[] pathParts;
  private final String expectedOutcome;


  public ActionRegistrationServiceTest(final String[] pathParts, final String expectedOutcome) {
    this.pathParts= pathParts;
    this.expectedOutcome= expectedOutcome;
  }

  @org.junit.Test
  public void testJoinPaths() {
    assertEquals(this.expectedOutcome, ActionRegistrationService.joinPath(this.pathParts));
  }
}