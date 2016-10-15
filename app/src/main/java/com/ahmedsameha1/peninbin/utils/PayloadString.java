package com.ahmedsameha1.peninbin.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by me on 15/10/16.
 */

public class PayloadString {

    private static final char[] symbols = new char[36];

    static {
        for (int idx = 0; idx < 10; ++idx)
            symbols[idx] = (char) ('0' + idx);
        for (int idx = 10; idx < 36; ++idx)
            symbols[idx] = (char) ('a' + idx - 10);
    }



        /*
         * static { for (int idx = 0; idx < 10; ++idx) symbols[idx] = (char)
         * ('0' + idx); for (int idx = 10; idx < 36; ++idx) symbols[idx] =
         * (char) ('a' + idx - 10); }
         */

        private static final Random random = new SecureRandom();

        private static final char[] buf = new char[30];

  /*      public PayloadString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }*/

        public static String getString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }


}
