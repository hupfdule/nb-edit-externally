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
public class Cmd {
  private String title= "";
  private String cmdLine= "";


  public Cmd() {
  }


  public Cmd(final String title, final String cmdLine) {
    this.title= title;
    this.cmdLine= cmdLine;
  }



  public String getTitle() {
    return title;
  }


  public void setTitle(String title) {
    if (title == null) {
      this.title= "";
    } else {
      this.title = title;
    }
  }


  public String getCmdLine() {
    return cmdLine;
  }


  public void setCmdLine(String cmdLine) {
    if (cmdLine == null) {
      this.cmdLine= "";
    } else {
      this.cmdLine = cmdLine;
    }
  }


  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + (this.title != null ? this.title.hashCode() : 0);
    hash = 23 * hash + (this.cmdLine != null ? this.cmdLine.hashCode() : 0);
    return hash;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Cmd other = (Cmd) obj;
    if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
      return false;
    }
    if ((this.cmdLine == null) ? (other.cmdLine != null) : !this.cmdLine.equals(other.cmdLine)) {
      return false;
    }
    return true;
  }


  @Override
  public String toString() {
    return "Cmd{" + "title=" + title + ", cmdLine=" + cmdLine + '}';
  }


  public boolean isEmpty() {
    return this.title.trim().isEmpty()
      && this.cmdLine.trim().isEmpty();
  }

}
