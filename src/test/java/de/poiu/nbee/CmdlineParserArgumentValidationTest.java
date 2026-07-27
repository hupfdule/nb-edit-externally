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

import de.poiu.nbee.parser.CmdlineParser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;


/**
 * Tests for {@link CmdlineParser} constructor/method argument handling that doesn't fit the
 * parameterized cmdLine-in/tokens-out structure of {@link CmdlineParserTest}.
 *
 * @author Marco Herrn
 */
public class CmdlineParserArgumentValidationTest {

  @Test
  public void constructorAcceptsNullReplacementsMap() {
    final CmdlineParser p= new CmdlineParser(null);
    assertArrayEquals(new String[]{"vim"}, p.parse("vim"));
  }


  @Test(expected = NullPointerException.class)
  public void replaceRejectsNullPlaceholder() {
    final CmdlineParser p= new CmdlineParser();
    p.replace(null, "value");
  }


  @Test(expected = NullPointerException.class)
  public void replaceRejectsNullReplacement() {
    final CmdlineParser p= new CmdlineParser();
    p.replace("${x}", null);
  }

}
