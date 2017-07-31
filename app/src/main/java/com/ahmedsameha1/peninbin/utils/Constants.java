package com.ahmedsameha1.peninbin.utils;

/**
 * Created by ahmed on 6/11/2016.
 */
public class Constants {
    public static final int NOT_EMULATED = 0;
    public static final String PAUSING = "pausing";
    public static final String CURRENT_FIELD_ID = "current_field_id";
    public static final int LONG_TIME = 100000;

    public static abstract class FieldTypes {
        public static final int DEFAULT_TYPE = 0;
        public static final int KORAN_TYPE = 1;
        public static final int ENGLISH_DICTIONARY_TYPE = 2;
        public static final int NOTEBOOK_TYPE_REMOVED_FEATURE_V68 = 1;
        public static final int KORAN_TYPE_OLD_V68 = 2;
        public static final int ENGLISH_DICTIONARY_TYPE_OLD_V68 = 3;
    }

    public static abstract class Rank {
        public static final int VITAL = 3;
        public static final int IMPORTANT = 2;
        public static final int MODERATE = 1;
        public static final int LOW = 0;
    }

    public static abstract class TestRound {
        public static final int ASKED_AT_CURRENT_ROUND = 1;
        public static final int DID_NOT_ASKED_AT_CURRENT_ROUND = 0;
    }

    public static abstract class CopyMove {
        public static final int COPY_FIELD = 0;
        public static final int COPY_ENTRIES = 1;
        public static final int MOVE_ENTRIES = 2;
    }

    public static abstract class TestType {
        public static final int ENHANCED_TEST = 0;
        public static final int FULLY_RANDOM_TEST = 1;
    }

    public static abstract class TimeOfAnswer {
        public static final int NOT_USED = 0;
        public static final int TEST = 0;
        public static final int STUDY_A_PERIOD = 1;
        public static final int NOTIFY = 0;
        public static final int MOVE_NEXT = 1;
    }

    public static abstract class ElapsedPausedSessionTime {
        public static final int STUDY_A_PERIOD = 0;
        public static final int ENHANCED_TEST = 1;
        public static final int FULLY_RANDOM_TEST = 2;
        public static final int STUDY_AGAIN_ENHANCED_TEST = 3;
        public static final int STUDY_AGAIN_FULLY_RANDOM_TEST = 4;
    }

    public class Study_Till_Correct {
        public static final int STUDY_PERIOD = 0;
        public static final int STUDY_AGAIN_ENHANCED_TEST = 1;
        public static final int STUDY_AGAIN_FULLY_RANDOM_TEST = 2;
        public static final int NOT_IN_REPEATED = 0;
        public static final int IN_REPEATED = 1;
    }
}
