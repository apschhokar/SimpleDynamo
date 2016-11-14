package edu.buffalo.cse.cse486586.simpledynamo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;


public class ServerTask extends AsyncTask<ServerSocket, String, Void> {

    static String TAG = ServerTask.class.getSimpleName();
    static String[] dataReceived;
    static Integer totalCount = 0;


    @Override
    protected Void doInBackground(ServerSocket... sockets) {
        ServerSocket serverSocket = sockets[0];
        Log.e(TAG, "call me atleast: ");

        SimpleDynamoProvider simple1 = new SimpleDynamoProvider();

            while (true) {
                try {
                    Socket server = serverSocket.accept();
                    ObjectInputStream readData = new ObjectInputStream(server.getInputStream());
                    try {
                        dataReceived = (String[]) readData.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, e.toString());
                    }
                    
                   // wait
                    while (!simple1.isInitialized){
                    }

                    Log.e("received on server side", "----------------------------->>>>>>>>>>> " + dataReceived[0]);

                    if (dataReceived[0].equals("InsertMin")) {
                        Log.e(TAG, "----------------->>>>> Inserted Minimum" + dataReceived[1]);
                        SimpleDynamoProvider simple = new SimpleDynamoProvider();
                        simple.insertInThisNode(dataReceived[1], dataReceived[2]);
                    }

                    if (dataReceived[0].equals("InsertHere")) {
                        Log.e(TAG, "Got in server to insert here  ---- >" + dataReceived[1]);
                        SimpleDynamoProvider simple = new SimpleDynamoProvider();
                        simple.insertInThisNode(dataReceived[1], dataReceived[2]);
                        //simple.replicate(dataReceived[1], dataReceived[2]);
                    }

                    if (dataReceived[0].equals("Replicate")) {
                        Log.e(TAG, "Got in server to replicate ---- >" + dataReceived[1]);
                        SimpleDynamoProvider simple = new SimpleDynamoProvider();
                        simple.insertInThisNode(dataReceived[1], dataReceived[2]);
                    }

                    if (dataReceived[0].equals("*")) {
                        SimpleDynamoProvider simple = new SimpleDynamoProvider();
                        ContentResolver mContentResolver = simple.globalContext.getContentResolver();
                        Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");

                        HashMap<String, String> sendBack = new HashMap<String, String>();
                        Log.e(TAG, "Query -------> Everything * ----------->>>>");
                        Cursor resultCursor = mContentResolver.query(mUri, null,
                                "@", null, null);
                        Log.e(TAG, "Query ------->just before  Everything * ----------->>>>");
                        resultCursor.moveToFirst();

                        while (!resultCursor.isAfterLast()) {
                            // sendBack.put() = resultCursor.getString(0)
                            Log.e(TAG, "Key cursor ---->" + resultCursor.getString(resultCursor.getColumnIndex("Key")));
                            Log.e(TAG, "Value cursor ---->" + resultCursor.getString(resultCursor.getColumnIndex("Value")));

                            sendBack.put(resultCursor.getString(resultCursor.getColumnIndex("Key")), resultCursor.getString(resultCursor.getColumnIndex("Value")));
                            resultCursor.moveToNext();
                        }

                        Log.e(TAG, "Query ------->After  Everything * ----------->>>>");

                        ObjectOutputStream sendData = new ObjectOutputStream(server.getOutputStream());
                        sendData.writeObject(sendBack);
                        //  publishProgress(dataReceived);
                    }

                    if (dataReceived[0].equals("*recover")) {
                        SimpleDynamoProvider simple = new SimpleDynamoProvider();
                        ContentResolver mContentResolver = simple.globalContext.getContentResolver();
                        Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");
                        HashMap<String, String> sendBack = new HashMap<String, String>();
                        Log.e(TAG, "Query -------> Everything * ----------->>>>");

                        Cursor resultCursor = mContentResolver.query(mUri, null,
                                "@recover", null, null);
                        Log.e(TAG, "Query ------->just before  Everything * ----------->>>>");

                        resultCursor.moveToFirst();

                        while (!resultCursor.isAfterLast()) {
                            // sendBack.put() = resultCursor.getString(0)
                            Log.e(TAG, "Key cursor ---->" + resultCursor.getString(resultCursor.getColumnIndex("Key")));
                            Log.e(TAG, "Value cursor ---->" + resultCursor.getString(resultCursor.getColumnIndex("Value")));

                            sendBack.put(resultCursor.getString(resultCursor.getColumnIndex("Key")), resultCursor.getString(resultCursor.getColumnIndex("Value")));
                            resultCursor.moveToNext();
                        }

                        Log.e(TAG, "Query ------->After  Everything * ----------->>>>");
                        ObjectOutputStream sendData = new ObjectOutputStream(server.getOutputStream());
                        sendData.writeObject(sendBack);
                        //  publishProgress(dataReceived);
                    }



                    if (dataReceived[0].equals("Retrieve")) {
                        SimpleDynamoProvider simple = new SimpleDynamoProvider();
                        ContentResolver mContentResolver = simple.globalContext.getContentResolver();
                        Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");

                        Log.e(TAG, "Query -------> Got in server ----------->>>>" + simple.MyPort);
                        Log.e(TAG, "Query -------> recived key" + dataReceived[1]);

                        Cursor resultCursor = simple.returnFromThisNode(dataReceived[1]);
                        String Send = "";
                        if (resultCursor.moveToFirst()) // data?
                            Send = resultCursor.getString(resultCursor.getColumnIndex("Value"));

                        resultCursor.close();

                        Log.e("DataRetreived", " In server cursor Retreived is this ----------->>>>" + Send);
                        String[] toSend = {Send};
                        ObjectOutputStream sendData = new ObjectOutputStream(server.getOutputStream());
                        sendData.writeObject(toSend);
                    }


                    if (dataReceived[0].equals("deleteAll")) {
                        //SimpleDhtProvider simple = new SimpleDhtProvider();
                    }

                    if (dataReceived[0].equals("delete")) {
                        SimpleDynamoProvider simple = new SimpleDynamoProvider();
//                        ContentResolver mContentResolver = simple.globalContext.getContentResolver();
//                        Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");
//
//                        simple.delete(mUri , dataReceived[1] , null);
                         simple.deleteHere(dataReceived[1]);
                    }

                    if (!server.isClosed()) {
                        server.close();
                    }
                    Log.e(TAG, "publish information: ");

                } catch (UnknownHostException e) {
                    Log.e(TAG, "ServerTask UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ServerTask socket IOException");
                }
          }
    }




    protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().*/

            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             *
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */

        Log.e(TAG, "Got in server: " + strings[1] +"  " + strings[2]);
        
        ContentValues values = new ContentValues();
        values.put("key"  ,strings[1]);
        values.put("value" , strings[2]);

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority("edu.buffalo.cse.cse486586.simpledht.provider");
        uriBuilder.scheme("content");

        Uri uri = uriBuilder.build();
        try {
            SimpleDynamoProvider simple =  new SimpleDynamoProvider();
            simple.insert(uri , values);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return;
    }


    public Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }
}

