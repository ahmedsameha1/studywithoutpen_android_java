package com.ahmedsameha1.studyvocabulary.database;

import android.provider.BaseColumns;


/**
 * Created by ahmed on 7/11/2015.
 */
public final class Contract {
    public static final String COLUMN_ID = "_id";
    public static final String CREATION_DATE = "creation_date";

    public static abstract class Field implements BaseColumns {
        public static final int DEFAULT_TYPE = 0;
        public static final int NOTEBOOK_TYPE = 1;
        public static final int STRING_DATA_TYPE = 0;
        public static final int NOT_STRING_DATA_TYPE = 1;
        public static final int DO_NOT_READ_ANSWER = 0;
        public static final int READ_ANSWER = 1;
        public static final int DO_NOT_IGNORE_CASE = 0;
        public static final int IGNORE_CASE = 1;
        public static final String TABLE_NAME = "field";
        public static final String COLUMN_FIELD_NAME = "name";
        public static final String COLUMN_FIELD_TYPE = "type";
        public static final String COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE = "string_or_blob";
        public static final String COLUMN_LOCALE = "locale";
        public static final String COLUMN_SORT_BY = "sort_by";
        public static final String COLUMN_LIST_TEXT_SIZE = "list_text_size";
        public static final String COLUMN_QUESTION_AREA_SIZE = "question_area_size";
        public static final String COLUMN_ANSWER_AREA_SIZE = "answer_area_size";
        public static final String COLUMN_TEST_TEXT_SIZE = "test_text_size";
        public static final String COLUMN_ACTION_BAR_SHOWN = "action_bar_shown";
        public static final String COLUMN_READ_ANSWER = "read_answer";
        public static final String COLUMN_USAGE_COUNT = "usage_count";
        public static final String COLUMN_CREATION_EMULATION_NUMBER = "creation_emulation_number";
        public static final String COLUMN_CHECK_TYPE = "check_type";
    }
    public static abstract class STring implements BaseColumns {
        public static final String TABLE_NAME = "string";
        public static final String COLUMN_VALUE = "value";
    }
    public static abstract class Not_String implements BaseColumns {
        public static final String TABLE_NAME = "not_string";
        public static final String COLUMN_VALUE = "value";
    }
    public static abstract class String_Entry implements BaseColumns {
        public static final String TABLE_NAME = "string_entry";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_FIELD = "field";
        public static final String COLUMN_PAUSED_TEST_ORDER = "paused_test_order";
        public static final String COLUMN_PAUSED_STUDY_PERIOD_ORDER = "paused_study_period_order";
        public static final String COLUMN_ASKED_COUNT = "asked_count";
        public static final String COLUMN_WRONGLY_ANSWERED_COUNT = "wrongly_answered_count";
        public static final String COLUMN_RANK = "rank";
        public static final String COLUMN_CREATION_EMULATED_DATE = "creation_emulated_date";
    }
    public static abstract class Not_String_Entry implements BaseColumns {
        public static final String TABLE_NAME = "not_string_entry";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_FIELD = "field";
        public static final String COLUMN_PAUSED_TEST_ORDER = "paused_test_order";
        public static final String COLUMN_PAUSED_STUDY_PERIOD_ORDER = "paused_study_period_order";
        public static final String COLUMN_ASKED_COUNT = "asked_count";
        public static final String COLUMN_WRONGLY_ANSWERED_COUNT = "wrongly_answered_count";
        public static final String COLUMN_RANK = "rank";
        public static final String COLUMN_CREATION_EMULATED_DATE = "creation_emulated_date";
    }
    public static abstract class Test_Wrong_Answer_String implements BaseColumns {
        public static final String TABLE_NAME = "test_wrong_answer_string";
        public static final String COLUMN_ENTRY = "entry";
        public static final String COLUMN_WRONG_ANSWER = "wrong_answer";
    }
    public static abstract class Test_Wrong_Answer_Not_String implements BaseColumns {
        public static final String TABLE_NAME = "test_wrong_answer_not_string";
        public static final String COLUMN_ENTRY = "entry";
        public static final String COLUMN_WRONG_ANSWER = "wrong_answer";
    }
    public static abstract class Test_Paused implements BaseColumns {
        public static final String TABLE_NAME = "test_paused";
        public static final String COLUMN_FIELD = "field";
        public static final String COLUMN_CURRENT_QUESTION_COUNTER = "current_question_counter";
        public static final String COLUMN_WRONG_ANSWERS_COUNT = "wrong_answers_count";
        public static final String COLUMN_TRIES_NUMBER = "tries_number";
    }
    public static abstract class Study_Period_Repeated_Entry implements BaseColumns {
        public static final String TABLE_NAME = "study_period_repeated_entry";
        public static final String COLUMN_FIELD = "field";
        public static final String COLUMN_REPEATED_ENTRY = "repeated_entry";
    }
    public static abstract class Study_Period_Paused implements BaseColumns {
        public static final String TABLE_NAME = "study_period_paused";
        public static final String COLUMN_FIELD = "field";
        public static final String COLUMN_CURRENT_QUESTION_COUNTER = "current_question_counter";
        public static final String COLUMN_TRIES_NUMBER = "tries_number";
    }
    public static abstract class Alternative_Answer_String implements BaseColumns {
        public static final String TABLE_NAME = "alternative_answer_string";
        public static final String COLUMN_ENTRY = "entry";
        public static final String COLUMN_ALTERNATIVE_ANSWER = "alternative_answer";
        public static final String COLUMN_ANSWERED_COUNT = "answered_count";
    }
    public static abstract class Alternative_Answer_Not_String implements BaseColumns {
        public static final String TABLE_NAME = "alternative_answer_not_string";
        public static final String COLUMN_ENTRY = "entry";
        public static final String COLUMN_ALTERNATIVE_ANSWER = "alternative_answer";
        public static final String COLUMN_ANSWERED_COUNT = "answered_count";
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 = "created_at";
    public static abstract class Old_Field_DBV6_AppV32 implements BaseColumns {
        public static final int DEFAULT_TYPE = 0;
        public static final int NOTEBOOK_TYPE = 1;
        // This constant represents the name of the table which contains the data of the names of the fields that the user will create.
        public static final String TABLE_NAME = "field";
        // This constant represents the name of the column which will contain the names of the fields that the user will create in the field table.
        public static final String COLUMN_FIELD_NAME = "field_name";
        public static final String COLUMN_FIELD_TYPE = "field_type";
    }
    public static abstract class Old_Question_DBV6_AppV32 implements BaseColumns {
        public static final String TABLE_NAME = "question";
        public static final String COLUMN_QUESTION_ENTRY = "question_entry";
    }
    public static abstract class Old_Answer_DBV6_AppV32 implements BaseColumns {
        public static final String TABLE_NAME = "answer";
        public static final String COLUMN_ANSWER_ENTRY = "answer_entry";
    }
    public static abstract class Old_Linker_DBV6_AppV32 implements BaseColumns {
        public static final String TABLE_NAME = "linker";
        public static final String COLUMN_QUESTION_LINK = "question_link";
        public static final String COLUMN_ANSWER_LINK = "answer_link";
        public static final String COLUMN_FIELD_LINK = "field_link";
        public static final String COLUMN_STUDY_DURING_ORDER = "study_during_order";
        public static final String COLUMN_TEST_ORDER = "test_order";
        public static final String COLUMN_TEST_WRONG_ANSWERS = "test_wrong_answers";
    }
    public static abstract class Old_Study_During_Temp_DBV6_AppV32 implements BaseColumns {
        public static final String TABLE_NAME = "study_during_temp_table";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_FIELD = "field";
    }
    public static abstract class Check implements BaseColumns {
        public static final int CHECK_NUMBER = 4179198;
        public static final String TABLE_NAME = "check_table";
        public static final String COLUMN_CHECK_NUMBER = "check_number";
    }

    public static abstract class OldField implements BaseColumns {
        // This constant represents the name of the table which contains the data of the names of the fields that the user will create.
        public static final String TABLE_NAME = "field";
        // This constant represents the name of the column which will contain the names of the fields that the user will create in the field table.
        public static final String COLUMN_FIELD_NAME = "field_name";
        // This constant represents the string suffix which will (with the field name) represent the name of the table of the question
        // The question table name will be always fieldName + TABLE_QUESTION_SUFFIX
        public static final String TABLE_QUESTION_SUFFIX = "_question";
        // This constant represents the string suffix which will (with the field name) represent the name of the column of the question table
        // In the question table, the question column name will be always fieldName + COLUMN_QUESTION_SUFFIX
        public static final String COLUMN_QUESTION_SUFFIX = "_question_entry";
        // This constant represents the string suffix which will (with the field name) represent the name of the table of the answer
        // The answer table name will be always fieldName + TABLE_ANSWER_SUFFIX
        public static final String TABLE_ANSWER_SUFFIX = "_answer";
        // This constant represents the string suffix which will (with the field name) represent the name of the column of the answer table
        // In the answer table, the answer column name will be always fieldName + COLUMN_ANSWER_SUFFIX
        public static final String COLUMN_ANSWER_SUFFIX = "_answer_entry";
        /*This constant represents the string suffix which will (with the field name) represent the name of the table which will make the link between
         any question and andy answer
        The link table name will be always fieldName + TABLE_LINK_SUFFIX*/
        public static final String TABLE_LINK_SUFFIX = "_question_answer";
        /*This constant represents the string suffix which will (with the field name) represent the name of the column of the link table which will contain
        the question to link between the question table and the answer table.
        In the link table, the question column name will be always fieldName + COLUMN_QUESTION_LINK_SUFFIX*/
        public static final String COLUMN_QUESTION_LINK_SUFFIX = "_question_link_entry";
        /*This constant represents the string suffix which will (with the field name) represent the name of the column of the link table which will contain
         the answer to link between the question table and the answer table.
         In the link table, the anwwer column name will be always fieldName + COLUMN_ANSWER_LINK_SUFFIX*/
        public static final String COLUMN_ANSWER_LINK_SUFFIX = "_answer_link_entry";
        // this column will hold the timestamp of the addition of the row
        public static final String COLUMN_CREATED_AT = "created_at";
    }
}
