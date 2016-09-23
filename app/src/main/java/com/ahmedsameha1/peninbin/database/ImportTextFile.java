package com.ahmedsameha1.peninbin.database;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.ImportNotebookFieldFromTextFileException;
import com.ahmedsameha1.peninbin.utils.InvalidTextFileFormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Ahmed on 7/3/16.
 */
public class ImportTextFile {
    public static final String EXPORT_IMPORT_DIRECTORY_NAME = "StudyWithoutPen";
    private static final int FAILED_IMPORT_NOTIFICATION = 0;
    private static final int FAILED_IMPORT_INVALID_FILE_NOTIFICATION = 1;
    private static final int SUCCESSFUL_IMPORT_NOTIFICATION = 2;
    private static final int IMPORT_NOTEBOOK_FIELD_FROM_TEXT_FILE = 3;
    private static Thread importThread;


    public static void startImportThread(final Context context, final String file_path) {
        importThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                importFields(context, file_path);
            }
        });
        importThread.start();
    }

    public static boolean isThreadRunning () {
        if ( importThread == null || !importThread.isAlive() ) {
            return false;
        }
        return true;
    }

    public static void importFields(Context context, String file_path) {
        /*String filePath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + EXPORT_IMPORT_DIRECTORY_NAME + File.separator + fileName;*/
        File file = new File(file_path);
        BufferedReader bufferedReader = null;
        boolean valid_structure = true;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            while (line != null) {
                int result = CompareUtility.checkTextLineToImport(line);
                if (result == 0) {
                    valid_structure = false;
                    break;
                }
                line = bufferedReader.readLine();
            }
            if (!valid_structure) {
                throw new InvalidTextFileFormatException();
            }
            CursorLoaderAndUpdateDatabase mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(context);
            DatabaseReadOperations databaseOperations = new DatabaseReadOperations(context);
            bufferedReader = new BufferedReader(new FileReader(file));
            String text_line = bufferedReader.readLine();
            while (text_line != null) {
                int field_separator_index = text_line.indexOf("@@@");
                int question_separator_index = text_line.indexOf("~~~");
                String field_name = text_line.substring(0, field_separator_index);
                String question = text_line.substring(field_separator_index + 3, question_separator_index);
                String answer = null;
                int order = -1;
                int rank = -1;
                if (text_line.contains("%%%")) {
                    int answer_separator_index = text_line.indexOf("%%%");
                    answer = text_line.substring(question_separator_index + 3, answer_separator_index);
                    rank = Integer.parseInt(text_line.substring(answer_separator_index + 3));
                } else if (text_line.contains("###")) {
                    int answer_separator_index = text_line.indexOf("###");
                    answer = text_line.substring(question_separator_index + 3, answer_separator_index);
                    order = Integer.parseInt(text_line.substring(answer_separator_index + 3));
                } else {
                    answer = text_line.substring(question_separator_index + 3);
                }
                ////////////
                long field_id = -1;
                if (databaseOperations.checkFieldDuplication(field_name)) {
                    field_id = databaseOperations.getFieldId(field_name);
                    if (question.length() > 0 && answer.length() > 0) {
                        if (!databaseOperations.checkEntryDuplication(question, answer, field_id, Contract.Field.STRING_DATA_TYPE)) {
                            if (databaseOperations.getFieldType(field_id) != Constants.FieldTypes.NOTEBOOK_TYPE) {
                                if (rank >= 0) {
                                    mySQLiteCursorLoader.insertEntryWithRank(Contract.Field.STRING_DATA_TYPE, question, answer, field_id, rank);
                                } else if (order >= 0) {
                                    mySQLiteCursorLoader.insertEntryWithOrder(Contract.Field.STRING_DATA_TYPE, question, answer, field_id, order);
                                } else {
                                    mySQLiteCursorLoader.insertEntry(Contract.Field.STRING_DATA_TYPE, question, answer, field_id);
                                }
                            } else {
                                throw new ImportNotebookFieldFromTextFileException();
                            }
                        }
                    }
                } else {
                    if (!field_name.equals("")) {
                        mySQLiteCursorLoader.createField(field_name, Constants.FieldTypes.DEFAULT_TYPE, Contract.Field.STRING_DATA_TYPE);
                        field_id = databaseOperations.getFieldId(field_name);
                        if (question.length() > 0 && answer.length() > 0) {
                            if (!databaseOperations.checkEntryDuplication(question, answer, field_id, Contract.Field.STRING_DATA_TYPE)) {
                                if (rank >= 0) {
                                    mySQLiteCursorLoader.insertEntryWithRank(Contract.Field.STRING_DATA_TYPE, question, answer, field_id, rank);
                                } else if (order >= 0) {
                                    mySQLiteCursorLoader.insertEntryWithOrder(Contract.Field.STRING_DATA_TYPE, question, answer, field_id, order);
                                } else {
                                    mySQLiteCursorLoader.insertEntry(Contract.Field.STRING_DATA_TYPE, question, answer, field_id);
                                }
                            }
                        }
                    }
                }
                text_line = bufferedReader.readLine();
            }
            bufferedReader.close();
            showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        } catch (InvalidTextFileFormatException e) {
            showNotification(context, FAILED_IMPORT_INVALID_FILE_NOTIFICATION, (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        } catch (ImportNotebookFieldFromTextFileException e) {
            showNotification(context, IMPORT_NOTEBOOK_FIELD_FROM_TEXT_FILE, (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        } catch (IOException e) {
            showNotification(context, FAILED_IMPORT_NOTIFICATION, (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        }
        /*try {
            CursorLoaderAndUpdateDatabase mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(context);
            DatabaseReadOperations databaseOperations = new DatabaseReadOperations(context);
            bufferedReader = new BufferedReader(new FileReader(file));
            String text_line = bufferedReader.readLine();
            while ( text_line != null ) {
                int field_separator_index = text_line.indexOf("@@@");
                int question_separator_index = text_line.indexOf("~~~");
                String field_name = text_line.substring(0,field_separator_index);
                String question = text_line.substring(field_separator_index + 3,question_separator_index);
                String answer = null;
                int rank = -1;
                if ( text_line.contains("%%%") ) {
                    int answer_separator_index = text_line.indexOf("%%%");
                    answer = text_line.substring(question_separator_index + 3, answer_separator_index);
                    rank = Integer.parseInt(text_line.substring(answer_separator_index + 3));
                } else {
                    answer = text_line.substring(question_separator_index + 3);
                }
                ////////////
                long field_id = -1;
                if ( databaseOperations.checkFieldDuplication(field_name) ) {
                    field_id = databaseOperations.getFieldId(field_name);
                    if (question.length() > 0 && answer.length() > 0) {
                        if (!databaseOperations.checkEntryDuplication(question, answer, field_id, Contract.Field.STRING_DATA_TYPE) ) {
                            if ( rank == -1 ) {
                                mySQLiteCursorLoader.insertEntry(Contract.Field.STRING_DATA_TYPE,question,answer,field_id);
                            } else {
                                mySQLiteCursorLoader.insertEntry(Contract.Field.STRING_DATA_TYPE,question,answer,field_id,rank);
                            }
                        }
                    }
                } else {
                    if ( ! field_name.equals("") ) {
                        mySQLiteCursorLoader.createField(field_name, Constants.FieldTypes.DEFAULT_TYPE, Contract.Field.STRING_DATA_TYPE);
                        field_id = databaseOperations.getFieldId(field_name);
                        if (question.length() > 0 && answer.length() > 0) {
                            if (!databaseOperations.checkEntryDuplication(question, answer, field_id, Contract.Field.STRING_DATA_TYPE) ) {
                                if ( rank == -1 ) {
                                    mySQLiteCursorLoader.insertEntry(Contract.Field.STRING_DATA_TYPE,question,answer,field_id);
                                } else {
                                    mySQLiteCursorLoader.insertEntry(Contract.Field.STRING_DATA_TYPE,question,answer,field_id,rank);
                                }
                            }
                        }
                    }
                }
                text_line = bufferedReader.readLine();
            }
        }
        catch ( IOException e) {
            showNotification(context,FAILED_IMPORT_NOTIFICATION,(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        }*/
    }

    private static void showNotification(Context context, int notificationId, NotificationManager notificationManager) {
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        TaskStackBuilder stackBuilder = null;
        switch (notificationId) {
            case FAILED_IMPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.import_failed))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case FAILED_IMPORT_INVALID_FILE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.invalid_file))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case IMPORT_NOTEBOOK_FIELD_FROM_TEXT_FILE:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.cannot_import_entries_to_notebook_fields_using_text_files))
                                .setContentText(context.getResources().getString(R.string.some_entries_have_not_imported))
                                .setTicker(context.getResources().getString(R.string.some_entries_have_not_imported))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case SUCCESSFUL_IMPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_completed))
                                .setContentText(context.getResources().getString(R.string.import_completed))
                                .setTicker(context.getResources().getString(R.string.import_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
        }
    }
}
