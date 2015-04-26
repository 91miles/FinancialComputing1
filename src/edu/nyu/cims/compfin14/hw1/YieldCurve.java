package edu.nyu.cims.compfin14.hw1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class simulates yield cureve 
 * @author kaiwenshen
 *
 */
public class YieldCurve {

  /**
   * Define a sorted map structure to represent the yield curve.
   * key: year
   * value: corresponding per-year interest rate
   */
  private TreeMap<Double,Double> yields = new TreeMap<Double, Double>();

  public static final double interval = 1.0;

  /**
   * Construct of the yield curve using list of bonds
   * @param bonds: a list of zero-coupon bonds
   */
  public YieldCurve(ArrayList<Bond> bonds){
    Iterator<Bond> Itr;
    //Generate iterator for the bonds
    Itr=bonds.iterator();
    while(Itr.hasNext()) {
      double ytm;
      double time;
      Bond bond=Itr.next();
      // Calculate the yield to maturity for each zero bond
      ytm=YieldCurve.getYTM(bond, bond.getPrice());
      // get the maturity for each bond
      time=bond.getMaturity();
      // put the maturity and related rate to the yield curve
      yields.put(time,ytm);
    }
  }


  /**
   * Construct of the yield curve with given yield curve
   * @param time: a list of time spots
   * @param r: a list of per year interest rate
   * 
   */
  public YieldCurve(ArrayList<Double> time, ArrayList<Double> r ){
    if(time.size()!=r.size()) {
      System.out.println("Construction of yield curve fails!");
      return;
    }
    // put the corresponding time and rate into the yield curve
    for(int i=0;i<time.size();i++){
      yields.put(time.get(i),r.get(i));
    }

  }

  /**
   * Calculate per-year interest rate for a given duration
   *       If time exists in our yield curve, the just return the corresponding rate
   *       Else linearly compute the rate(Assume the interest rate between given two time spots is linear)
   *            If the input duration is out of range, return the first or last rate which is the closest rate to it
   *            Otherwise, find the closest upper and lower time bound,linearly compute interest rate
   *
   * @param time: given duration
   * @return per-year interest rate
   */
  public double getInterestRate(double time){
    // if the time spot exists in our yield curve, the just return the corresponding rate
    if(yields.containsKey(time))
      return yields.get(time);
    else{
      //if the time is less than the last time spot in yield curve, return the minimum time spot related rate
      if(time<yields.firstKey()){
        return yields.get(yields.firstKey());
      }
      //if the time is bigger than the last time spot in yield curve, return the maximum time spot related rate
      else if(time>yields.lastKey()){
        return yields.get(yields.lastKey());
      }
      /* if the time is within the minimum and maximum interval, find the closet lower and upper time bound
       * using linear method to calculate the per-year interest rate
       */
      else{
        double rate;
        double start=0.0;
        double end=0.0;
        //generate the iterator for the time spot in the yield curve
        Iterator<Double> time_itr =yields.keySet().iterator();
        while(time_itr.hasNext()) {
          double get_time=time_itr.next();
          //update the lower bound
          if(get_time<time)
            start=get_time;
          //update the upper bound
          else if(get_time>time){
            end=get_time;
            break;
          }
        }
        //Calculate the interest rate by linearly combination of lower and upper bound
        rate=(yields.get(end)-yields.get(start))/(end-start)*(time-start)+yields.get(start);
        return rate;
      }
    }
  }

  /**
   * Calculate the discount factor for a given duration, using format e^{-rt}
   * @param t: given duration
   * @return discount factor
   */
  public double getDiscountFactor(double t){
    //get interest rate for given time spot
    double r = getInterestRate(t);
    //calculate the discount factor using giving formula
    return Math.exp(-r*t);
  }

  /**
   * Calculate the forward rate between two dates, using continuous compounding
   * @param t0: given start date
   * @param t1: given end date
   *
   * @return forward rate between these two date
   */
  public double getForwardRate(double t1, double t2){
    //get the interest rate for time spot t0
    double r1=getInterestRate(t1);
    //get the interest rate for time spot t1
    double r2=getInterestRate(t2);
    double r;
    //calculate the forward rate using given formula
    r = (r2*t2 - r1*t1)/(t2-t1);
    return r;
  }

  /**
   * Calculate the yield-to-maturity for a particular price using bisection method
   *        Start from ytm=0.5, Terminate when difference < $1
   *        if calculated price > given price, search for the right half
   *        if calculated price < given price, search for the left half
   * @param bond
   *               the bond objects
   * @param price
   *               given particular price
   * @return ytm (yield to maturity)
   */
  static public double getYTM (Bond bond, double price){
    double ytm;
    double lower;
    double upper;
    // ArrayList<Double> ytm_trail=new ArrayList<Double>();
    // For zero-coupon bond 
    if(bond.getType()==BondType.Zero_Coupon){
      ytm = Math.pow((bond.getFaceValue()/price), 1/bond.getMaturity()) - 1;
    }
    // For coupon-bearing bond, we use bisection to try different values
    // until the difference between fair value and price less than given threshold
    else
    {
      // customized threshold for stopping search
      double threshold = interval;
      // since ytm is between (0,1), we set the default first trail of ytm to be 0.5
      ytm=0.5;
      // the default starting point is 0.0
      lower=0.0;
      // the default ending point is 1.0
      upper=1.0;
      double diff;
      while(true)
      {
        // compute the difference between the given price and price computed using trail ytm
        diff=Bond.getPrice(bond,ytm)-price;
        // if the difference less than given threshold, then stop
        if(Math.abs(diff)<threshold)
          break;
        // if the difference is less then 0, ytm is larger than the actual ytm,
        // search in the left half
        else if(diff<0)
        {
          upper=ytm;
          ytm=lower+(ytm-lower)/2;
        }
        // if the difference is greater then 0, ytm is smaller than the actual ytm, 
        // search in the right half
        else
        {
          lower=ytm;
          ytm=upper-(upper-ytm)/2;
        }
      }
    }
    return ytm;
  }
  /**
   * Implement toString: the contents of yield curve are placed into the a string
   *
   * @return string which contains the info of yield curve
   */
  public String toString() {
    String s="";
    // generate the iterator for the yield curve
    Iterator<Double> time_itr =yields.keySet().iterator();
    // put the time and related rate into string s
    while(time_itr.hasNext())
    {
      double year=time_itr.next();
      // If the time is integer, using integer format
      if(Math.round(year)-year==0) {
        s+=String.format("Year: %.0f",year);
      }
      // Else using float format
      else {
        s+=String.format("Year: %.2f",year);
      }
      s+=String.format(", Rate: %.2f%% \n",yields.get(year)*100);
    }
    return s;
  }
}
