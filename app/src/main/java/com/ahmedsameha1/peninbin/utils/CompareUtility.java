package com.ahmedsameha1.peninbin.utils;

/**
 * Created by ahmed on 02/05/16.
 */
public class CompareUtility {


    public static boolean nonStrictCompareIgnoreCase(String text1, String text2 ) {
        String toBeMatchedregex = createAnswerRegexForNonStrictCompareIgnoreCase(text1);
        return text2.matches(toBeMatchedregex);
    }

    public static boolean nonStrictCompare( String text1, String text2 ) {
        String toBeMatchedregex = createAnswerRegexForNonStrictCompare(text1);
        return text2.matches(toBeMatchedregex);
    }

    public static int checkTextLineToImport( String text_line ) {
        String pattern1 = "^(?!(.*(@@@).*){2,}|(.*(~~~).*){2,}|(.*(%%%).*){2,}|(.*(###).*)+).{1,500}@@@.{1,500}~~~.{0,499}[^%]%%%[0-3]$";
        String pattern2 = "^(?!(.*(@@@).*){2,}|(.*(~~~).*){2,}|(.*(%%%).*)+|(.*(###).*){2,}).{1,500}@@@.{1,500}~~~.{0,499}[^#]###\\d{1,9}$";
        String pattern3 = "^(?!(.*(@@@).*){2,}|(.*(~~~).*){2,}|(.*(%%%).*)+|(.*(###).*)+).{1,500}@@@.{1,500}~~~.{1,500}$";
        if ( text_line.matches(pattern1) ) {
            return 1;
        } else if ( text_line.matches(pattern2) ) {
            return 2;
        } else if ( text_line.matches(pattern3) ){
            return 3;
        } else {
            return 0;
        }
    }

    public static String createAnswerRegexForNonStrictCompare ( String text1 ) {
        String toBeMatchedregex = "\\s*";
        for ( int i = 0; i < text1.length(); i++ ) {
            if ( Character.toString(text1.charAt(i)).matches("[\\s\\p{Punct}]") ) {
                if ( ! Character.toString(text1.charAt(i)).matches("[\\Q.^$*+?()[{\\|\\E]")) {
                    if ( ! Character.toString(text1.charAt(i)).matches("\\s") ) {
                        toBeMatchedregex += "\\s*";
                        toBeMatchedregex += text1.charAt(i);
                        toBeMatchedregex += "\\s*";
                    } else {
                        if ( text1.substring(0,i).matches("\\s*") ) {
                            toBeMatchedregex += "\\s*";
                        } else if ( text1.substring(i + 1,text1.length() ).matches("\\s*") ) {
                            toBeMatchedregex += "\\s*";
                        } else if ( Character.toString(text1.charAt(i+1)).matches("[\\s\\p{Punct}]")) {
                            toBeMatchedregex += "\\s*";
                        } else if ( Character.toString(text1.charAt(i-1)).matches("[\\s\\p{Punct}]")) {
                            toBeMatchedregex += "\\s*";
                        } else {
                            toBeMatchedregex += "\\s*\\s\\s*";
                        }
                    }
                } else {
                    toBeMatchedregex += "\\s*\\";
                    toBeMatchedregex += text1.charAt(i);
                    toBeMatchedregex += "\\s*";
                }
            } else {
                toBeMatchedregex += "\\Q";
                toBeMatchedregex += text1.charAt(i);
                toBeMatchedregex += "\\E";
            }
            if ( i == text1.length() - 1 ) {
                toBeMatchedregex += "\\s*";
            }
        }
        return toBeMatchedregex;
    }

    public static String createAnswerRegexForNonStrictCompareIgnoreCase ( String text1 ) {
        String toBeMatchedregex = "(?i)\\s*";
        for ( int i = 0; i < text1.length(); i++ ) {
            if ( Character.toString(text1.charAt(i)).matches("[\\s\\p{Punct}]") ) {
                if ( ! Character.toString(text1.charAt(i)).matches("[\\Q.^$*+?()[{\\|\\E]")) {
                    if ( ! Character.toString(text1.charAt(i)).matches("\\s") ) {
                        toBeMatchedregex += "\\s*";
                        toBeMatchedregex += text1.charAt(i);
                        toBeMatchedregex += "\\s*";
                    } else {
                        if ( text1.substring(0,i).matches("\\s*") ) {
                            toBeMatchedregex += "\\s*";
                        } else if ( text1.substring(i + 1,text1.length() ).matches("\\s*") ) {
                            toBeMatchedregex += "\\s*";
                        } else if ( Character.toString(text1.charAt(i+1)).matches("[\\s\\p{Punct}]")) {
                            toBeMatchedregex += "\\s*";
                        } else if ( Character.toString(text1.charAt(i-1)).matches("[\\s\\p{Punct}]")) {
                            toBeMatchedregex += "\\s*";
                        } else {
                            toBeMatchedregex += "\\s*\\s\\s*";
                        }
                    }
                } else {
                    toBeMatchedregex += "\\s*\\";
                    toBeMatchedregex += text1.charAt(i);
                    toBeMatchedregex += "\\s*";
                }
            } else {
                toBeMatchedregex += "\\Q";
                toBeMatchedregex += text1.charAt(i);
                toBeMatchedregex += "\\E";
            }
            if ( i == text1.length() - 1 ) {
                toBeMatchedregex += "\\s*";
            }
        }
        return toBeMatchedregex;
    }



}
