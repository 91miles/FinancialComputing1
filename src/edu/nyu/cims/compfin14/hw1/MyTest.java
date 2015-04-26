package edu.nyu.cims.compfin14.hw1;

import java.util.ArrayList;

public class MyTest {
  public static void main(String[] args){

    /* question 1 */
    ArrayList<Double> times=new ArrayList<Double>();
    ArrayList<Double> yields=new ArrayList<Double>();
    times.add(1.0);
    times.add(2.0);
    times.add(3.0);
    yields.add(0.02);
    yields.add(0.023);
    yields.add(0.03);
    /* generate yield curve object yc1 using above input arrays times & yields*/
    YieldCurve yc1=new YieldCurve(times,yields);
    System.out.println("1. ");
    //print yc1 to string
    System.out.println(yc1.toString());

    //question 2
    System.out.println("2. ");
    //generate bond list consist of two bonds b1 & b2
    ArrayList<Bond> bonds=new ArrayList<Bond>();
    Bond b1=new Bond(100,95,0.5);
    Bond b2=new Bond(1000,895,1);
    bonds.add(b1);
    bonds.add(b2);
    // generate yield curve object yc2 using above bond list
    YieldCurve yc2=new YieldCurve(bonds);
    //print yc2 to string
    System.out.print(yc2.toString());
    //print per-year interest rate for year 0.75
    System.out.print("The per-year interest rate for time 0.75 is: ");
    System.out.printf("%.4f%%%n%n", yc2.getInterestRate(0.75) * 100);

    //question3
    System.out.println("3. ");
    //create bond 3
    Bond b3=new Bond(500,0.05,3,Frequency.Semi_Annually);
    double price=Bond.getPrice(yc1, b3);
    
    System.out.println("yc1's interest rate at year of 0.5 is "+yc1.getInterestRate(0.5));
    System.out.println("yc1's interest rate at year of 1.5 is "+yc1.getInterestRate(1.5));
    //print price for bond 3
    System.out.printf("The price is %.2f \n", price);
    double ytm=YieldCurve.getYTM(b3, price);
    //print yield to maturity for bond 3
    System.out.printf("The yield to maturity of this bond is %.2f%% \n", ytm * 100);


  }

}
