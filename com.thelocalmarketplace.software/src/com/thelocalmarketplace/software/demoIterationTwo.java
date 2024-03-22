/**
 * Yotam Rojnov (UCID: 30173949)
 * Duncan McKay (UCID: 30177857)
 * Mahfuz Alam (UCID:30142265)
 * Luis Trigueros Granillo (UCID: 30167989)
 * Lilia Skumatova (UCID: 30187339)
 * Abdelrahman Abbas (UCID: 30110374)
 * Talaal Irtija (UCID: 30169780)
 * Alejandro Cardona (UCID: 30178941)
 * Alexandre Duteau (UCID: 30192082)
 * Grace Johnson (UCID: 30149693)
 * Abil Momin (UCID: 30154771)
 * Tara Ghasemi M. Rad (UCID: 30171212)
 * Izabella Mawani (UCID: 30179738)
 * Binish Khalid (UCID: 30061367)
 * Fatima Khalid (UCID: 30140757)
 * Lucas Kasdorf (UCID: 30173922)
 * Emily Garcia-Volk (UCID: 30140791)
 * Yuinikoru Futamata (UCID: 30173228)
 * Joseph Tandyo (UCID: 30182561)
 * Syed Haider (UCID: 30143096)
 * Nami Marwah (UCID: 30178528)
 */
package com.thelocalmarketplace.software;

import java.util.Scanner;

public class demoIterationTwo {
    
    public static void main(String[] args) {
        // Example usage
        demoIterationTwo();
    }
    
    public static void demoIterationTwo() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Checkout completed. How would you like to pay?");
        System.out.println("1. Card (debit/credit)");
        System.out.println("2. Coin");
        System.out.println("3. Banknote");
        System.out.print("Enter your choice (1/2/3): ");
        
        int choice = scanner.nextInt();
        
        switch (choice) {
            case 1:
                System.out.println("You have selected to pay with card.");
                System.out.print("Enter card type (1 for debit/ 2 for credit / 3 back to previous option): ");
                String cardType = scanner.next().toLowerCase();
                if (cardType.equals("debit")) {
                    System.out.println("You have selected to pay with a debit card.");
                    // Add your debit card payment logic here
                } else if (cardType.equals("credit")) {
                    System.out.println("You have selected to pay with a credit card.");
                    // Add your credit card payment logic here
                } else {
                	break;
                    // System.out.println("Invalid card type. Please enter either 'debit' or 'credit'.");
                }
                break;
            case 2:
                System.out.println("You have selected to pay with coin.");
                // Add your coin payment logic here
                break;
            case 3:
                System.out.println("You have selected to pay with banknote.");
                // Add your banknote payment logic here
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
        
        scanner.close();
    }
}