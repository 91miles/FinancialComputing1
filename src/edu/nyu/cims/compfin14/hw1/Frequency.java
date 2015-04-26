package edu.nyu.cims.compfin14.hw1;

public enum Frequency {
  Annually (1), 
  Semi_Annually (0.5);
  //Quarterly;

  private double period;
  Frequency(double len) {
    this.period = len;
  }
  public double getLength(){
    return this.period;
  }
}