package com.ahmedsameha1.peninbin.database;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.utils.InvalidTextFileFormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ahmed on 7/3/16.
 */
public class ImportTextFile {
    public static final String EXPORT_IMPORT_DIRECTORY_NAME = "StudyWithoutPen";
    private static final int FAILED_IMPORT_NOTIFICATION = 0;
    private static final int FAILED_IMPORT_INVALID_FILE_NOTIFICATION = 1;
    private static final int SUCCESSFUL_IMPORT_NOTIFICATION = 2;
    private static final int ORDER = 4;
    private static final int RANK = 3;
    private static Thread importThread;


    public static void startImportThread(final Context context, final String file_path) {
        importThread = new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            importFields(context, file_path);
        });
        importThread.start();
    }

    public static boolean isThreadRunning() {
        if (importThread == null || !importThread.isAlive()) {
            return false;
        }
        return true;
    }

    private static ArrayList<Object[]> checkSort(String file_path) throws
            InvalidTextFileFormatException, IOException {
        ArrayList<Object[]> arrayList = new ArrayList<>();
        File file = new File(file_path);
        BufferedReader bufferedReader = null;
        boolean valid_structure = true;

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
            arrayList.add(new Object[]{field_name, question, answer, rank, order});
            text_line = bufferedReader.readLine();
        }
        bufferedReader.close();
        Collections.sort(arrayList, (entry1, entry2) -> {
            if ((Integer) entry1[ORDER] == -1 && (Integer) entry2[ORDER] == -1) {
                if ((Integer) entry1[RANK] == -1 && (Integer) entry2[RANK] == -1) {
                    return 0;
                } else if ((Integer) entry1[RANK] == -1) {
                    return 1;
                } else if ((Integer) entry2[RANK] == -1) {
                    return -1;
                }
            } else {
                if ((Integer) entry1[ORDER] == -1) {
                    return 1;
                } else if ((Integer) entry2[ORDER] == -1) {
                    return -1;
                }
            }

            if ((Integer) entry1[ORDER] < (Integer) entry2[ORDER]) {
                return -1;
            } else if ((Integer) entry1[ORDER] > (Integer) entry2[ORDER]) {
                return 1;
            } else if ((Integer) entry1[RANK] > (Integer) entry2[RANK]) {
                return -1;
            } else if ((Integer) entry1[RANK] < (Integer) entry2[RANK]) {
                return 1;
            } else {
                return 0;
            }
        });
        return arrayList;
    }

    public static void importFields(Context context, String file_path) {
        CursorLoaderAndUpdateDatabase mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(context);
        SQLiteDatabase sqLiteDatabase = mySQLiteCursorLoader.getSqLiteDatabase();
        try {
            ArrayList<Object[]> arrayList = checkSort(file_path);
            sqLiteDatabase.beginTransactionNonExclusive();
            DatabaseReadOperations databaseOperations = new DatabaseReadOperations(context);
            for (Object[] temp : arrayList) {
                String field_name = (String) temp[0];
                String question = ((String) temp[1]).trim();
                String answer = ((String) temp[2]).trim();
                int order = (int) temp[ORDER];
                int rank = (int) temp[RANK];
                long field_id = -1;
                if (databaseOperations.checkFieldDuplication(field_name)) {
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
                } else {
                    if (!field_name.equals("")) {
                        mySQLiteCursorLoader.createField(field_name, Contract.Field.STRING_DATA_TYPE);
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
            }
            sqLiteDatabase.setTransactionSuccessful();
            showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        } catch (InvalidTextFileFormatException e) {
            showNotification(context, FAILED_IMPORT_INVALID_FILE_NOTIFICATION, (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        } catch (IOException e) {
            showNotification(context, FAILED_IMPORT_NOTIFICATION, (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        }
        finally {
            if ( sqLiteDatabase.inTransaction() ) {
                sqLiteDatabase.endTransaction();
            }
        }
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
                resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
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
                resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
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
                resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
        }
    }
}
