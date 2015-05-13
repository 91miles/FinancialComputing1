package edu.nyu.cims.compfin14.hw1;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Implement the Bond object (coupon-bearing bond or zero-coupon bond)
 *
 * @author Kaiwen Shen
 */

enum BondType{
  Zero_Coupon,
  Coupon_Bearing;
}

public class Bond {

  private BondType type; 
  private double faceValue; 

  /**
   * maturity is measured in years
   */
  private double maturity; 
  private Frequency paymentFrequency; 
  private double price; 

  /**
   * coupon rate per year
   */
  private double couponRate;
  private double coupon;

  /**
   * using map structure to indicate the cash flow over time
   * key: time
   * value: cash flow
   */
  Map<Double,Double> cashFlow = new TreeMap<Double, Double>();

  /**
   * Construct of the coupon bearing bond
   * @param FaceValue: the face value of the bond
   * @param CouponRate: annually paid coupon rate of the bond
   * @param maturity: maturity of the bond
   * @param paymentfrequency: indicate the coupon payment frequency of the bond
   */
  public Bond(double FaceValue,double CouponRate, 
      double maturity, Frequency paymentfrequency){
    this.couponRate=CouponRate;
    this.faceValue=FaceValue;
    this.maturity=maturity;
    this.type=BondType.Coupon_Bearing;
    this.paymentFrequency=paymentfrequency; 
    this.coupon=FaceValue*CouponRate*this.paymentFrequency.getLength();
    this.cashFlow=this.getCashFlow();
  }

  /**
   * Construct of the zero-coupon bearing bond,
   * @param facevalue: the face value of the bond
   * @param price: price of the bond
   * @param maturity: maturity of the bond
   */
  public Bond(double facevalue,double price,double maturity){
    this.coupon=0;
    this.faceValue=facevalue;
    this.price=price;
    this.maturity=maturity;
    this.type=BondType.Zero_Coupon;
    this.cashFlow=this.getCashFlow();
  }

  /**
   * Calculate the bond's cash flow
   * For zero_coupon bonds, just return the maturity and its face value
   * For coupon_bearing bond, return the coupon time and corresponding cash
   *
   * @return map
   *  get the map with the key being time and the value being distributed cash flow at that time
   */
  public Map<Double, Double> getCashFlow(){
    if (this.type==BondType.Zero_Coupon) {
      this.cashFlow.put(this.maturity, this.faceValue);
      return this.cashFlow;
    }

    int couponNum;
    double increment;
    // get the total counts of payment on coupon
    couponNum=(int) (this.getMaturity() / this.paymentFrequency.getLength());
    increment=this.paymentFrequency.getLength();

    double startingTime=0+increment;
    /* for each time spot, generate the cash for that time
                             If it is the zero-coupon bond, the cash flow for before maturity is zero
                             for coupon-bearing bond, the cash flow is the coupon
     */
    for(int i=1;i<couponNum;i++){
      this.cashFlow.put(startingTime, this.getCoupon());
      startingTime+=increment;
    }
    // Cash for the last time spot is face value plus coupon
    this.cashFlow.put(startingTime,this.getCoupon()+this.getFaceValue());
    return cashFlow;
  }

  /**
   * Overload function
   * Calculate the bond's price given yield curve object, discounting cash flow using corresponding interest rate
   * @param ycm: given yield curve object
   * @param bond: the bond object
   * @return price
   */
  static public double getPrice(YieldCurve ycm, Bond bond){
    double price=0.0;

    // for each time spot in cash flow, calculate the corresponding discount present value
    for (Double time : bond.cashFlow.keySet()) {
      double value = bond.cashFlow.get(time);
      //compute the present value of each time spot using discount factor at that spot time
      price+=value*ycm.getDiscountFactor(time);
    }
    return price;
  }

  /**
   * Calculate bond's fair price for a given yield to maturity by discounting cash flow using ytm
   * For zero-coupon bond, use compound interest. 
   * For coupon-bearing bond, use continuous compounding. 
   * @param bond: the bond objects
   * @param ytm: given yield to maturity
   * @return price
   */
  static public double getPrice(Bond bond, double ytm){
    double price=0.0;

    // If it is a zero coupon bond, we just need the discount the face value at ytm
    if(bond.type==BondType.Zero_Coupon){
      price=bond.faceValue*Math.exp(-ytm*bond.maturity);
    }
    // If it is a coupon bearing bond, we need the discount all the cash flow with ytm
    else {
      // loop for each element in cash flow, calculate the present value discount with ytm
      for (Double time : bond.cashFlow.keySet()) {
        double value = bond.cashFlow.get(time);
        price+=value*Math.exp(-ytm*time);
      }
    }
    return price;
  }

  

  public BondType getType() {
    return type;
  }

  public double getFaceValue() {
    return faceValue;
  }

  public double getMaturity() {
    return maturity;
  }

  public Frequency getPaymentFrequency() {
    return paymentFrequency;
  }

  public double getPrice() {
    return price;
  }

  public double getCouponRate() {
    return couponRate;
  }

  public double getCoupon() {
    return coupon;
  }


}
