package com.ahmedsameha1.peninbin.utils;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by me on 15/10/16.
 */

public class Money {


    public static void startPurchase() {

    }

    public static void continuePurchase() {

    }

    private String [] ffjde ( String s ) {
        ArrayList<Character> randomed_characters = new ArrayList<>(s.length());
        ArrayList<Integer> taken_random_positions = new ArrayList<>(s.length());
        SecureRandom secureRandom = new SecureRandom();
        for ( int f = 0; f < s.length(); f++ ) {
            int rand = secureRandom.nextInt(s.length());
            int i;
            while ( true ) {
                for ( i = 0; i < taken_random_positions.size(); i++) {
                    if ( taken_random_positions.get(i) == rand ) {
                        //System.out.println("This was a for try");
                        break;
                    }
                }
                if (i == taken_random_positions.size() ) {

                    break;
                }

                rand = secureRandom.nextInt(s.length());
            }
            taken_random_positions.add(f,rand);
            randomed_characters.add(rand,s.charAt(f));
        }
        String randomized_key = "";
        for ( Character c : randomed_characters ) {
            randomized_key += c;
        }
        String number_key = "";
        for ( int i : taken_random_positions ) {
           number_key += (char) i;
        }
        return new String [] { randomized_key, number_key};
    }

}
