package com.ahmedsameha1.studyvocabularyfive.databasetemp;

import android.provider.BaseColumns;


/**
 * Created by ahmed on 7/11/2015.
 */
public final class Contract {
    public static final String COLUMN_NAME_CREATED_AT = "created_at";


    public static abstract class Field implements BaseColumns {
        public static final int DEFAULT_TYPE = 0;
        public static final int NOTEBOOK_TYPE = 1;
        // This constant represents the name of the table which contains the data of the names of the fields that the user will create.
        public static final String TABLE_NAME = "field";
        // This constant represents the name of the column which will contain the names of the fields that the user will create in the field table.
        public static final String COLUMN_NAME_FIELD_NAME = "field_name";
        public static final String COLUMN_NAME_FIELD_TYPE = "field_type";
    }
    public static abstract class Question implements BaseColumns {
        public static final String TABLE_NAME = "question";
        public static final String COLUMN_NAME_QUESTION_ENTRY = "question_entry";
    }
    public static abstract class Answer implements BaseColumns {
        public static final String TABLE_NAME = "answer";
        public static final String COLUMN_NAME_ANSWER_ENTRY = "answer_entry";
    }
    public static abstract class Linker implements BaseColumns {
        public static final String TABLE_NAME = "linker";
        public static final String COLUMN_NAME_QUESTION_LINK = "question_link";
        public static final String COLUMN_NAME_ANSWER_LINK = "answer_link";
        public static final String COLUMN_NAME_FIELD_LINK = "field_link";
        public static final String COLUMN_NAME_STUDY_DURING_ORDER = "study_during_order";
        public static final String COLUMN_NAME_TEST_ORDER = "test_order";
        public static final String COLUMN_NAME_TEST_WRONG_ANSWERS = "test_wrong_answers";
    }
    public static abstract class Study_During_Temp implements BaseColumns {
        public static final String TABLE_NAME = "study_during_temp_table";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_ANSWER = "answer";
        public static final String COLUMN_NAME_FIELD = "field";
    }
    public static abstract class Check implements BaseColumns {
        public static final int CHECK_NUMBER = 4179198;
        public static final String TABLE_NAME = "check_table";
        public static final String COLUMN_NAME_CHECK_NUMBER = "check_number";
    }
    public static abstract class OldField implements BaseColumns {
        // This constant represents the name of the table which contains the data of the names of the fields that the user will create.
        public static final String TABLE_NAME = "field";
        // This constant represents the name of the column which will contain the names of the fields that the user will create in the field table.
        public static final String COLUMN_NAME_FIELD_NAME = "field_name";
        // This constant represents the string suffix which will (with the field name) represent the name of the table of the question
        // The question table name will be always fieldName + TABLE_NAME_QUESTION_SUFFIX
        public static final String TABLE_NAME_QUESTION_SUFFIX = "_question";
        // This constant represents the string suffix which will (with the field name) represent the name of the column of the question table
        // In the question table, the question column name will be always fieldName + COLUMN_NAME_QUESTION_SUFFIX
        public static final String COLUMN_NAME_QUESTION_SUFFIX = "_question_entry";
        // This constant represents the string suffix which will (with the field name) represent the name of the table of the answer
        // The answer table name will be always fieldName + TABLE_NAME_ANSWER_SUFFIX
        public static final String TABLE_NAME_ANSWER_SUFFIX = "_answer";
        // This constant represents the string suffix which will (with the field name) represent the name of the column of the answer table
        // In the answer table, the answer column name will be always fieldName + COLUMN_NAME_ANSWER_SUFFIX
        public static final String COLUMN_NAME_ANSWER_SUFFIX = "_answer_entry";
        /*This constant represents the string suffix which will (with the field name) represent the name of the table which will make the link between
         any question and andy answer
        The link table name will be always fieldName + TABLE_NAME_LINK_SUFFIX*/
        public static final String TABLE_NAME_LINK_SUFFIX = "_question_answer";
        /*This constant represents the string suffix which will (with the field name) represent the name of the column of the link table which will contain
        the question to link between the question table and the answer table.
        In the link table, the question column name will be always fieldName + COLUMN_NAME_QUESTION_LINK_SUFFIX*/
        public static final String COLUMN_NAME_QUESTION_LINK_SUFFIX = "_question_link_entry";
        /*This constant represents the string suffix which will (with the field name) represent the name of the column of the link table which will contain
         the answer to link between the question table and the answer table.
         In the link table, the anwwer column name will be always fieldName + COLUMN_NAME_ANSWER_LINK_SUFFIX*/
        public static final String COLUMN_NAME_ANSWER_LINK_SUFFIX = "_answer_link_entry";
        // this column will hold the timestamp of the addition of the row
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }
}
