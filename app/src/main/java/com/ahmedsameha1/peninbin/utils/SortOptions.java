package com.ahmedsameha1.peninbin.utils;


import com.ahmedsameha1.peninbin.database.Contract;

public enum SortOptions {

    SORT_CREATION_DATE_DESC,
    SORT_ANSWER_DESC,
    SORT_QUESTION_ASC,
    SORT_ANSWER_ASC,
    SORT_DATE_DESC,
    SORT_DATE_ASC,
    SORT_QUESTION_DESC,
    SORT_CREATION_DATE_ASC,
    SORT_ENTRY_ORDER_ASC;

    public static final String[] SORT_STRINGS = {"datetime(" + Contract.CREATION_DATE + ") DESC",
            " a DESC",
            " q ASC",
            " a ASC",
            "date( q ) DESC",
            "date( q ) ASC",
            " q DESC",
            "datetime(" + Contract.CREATION_DATE + ") ASC",
            Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, " + "datetime(" + Contract.CREATION_DATE + ") ASC"};


    public String getSortString () {
        return SORT_STRINGS[this.ordinal()];
    }

    public int getSortOptionPosition() {
        return this.ordinal();
    }

    public static SortOptions getSortOption ( int position ) {
        switch ( position ) {
            case 0:
                return SORT_CREATION_DATE_DESC;
            case 1:
                return SORT_ANSWER_DESC;
            case 2:
                return SORT_QUESTION_ASC;
            case 3:
                return SORT_ANSWER_ASC;
            case 4:
                return SORT_DATE_DESC;
            case 5:
                return SORT_DATE_ASC;
            case 6:
                return SORT_QUESTION_DESC;
            case 7:
                return SORT_CREATION_DATE_ASC;
            case 8:
                return SORT_ENTRY_ORDER_ASC;
            default:
                return SORT_CREATION_DATE_DESC;
        }
    }
}