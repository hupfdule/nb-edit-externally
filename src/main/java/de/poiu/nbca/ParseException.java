/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;


/**
 *
 * @author mherrn
 */
public class ParseException extends RuntimeException {

  private final CharSequence cmdLine;

  /**
   * Creates a new instance of <code>ParseException</code> without detail message.
   */
  public ParseException(final CharSequence cmdLine) {
    this.cmdLine= cmdLine;
  }


  /**
   * Constructs an instance of <code>ParseException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public ParseException(final String msg, final CharSequence cmdLine) {
    super(msg);
    this.cmdLine= cmdLine;
  }


  public CharSequence getCmdLine() {
    return cmdLine;
  }


}
