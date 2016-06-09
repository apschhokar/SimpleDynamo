package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDynamoProvider extends ContentProvider {
	static final String TAG = SimpleDynamoProvider.class.getSimpleName();
    final static String URL = "content://edu.buffalo.cse.cse486586.simpledynamo.provider";


    static String Hashedsuccessor = "";
    static String HashedPredesscor = "";
    static String MinEmulator ="";
    static String MaxEmulator ="";
    static String MinHash = "";
    static String MaxHash = "";
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static  String Hashed_REMOTE_PORT0 = "";
    static  String Hashed_REMOTE_PORT1 = "";
    static  String Hashed_REMOTE_PORT2 = "";
    static  String Hashed_REMOTE_PORT3 = "";
    static  String Hashed_REMOTE_PORT4 = "";
    static final int SERVER_PORT = 10000;
    static String MyPort = "";
    static String MyHashedPort = "";
    static String MySuccessor = "";
    static String MyPredessor = "";
    static boolean singleEmu = true;
    static String[] array = new String[5];

    static Context globalContext;
    static int finalinsertCount=0;
    static int actualinsertCount=0;
    static int calledCOunt=0;

    static HashMap<String , String> MapKeyValue = new HashMap<String, String>();
    static HashSet<String> keyHashSet = new HashSet<String>();

    static boolean rWaitNormalOver = false;
    static boolean rWaitMinOver = false;
    static MatrixCursor StarCursor = new MatrixCursor(new String[]{"key", "value"});
    static MatrixCursor RecoverPreviousCursor = new MatrixCursor(new String[]{"key", "value"});
    static MatrixCursor RecoverForwardCursor = new MatrixCursor(new String[]{"key", "value"});

    static ArrayList<String> emulatorString = new ArrayList<String>();
    static ArrayList<String> AllAvailablePorts = new ArrayList<String>();
    static ArrayList<String> AllAvailableHashedPorts = new ArrayList<String>();

    static HashMap<String, String> receivedMapForward = new HashMap<String, String>();
    static HashMap<String, String> receivedMapPrevious = new HashMap<String, String>();

    static boolean isInitialized = false;

    static ArrayList<String>  decideKey = new ArrayList<String>();


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub

        if (selection.equals("*")) {
            deleteALl();
        } else if (selection.equals("@")) {
            deleteLocal();
        } else {
//            FileInputStream input;
//            try {
//                File file = new File(getContext().getFilesDir().getAbsolutePath() + File.separator + selection);
//                Log.v(TAG, "deleted file was" + file.toString());
//                file.delete();
//            } catch (Exception e) {
//                Log.v(TAG, "could not read file");
//                e.printStackTrace();
//            }
            Log.v("delete ", selection);
            itsDeletePOsition(selection);
        }
        return 0;
    }


    public void deleteHere(String Key){
        Log.e(TAG, "Replicas was also deleted");

        FileInputStream input;
        try {
            File file = new File(globalContext.getFilesDir().getAbsolutePath() + File.separator + Key);
            Log.v(TAG, "deleted file was " + file.toString());
            file.delete();
        } catch (Exception e) {
            Log.v(TAG, "could not read file");
            e.printStackTrace();
        }
        Log.v("delete replica ", Key);
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    public void itsDeletePOsition(String toBeInserted) {

        Log.e(TAG, "All Are --------->" + returnWithoutHash(AllAvailableHashedPorts.get(0)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(1)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(2)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(3)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(4)));
        String insert = getHashValue(toBeInserted);
        String port = "";

        if (insert.compareTo(AllAvailableHashedPorts.get(0)) <= 0 || insert.compareTo(AllAvailableHashedPorts.get(4)) >= 0) {
            Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(0)));
            port = returnWithoutHash(AllAvailableHashedPorts.get(0));

        } else {

            if (insert.compareTo(AllAvailableHashedPorts.get(0)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(1)) <= 0) {
                Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(1)));
                port = returnWithoutHash(AllAvailableHashedPorts.get(1));

            } else if (insert.compareTo(AllAvailableHashedPorts.get(1)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(2)) <= 0) {
                Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(2)));
                port = returnWithoutHash(AllAvailableHashedPorts.get(2));

            } else if (insert.compareTo(AllAvailableHashedPorts.get(2)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(3)) <= 0) {
                Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(3)));
                port = returnWithoutHash(AllAvailableHashedPorts.get(3));

            } else if (insert.compareTo(AllAvailableHashedPorts.get(3)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(4)) <= 0) {
                Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(4)));
                port = returnWithoutHash(AllAvailableHashedPorts.get(4));

            }
        }

        new DeleteReplicas().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, port, toBeInserted);
        deleteCorrectAndReplicas(port , toBeInserted);

    }


    @Override
    public  Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
//
//        while (!isInitialized){
//
//        }

            Log.e(TAG, " insert hoga ---------> " + (String) values.get("key"));
            String toBeInserted = getHashValue((String) values.get("key"));


            MySuccessor = returnWithoutHash(Hashedsuccessor);
            MyPredessor = returnWithoutHash(HashedPredesscor);
            MinEmulator = returnWithoutHash(MinHash);
            MaxEmulator = returnWithoutHash(MaxHash);

            Log.e("HashLess", "pred " + MyPredessor + " itself ------" + MyPort + " Succ -----------" + MySuccessor + " Min --------" + MinEmulator + " Max --------" + MaxEmulator);

            itsPOsition((String) values.get("key"), values.get("value").toString());

            return uri;

    }



    public void itsPOsition(String toBeInserted , String Value) {

            Log.e(TAG, "All Are --------->" + returnWithoutHash(AllAvailableHashedPorts.get(0)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(1)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(2)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(3)) + " " + returnWithoutHash(AllAvailableHashedPorts.get(4)));
            String insert = getHashValue(toBeInserted);
            String port = "";

            if (insert.compareTo(AllAvailableHashedPorts.get(0)) <= 0 || insert.compareTo(AllAvailableHashedPorts.get(4)) >= 0) {
                Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(0)));
                port = returnWithoutHash(AllAvailableHashedPorts.get(0));

            } else {

                if (insert.compareTo(AllAvailableHashedPorts.get(0)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(1)) <= 0) {
                    Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(1)));
                    port = returnWithoutHash(AllAvailableHashedPorts.get(1));

                } else if (insert.compareTo(AllAvailableHashedPorts.get(1)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(2)) <= 0) {
                    Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(2)));
                    port = returnWithoutHash(AllAvailableHashedPorts.get(2));

                } else if (insert.compareTo(AllAvailableHashedPorts.get(2)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(3)) <= 0) {
                    Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(3)));
                    port = returnWithoutHash(AllAvailableHashedPorts.get(3));

                } else if (insert.compareTo(AllAvailableHashedPorts.get(3)) >= 0 && insert.compareTo(AllAvailableHashedPorts.get(4)) <= 0) {
                    Log.e(TAG, "Deserve to be ---------> " + returnWithoutHash(AllAvailableHashedPorts.get(4)));
                    port = returnWithoutHash(AllAvailableHashedPorts.get(4));

                }
            }

            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, port, toBeInserted, Value);
            replicate(toBeInserted, Value , port);
    }



    public String returnWithoutHash(String temp){
        String toReturn = "";

        if(temp.equals(Hashed_REMOTE_PORT0)) {
            toReturn = REMOTE_PORT0;
        }
        else if(temp.equals(Hashed_REMOTE_PORT1)){
            toReturn = REMOTE_PORT1;
        }
        else if(temp.equals(Hashed_REMOTE_PORT2)){
            toReturn = REMOTE_PORT2;
        }
        else if(temp.equals(Hashed_REMOTE_PORT3)){
            toReturn = REMOTE_PORT3;
        }
        else if(temp.equals(Hashed_REMOTE_PORT4)){
            toReturn = REMOTE_PORT4;
        }
        return  toReturn;
    }








    //taken from PA1 and modified for 5 AVDS
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

                try {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(msgs[0]));
                    // String msgToSend = msgs[0];

                    String[] toSend = {"InsertHere", msgs[1], msgs[2]};
                    ObjectOutputStream sendData = new ObjectOutputStream(socket.getOutputStream());

                    Log.e("honeysingh", " data is this -----------" + msgs[0] + " " + msgs[1] + " " + msgs[2]);

                    sendData.writeObject(toSend);

                    //toSend.flush();
                    socket.close();

                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException");
                }

                return null;
            }
    }


    public void deleteCorrectAndReplicas( String destinationPort , String Key){

        Log.e(TAG , " delette Replicate keys are called");



        ArrayList<String> deletingReplicas = new ArrayList<String>();
        String next1Port = "";
        String next2Port = "";


        if (destinationPort.equals("11124")){
            next1Port = "11112";
            next2Port = "11108";
        }
        else if(destinationPort.equals("11112")){
            next1Port = "11108";
            next2Port = "11116";
        }
        else if(destinationPort.equals("11108")){
            next1Port = "11116";
            next2Port = "11120";
        }
        else if(destinationPort.equals("11116")){
            next1Port = "11120";
            next2Port = "11124";
        }
        else if(destinationPort.equals("11120")){
            next1Port = "11124";
            next2Port = "11112";
        }

        deletingReplicas.add(next1Port);
        deletingReplicas.add(next2Port);

        for (String SendingPort : deletingReplicas) {
            Log.e(TAG , "delete Replicate keys sending to port");
            new DeleteReplicas().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, SendingPort, Key);
        }
    }


    public String nextTwoPort(String port){
        String nextPort = "";

        if (port.equals("11124")){
            nextPort = "11112";
        }
        else if(port.equals("11112")){
            nextPort = "11108";
        }
        else if(port.equals("11108")){
            nextPort = "11116";
        }
        else if(port.equals("11116")){
            nextPort = "11120";
        }
        else if(port.equals("11120")){
            nextPort = "11124";
        }

        return nextPort;
    }




    //taken from PA1 and modified for 5 AVDS
    private class DeleteReplicas extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            try {
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(msgs[0]));
                // String msgToSend = msgs[0];

                String[] toSend = {"delete", msgs[1]};
                ObjectOutputStream sendData = new ObjectOutputStream(socket.getOutputStream());

                Log.e(TAG, " delete is this -----------" + msgs[0] + " " + msgs[1]);

                sendData.writeObject(toSend);

                //toSend.flush();
                socket.close();

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }





    public void replicate(String key , String value , String destinationPort){

        Log.e(TAG , "Replicate keys are called");

        Integer a = Integer.parseInt(destinationPort);
        a = a / 2;
        String  destinationHashedPort = getHashValue(a.toString());

        ArrayList<String> replicatingSucc = new ArrayList<String>();
        int count = AllAvailableHashedPorts.size();

        for (int i = 0; i < AllAvailableHashedPorts.size(); i++) {
            if (destinationHashedPort.equals(AllAvailableHashedPorts.get(i))) {
                if ((AllAvailableHashedPorts.size() - i) > 2) {
                    replicatingSucc.add(returnWithoutHash(AllAvailableHashedPorts.get(i + 1)));
                    replicatingSucc.add(returnWithoutHash(AllAvailableHashedPorts.get(i + 2)));
                } else if ((AllAvailableHashedPorts.size() - i) == 2) {
                    replicatingSucc.add(returnWithoutHash(AllAvailableHashedPorts.get(count - 1)));
                    replicatingSucc.add(returnWithoutHash(AllAvailableHashedPorts.get(0)));
                } else if ((AllAvailableHashedPorts.size() - i) == 1) {
                    replicatingSucc.add(returnWithoutHash(AllAvailableHashedPorts.get(0)));
                    replicatingSucc.add(returnWithoutHash(AllAvailableHashedPorts.get(1)));
                }
            }
        }

        for (String SendingPort : replicatingSucc) {
            Log.e(TAG , "Replicate keys sending to port");
            new ReplicateDataClient().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, SendingPort, key, value);
        }
    }


    public class ReplicateDataClient extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

                Log.e(TAG, " replicating client -----------" + msgs[0] + " " + msgs[1]);

                    try {
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(msgs[0]));
                        // String msgToSend = msgs[0];

                        String[] toSend = {"Replicate", msgs[1], msgs[2]};
                        ObjectOutputStream sendData = new ObjectOutputStream(socket.getOutputStream());
                        Log.e(TAG, "replicating  data is this -----------" + msgs[1] + " " + msgs[2]);
                        sendData.writeObject(toSend);

                        //toSend.flush();
                        socket.close();

                    } catch (UnknownHostException e) {
                        Log.e(TAG, "ClientTask UnknownHostException");
                    } catch (IOException e) {
                        Log.e(TAG, "ClientTask socket IOException");
                    }

                return null;
        }
    }





    @Override
    public  Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
//        while (!isInitialized){
//
//        }

        // TODO Auto-generated method stub
            return MultipleAVD(selection);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }


    //////************* Hash Value ************************************************///////

    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }



    public String getHashValue(String toBeHashed){
        String hashed = "";

        try {
            hashed = genHash(toBeHashed);
        }catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException");
            e.printStackTrace();
        }

        //  Log.e(TAG, "returning Hash Value" + hashed);

        return hashed;
    }

    //////************* Hash Value ************************************************///////


    @Override
    public boolean onCreate() {
        // intialiseProvider();
        Log.e("m going to crash", "lol");

        globalContext = getContext();
        initialize();
        Log.e(TAG, "basic initialise done");

        recoverAndRetreive();
        isInitialized = true;
        // TODO Auto-generated method stub

        Log.e(TAG, "OnCreate done");

        return false;
    }


    public void SetContext(Context sContext) {
        this.globalContext = sContext;
        Log.e("context", globalContext.toString());
    }



    public void initialize() {

        TelephonyManager tel = (TelephonyManager) globalContext.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        MyPort = myPort;

        Integer b = Integer.parseInt(myPort);
        b = b / 2;

        try {
            MyHashedPort = genHash(b.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.v("prvider Port", MyPort);

        //code Snipets from PA1
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT, 15);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket" + e.toString());
            return;
        }

        Integer a = Integer.parseInt(REMOTE_PORT0);
        AllAvailablePorts.add(a.toString());
        a = a / 2;
        Log.e(TAG, "print the value of a is " + a.toString());
        array[0] = getHashValue(a.toString());
        Hashed_REMOTE_PORT0 = array[0];

        a = Integer.parseInt(REMOTE_PORT1);
        AllAvailablePorts.add(a.toString());
        a = a / 2;
        array[1] = getHashValue(a.toString());
        Hashed_REMOTE_PORT1 = array[1];

        a = Integer.parseInt(REMOTE_PORT2);
        AllAvailablePorts.add(a.toString());
        a = a / 2;
        array[2] = getHashValue(a.toString());
        Hashed_REMOTE_PORT2 = array[2];

        a = Integer.parseInt(REMOTE_PORT3);
        AllAvailablePorts.add(a.toString());
        a = a / 2;
        array[3] = getHashValue(a.toString());
        Hashed_REMOTE_PORT3 = array[3];

        a = Integer.parseInt(REMOTE_PORT4);
        AllAvailablePorts.add(a.toString());
        a = a / 2;
        array[4] = getHashValue(a.toString());
        Hashed_REMOTE_PORT4 = array[4];

        AllAvailableHashedPorts.add(Hashed_REMOTE_PORT0);
        AllAvailableHashedPorts.add(Hashed_REMOTE_PORT1);
        AllAvailableHashedPorts.add(Hashed_REMOTE_PORT2);
        AllAvailableHashedPorts.add(Hashed_REMOTE_PORT3);
        AllAvailableHashedPorts.add(Hashed_REMOTE_PORT4);



        // Sorting
        Collections.sort(AllAvailableHashedPorts, new Comparator<String>() {
            @Override
            public int compare(String fruit2, String fruit1) {
                return fruit2.compareTo(fruit1);
            }
        });

        int count = AllAvailableHashedPorts.size();

        MinHash = AllAvailableHashedPorts.get(0);
        MaxHash = AllAvailableHashedPorts.get(AllAvailableHashedPorts.size() - 1);


        for(int i = 0; i < (count-1) ;i++){
            if (MyHashedPort.equals(AllAvailableHashedPorts.get(i))){
                if (i < (AllAvailableHashedPorts.size() - 1)) {
                    Hashedsuccessor = AllAvailableHashedPorts.get(i + 1);
                } else if (i == (AllAvailableHashedPorts.size() - 1)) {
                    Hashedsuccessor = MinHash;
                }

                if (i > 0) {
                    HashedPredesscor = AllAvailableHashedPorts.get(i - 1);
                } else if (i == 0) {
                    HashedPredesscor = MaxHash;
                }
            }
        }

        MySuccessor = returnWithoutHash(Hashedsuccessor);
        MyPredessor = returnWithoutHash(HashedPredesscor);
        MinEmulator = returnWithoutHash(MinHash);
        MaxEmulator = returnWithoutHash(MaxHash);

    }




    //////************* Delete ************************************************///////

    public void deleteALl() {
        for (String filename : globalContext.fileList()) {
            try {
                File file = new File(globalContext.getFilesDir().getAbsolutePath() + File.separator + filename);
                Log.v(TAG, "deleted file was" + file.toString());
                file.delete();
            } catch (Exception e) {
                Log.v(TAG, "file read failed");
            }
        }
    }



    public void deleteLocal(){
        for (String filename:globalContext.fileList()){
            try{
                File file = new File(globalContext.getFilesDir().getAbsolutePath() + File.separator + filename);
                Log.v(TAG, "deleted file was" + file.toString());
                file.delete();
            } catch (Exception e) {
                Log.v(TAG, "file read failed");
            }
        }
    }

    //////************* Delete ************************************************///////


    //////************* Insert ************************************************///////


    public void insertInThisNode(String Key , String Value){

        FileInputStream input;
        String val ="";
              try {
                Log.v("File Descriptor", "------> before okay");

                  File file = new File(globalContext.getFilesDir().getAbsolutePath() + File.separator + Key);
                  Log.v(TAG, "deleted file was " + file.toString());
                  if(file.exists()){
                      FileInputStream stream = new FileInputStream(file);
                      BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                      String line = br.readLine();
                      String[] split = line.split("-");

                      Log.e(TAG ,Key + " line --->" +line + "Value ---->" + Value );
                      //keep the count of version
                      int countVersion = Integer.parseInt(split[1]);

                      if (split[0].equals(Value)){
                            //do nothing
                          Log.e(TAG, "inserted value and already stored values are ------> same , so just return "+ Key);

                          return;
                      }
                      else{
                          String[] splitValue = Value.split("-");
                          Log.e(TAG, "inserted value and already stored values are ------> not same " + Key);
                          try {
                              if (splitValue[1] == null){

                              }
                              else {
                                  int anotherCount = Integer.parseInt(splitValue[1]);
                                  if(anotherCount > countVersion){
                                      val = Value;
                                      Log.e(TAG, Key + " inserted value is ----->" +val+" anotherCount > countVersion");

                                  }
                                  else{
                                      val  = line;
                                      Log.e(TAG,  " inserted value is ----->" +val+" else part");

                                  }
                              }
                          }

                          catch(ArrayIndexOutOfBoundsException exception) {
                              countVersion = countVersion + 1;
                              val =  Value +"-"+ countVersion;
                              Log.e(TAG, Key +" inserted value is ----->" +val+" split[1]==null");
                          }
                      }
                  }
                  else {
                         val = Value +"-0";
                  }



                FileOutputStream output;
                output = globalContext.openFileOutput(Key , Context.MODE_PRIVATE);
                Log.v("File Descriptor", output.getFD().toString());
                output.write(val.getBytes());
                output.close();

                actualinsertCount++;
                Log.v("final insert ", "actually was true" + Value + "   " + actualinsertCount);

            } catch(Exception e) {
                Log.v("", "ROME parse error: " + e.toString());
            }


        finalinsertCount++;
        Log.v("final insert", Value + "   " + finalinsertCount);
    }

    //////************* Insert ************************************************///////







    //////************* Query ************************************************///////



    public Cursor MultipleAVD(String selection) {

            MatrixCursor Cursor = new MatrixCursor(new String[]{"key", "value"});

            int count = 0;

            //flag set after the grader test for single emulator

            if (selection.equals("@")) {
                Log.e(TAG, "Query --------> everything from this emulator");

                for (String filename : globalContext.fileList()) {
                    try {
                        File file = new File(globalContext.getFilesDir().getAbsolutePath() + File.separator + filename);
                        Log.v("file location", file.toString());

                        FileInputStream stream = new FileInputStream(file);
                        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                        String line = br.readLine();

                        String[] onlyValue  = line.split("-");

                        Log.v("---- line ---- " + count+" ", line);
                        br.close();
                        Cursor.addRow(new String[]{filename, onlyValue[0]});
                        count++;

                    } catch (Exception e) {
                        Log.v(TAG, "file read failed");
                    }
                }
            }



         else if (selection.equals("@recover")) {
            Log.e(TAG, "Query --------> everything from this emulator");

            for (String filename : globalContext.fileList()) {
                try {
                    File file = new File(globalContext.getFilesDir().getAbsolutePath() + File.separator + filename);
                    Log.v("file location", file.toString());

                    FileInputStream stream = new FileInputStream(file);
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                    String line = br.readLine();

                    Log.v("---- line ---- " + count+" ", line);
                    br.close();
                    Cursor.addRow(new String[]{filename, line});
                    count++;

                } catch (Exception e) {
                    Log.v(TAG, "file read failed");
                }
            }
        }



        //////////************************** * in the ring ****************************//////////////

            else if (selection.equals("*")) {
                Log.e(TAG, "Query --------> going to every emulator");
                Cursor = getAllCursorinRing();
            }

            //////////************************** Normal retreival the ring ****************************//////////////

            else {
                String toBeRetreived = getHashValue(selection);

                if (toBeRetreived.compareTo(AllAvailableHashedPorts.get(0)) <= 0 || toBeRetreived.compareTo(AllAvailableHashedPorts.get(4)) >= 0) {
                    try {
                        Cursor = getSuccCursorinRing(selection, returnWithoutHash(AllAvailableHashedPorts.get(0)));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {

                    if (toBeRetreived.compareTo(AllAvailableHashedPorts.get(0)) >= 0 && toBeRetreived.compareTo(AllAvailableHashedPorts.get(1)) <= 0) {

                        try {
                            Cursor = getSuccCursorinRing(selection, returnWithoutHash(AllAvailableHashedPorts.get(1)));
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else if (toBeRetreived.compareTo(AllAvailableHashedPorts.get(1)) >= 0 && toBeRetreived.compareTo(AllAvailableHashedPorts.get(2)) <= 0) {
                        try {
                            Cursor = getSuccCursorinRing(selection, returnWithoutHash(AllAvailableHashedPorts.get(2)));
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (toBeRetreived.compareTo(AllAvailableHashedPorts.get(2)) >= 0 && toBeRetreived.compareTo(AllAvailableHashedPorts.get(3)) <= 0) {
                        try {
                            Cursor = getSuccCursorinRing(selection, returnWithoutHash(AllAvailableHashedPorts.get(3)));
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (toBeRetreived.compareTo(AllAvailableHashedPorts.get(3)) >= 0 && toBeRetreived.compareTo(AllAvailableHashedPorts.get(4)) <= 0) {
                        try {
                            Cursor = getSuccCursorinRing(selection, returnWithoutHash(AllAvailableHashedPorts.get(4)));
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        return Cursor;
    }




    public MatrixCursor returnFromThisNode(String selection) {

            MatrixCursor Cursor = new MatrixCursor(new String[]{"key", "value"});

            try {
                File file = new File(globalContext.getFilesDir().getAbsolutePath() + File.separator + selection);
                Log.v("file location", file.toString());

                FileInputStream stream = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String line = br.readLine();

                //String[] onlyValue  = line.split("-");

                Log.v("---- line ---", line);
                br.close();
                Cursor.addRow(new String[]{selection, line});
            } catch (Exception e) {
                Log.v(TAG, "file read failed");
            }

            return Cursor;
    }



    public MatrixCursor getAllCursorinRing(){

            try {
                new RetreiveAll().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return StarCursor;
    }





    public MatrixCursor getSuccCursorinRing(String selection, String Port) throws ExecutionException, InterruptedException {

            MatrixCursor Cursor = new MatrixCursor(new String[]{"key", "value"});
            try{
                new ClientRetreive().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, Port, selection).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

          //added to implement qourum
            try{
                new ClientRetreive().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, nextPort(Port), selection , "1stReplica").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try{
                new ClientRetreive().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, nextPort(nextPort(Port)), selection , "2ndReplica").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

          int greatestValue = -1;

           for (String version : decideKey){
                String [] finalquery = version.split("\\|");
                String[] greaterVersion = finalquery[1].split("-");
                int versionValue = Integer.parseInt(greaterVersion[1]);

                if(versionValue > greatestValue){
                    greatestValue = versionValue;
                    MapKeyValue.put(finalquery[0], greaterVersion[0]);
                }
            }

            Log.e(TAG, "Query -----> After wait was over" + MapKeyValue.get(selection));
            Cursor.addRow(new String[]{selection, MapKeyValue.get(selection)});
            decideKey.clear();
            rWaitNormalOver = false;
            return Cursor;
    }







    //taken from PA1 and modified for 5 AVDS
    public class ClientRetreive extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

                try {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(msgs[0]));
                    // String msgToSend = msgs[0];
                    //socket.setSoTimeout(200);

                    String[] toSend = {"Retrieve", msgs[1]};
                    ObjectOutputStream sendData = new ObjectOutputStream(socket.getOutputStream());

                    Log.e(TAG, "Query -------->  Normal Retreive is this -----------" + msgs[0] + msgs[1]);
                    sendData.writeObject(toSend);

                    String[] dataReceived = {};

                    ObjectInputStream readData = new ObjectInputStream(socket.getInputStream());

                    try {
                        dataReceived = (String[]) readData.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, e.toString());
                    }

                    Log.e(TAG, "Query -------->  After Retreive is this -----------" + dataReceived[0]);

                    if (dataReceived[0]==null || dataReceived[0].isEmpty() ) {
                        Log.e(TAG, "data received empty or null");


                    }else{
                        Log.e(TAG, msgs[1]+" data received good " + dataReceived[0]);

                        decideKey.add(msgs[1]+"|"+dataReceived[0]);
                    }


                    //finally receive back the element.
                   // MapKeyValue.put(msgs[1], dataReceived[0]);

                    //toSend.flush();
                    socket.close();

                } catch (UnknownHostException e) {
                   Log.e(TAG, "ClientTask UnknownHostException");
                } catch(StreamCorruptedException e){
                    Log.e(TAG, "ClientTask socket StreamCorruptedException  -----> so what? lets call the next port");

                   // doInBackground(nextPort(msgs[0]), msgs[1]);
                }

                catch (EOFException e) {
                    Log.e(TAG, "ClientTask socket EOFException  -----> so what? lets call the next port");

                    //doInBackground(nextPort(msgs[0]) , msgs[1]);

                } catch (SocketTimeoutException e) {
                    Log.e(TAG, "ClientTask socket timout ");
                    Log.e(TAG, "ClientTask socket timout  -----> so what? lets call the next port");

                    //doInBackground(nextPort(msgs[0]) , msgs[1]);

                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException");
                    Log.e(TAG, "ClientTask socket IOException  -----> so what? lets call the next port");

                   // doInBackground(nextPort(msgs[0]) , msgs[1]);

                }


            return null;
            }
    }



    public String nextPort(String port){
        String nextPort = "";

        if (port.equals("11124")){
            nextPort = "11112";
        }
        else if(port.equals("11112")){
             nextPort = "11108";
        }
        else if(port.equals("11108")){
            nextPort = "11116";
        }
        else if(port.equals("11116")){
            nextPort = "11120";
        }
        else if(port.equals("11120")){
            nextPort = "11124";
        }

        return nextPort;
    }


    //taken from PA1 and modified for 5 AVDS
    public class RetreiveAll extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

                for (String port : AllAvailablePorts) {

                    try {
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(port));
                        // String msgToSend = msgs[0];
                        Log.e(TAG, "Query --------> Retreive All is this -----------");

                        String[] toSend = {"*"};
                        ObjectOutputStream sendData = new ObjectOutputStream(socket.getOutputStream());
                        sendData.writeObject(toSend);

                        HashMap<String, String> receivedMap = new HashMap<String, String>();

                        ObjectInputStream readData = new ObjectInputStream(socket.getInputStream());

                        try {
                            receivedMap = (HashMap<String, String>) readData.readObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, e.toString());
                        }

                        Log.e(TAG, "Query------> COntent provider recieved from one client");

                        Iterator it = receivedMap.entrySet().iterator();
                        while (it.hasNext()) {
                            HashMap.Entry pair = (HashMap.Entry) it.next();
                            System.out.println(pair.getKey() + " = " + pair.getValue());
                            StarCursor.addRow(new String[]{(String) pair.getKey(), (String) pair.getValue()});
                            it.remove(); // avoids a ConcurrentModificationException
                        }
                        //toSend.flush();
                        socket.close();

                    } catch (UnknownHostException e) {
                        Log.e(TAG, "ClientTask UnknownHostException");
                    } catch (IOException e) {
                        Log.e(TAG, "ClientTask socket IOException");
                    }

                }
                return null;
            }
        }


    //taken from PA1 and modified for 5 AVDS
    public class RetreiveAfterRecover extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

                try {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(msgs[0]));

                    // String msgToSend = msgs[0];
                    Log.e(TAG, "Recover Previous  --------> Retreive All is this -----------");

                    String[] toSend = {"*recover"};
                    ObjectOutputStream sendData = new ObjectOutputStream(socket.getOutputStream());
                    sendData.writeObject(toSend);

                    HashMap<String, String> receivedMap = new HashMap<String, String>();

                    ObjectInputStream readData = new ObjectInputStream(socket.getInputStream());

                    try {
                        receivedMap = (HashMap<String, String>) readData.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, e.toString());
                    }

                    Log.e(TAG, "Recover Previous ------> COntent provider recieved from one client");

                    int count = 1;
                    Iterator it = receivedMap.entrySet().iterator();
                    while (it.hasNext()) {
                        HashMap.Entry pair = (HashMap.Entry) it.next();
                        System.out.println(pair.getKey() +" "+count+ " = " + pair.getValue());
                        receivedMapPrevious.put((String) pair.getKey(), (String) pair.getValue());
                        count ++;
                        //it.remove(); // avoids a ConcurrentModificationException
                    }
                    //toSend.flush();
                    socket.close();

                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException");
                }
            return null;
        }
    }

    //taken from PA1 and modified for 5 AVDS
    public class RetreiveAfterRecoverForward extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            try {
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(msgs[0]));

                // String msgToSend = msgs[0];
                Log.e(TAG, "Recover forward --------> Retreive All is this -----------");

                String[] toSend = {"*recover"};
                ObjectOutputStream sendData = new ObjectOutputStream(socket.getOutputStream());
                sendData.writeObject(toSend);

                HashMap<String, String> receivedMap = new HashMap<String, String>();


                ObjectInputStream readData = new ObjectInputStream(socket.getInputStream());

                try {
                    receivedMap = (HashMap<String, String>) readData.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }

                Log.e(TAG, "Recover forward ------> COntent provider recieved from one client");
                Iterator it = receivedMap.entrySet().iterator();
                int count = 1;

                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it.next();
                    System.out.println(pair.getKey() +" "+count+ " = " + pair.getValue());
                    receivedMapForward.put((String) pair.getKey(), (String) pair.getValue());
                    count++;
                    //it.remove(); // avoids a ConcurrentModificationException
                }
                //toSend.flush();
                socket.close();

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }
            return null;
        }
    }


    public void recoverAndRetreive(){
        Log.e(TAG , "Recover and retreive is called method");

        RecoverPrevious();
        RecoverForward();
    }

    public void RecoverPrevious(){
        ArrayList<String> previousPorts = new ArrayList<String>();
        ArrayList<String> HashedPreviousPorts = new ArrayList<String>();

        int count = AllAvailableHashedPorts.size();

        for (int i = 0; i < AllAvailableHashedPorts.size(); i++) {
            if (MyHashedPort.equals(AllAvailableHashedPorts.get(i))) {
                if ((AllAvailableHashedPorts.size() - i) <= 3) {
                    previousPorts.add(returnWithoutHash(AllAvailableHashedPorts.get(i-1)));
                    previousPorts.add(returnWithoutHash(AllAvailableHashedPorts.get(i-2)));
                } else if ((AllAvailableHashedPorts.size() - i) == 4) {
                    previousPorts.add(returnWithoutHash(AllAvailableHashedPorts.get(count -1)));
                    previousPorts.add(returnWithoutHash(AllAvailableHashedPorts.get(0)));
                } else if ((AllAvailableHashedPorts.size() - i) == 5) {
                    previousPorts.add(returnWithoutHash(AllAvailableHashedPorts.get(count -1)));
                    previousPorts.add(returnWithoutHash(AllAvailableHashedPorts.get(count -2)));
                }
            }
        }

        Log.e(TAG , "count of previous port is" + previousPorts.size());

        //fetch the data from previous two ports
        for (String singlePort : previousPorts) {
            try {
                new RetreiveAfterRecover().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, singlePort).get();
                Integer a = Integer.parseInt(singlePort);
                a = a / 2;
                HashedPreviousPorts.add(getHashValue(a.toString()));

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

            for (int i = 0; i < AllAvailableHashedPorts.size(); i++) {
                if (MyHashedPort.equals(AllAvailableHashedPorts.get(i))) {
                    if ((AllAvailableHashedPorts.size() - i) == 1) {
                        HashedPreviousPorts.add(AllAvailableHashedPorts.get(1));
                    }
                    else if ((AllAvailableHashedPorts.size() - i) == 2) {
                        HashedPreviousPorts.add(AllAvailableHashedPorts.get(0));
                    }
                    else if ((AllAvailableHashedPorts.size() - i) == 3) {
                        HashedPreviousPorts.add(AllAvailableHashedPorts.get(4));
                    } else if ((AllAvailableHashedPorts.size() - i) == 4) {
                        HashedPreviousPorts.add(AllAvailableHashedPorts.get(3));
                    } else if ((AllAvailableHashedPorts.size() - i) == 5) {
                        HashedPreviousPorts.add(AllAvailableHashedPorts.get(2));
                    }
                }
            }

            Collections.sort(HashedPreviousPorts, new Comparator<String>() {
                @Override
                public int compare(String fruit2, String fruit1) {
                    return fruit2.compareTo(fruit1);
                }
            });


             for (String port : HashedPreviousPorts){
                 Log.e(TAG , "range -----------> "+ Integer.parseInt(returnWithoutHash(port))/2);
             }


                Iterator it = receivedMapPrevious.entrySet().iterator();
                while (it.hasNext()) {
                    Log.e(TAG , "I was called inside the hasmap Iterator");
                    HashMap.Entry pair = (HashMap.Entry) it.next();
                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    Log.e(TAG, "Key cursor ---->" + pair.getKey());
                    Log.e(TAG, "Value cursor ---->" + pair.getValue());

                    String Key = (String) pair.getKey();
                    String Value = (String) pair.getValue();
                    filterOutPreviousValues(Key, Value ,HashedPreviousPorts );


                    it.remove(); // avoids a ConcurrentModificationException
                }

             receivedMapPrevious.clear();


        }


    public void RecoverForward(){

        ArrayList<String> forwardPort = new ArrayList<String>();
        int count = AllAvailableHashedPorts.size();

        for (int i = 0; i < AllAvailableHashedPorts.size(); i++) {
            if (MyHashedPort.equals(AllAvailableHashedPorts.get(i))) {
                if ((AllAvailableHashedPorts.size() - i) > 2) {
                    forwardPort.add(returnWithoutHash(AllAvailableHashedPorts.get(i + 1)));
                    forwardPort.add(returnWithoutHash(AllAvailableHashedPorts.get(i + 2)));
                } else if ((AllAvailableHashedPorts.size() - i) == 2) {
                    forwardPort.add(returnWithoutHash(AllAvailableHashedPorts.get(count - 1)));
                    forwardPort.add(returnWithoutHash(AllAvailableHashedPorts.get(0)));
                } else if ((AllAvailableHashedPorts.size() - i) == 1) {
                    forwardPort.add(returnWithoutHash(AllAvailableHashedPorts.get(0)));
                    forwardPort.add(returnWithoutHash(AllAvailableHashedPorts.get(1)));
                }
            }
        }

        for (String SendingPort : forwardPort) {
            Log.e(TAG, "Replicate keys sending to port");
            try {
                new RetreiveAfterRecoverForward().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, SendingPort).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }




        Iterator it = receivedMapForward.entrySet().iterator();
        while (it.hasNext()) {
            Log.e(TAG , "I was called inside the hasmap Iterator");

            HashMap.Entry pair = (HashMap.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            Log.e(TAG, "Key cursor ---->" + pair.getKey());
            Log.e(TAG, "Value cursor ---->" + pair.getValue());

            String Key = (String) pair.getKey();
            String Value = (String) pair.getValue();
            filterOutForwardValues(Key, Value);

            it.remove(); // avoids a ConcurrentModificationException
        }
        receivedMapForward.clear();
    }




    public void filterOutPreviousValues(String Key , String Value , ArrayList<String> HashedPreviousPorts){

        String getHashedKey = getHashValue(Key);
        Integer a  = (Integer.parseInt(returnWithoutHash(MyHashedPort))/2);
        String MywithoutHashPort  = a.toString();

       if (MywithoutHashPort.equals("5554")){
           if((getHashedKey.compareTo(getHashValue("5560")) >= 0 || getHashedKey.compareTo(getHashValue("5562")) <=0 )){
               Log.e(TAG, "Selected values are inserted Previous " + Key);
               insertInThisNode(Key, Value);
           }

            if(getHashedKey.compareTo(getHashValue("5562")) >= 0 && getHashedKey.compareTo(getHashValue("5556")) <=0 ){
               Log.e(TAG, "Selected values are inserted Previous " + Key);
               insertInThisNode(Key, Value);
             }

           }


        else if (MywithoutHashPort.equals("5556")){

           if((getHashedKey.compareTo(getHashValue("5560")) >= 0 || getHashedKey.compareTo(getHashValue("5562")) <=0 )){
               Log.e(TAG, "Selected values are inserted Previous " + Key);
               insertInThisNode(Key, Value);
           }

           if(getHashedKey.compareTo(getHashValue("5558")) >= 0 && getHashedKey.compareTo(getHashValue("5560")) <=0 ){
               Log.e(TAG, "Selected values are inserted Previous " + Key);
               insertInThisNode(Key, Value);
           }
       }

        else if (MywithoutHashPort.equals("5558")){
           if((getHashedKey.compareTo(getHashValue("5562")) >= 0 && getHashedKey.compareTo(getHashValue("5554")) <=0 )){
               Log.e(TAG, "Selected values are inserted Previous " + Key);
               insertInThisNode(Key, Value);
           }

        }
        else if (MywithoutHashPort.equals("5560")){
           if((getHashedKey.compareTo(getHashValue("5556")) >= 0 && getHashedKey.compareTo(getHashValue("5558")) <=0 )){
               Log.e(TAG, "Selected values are inserted Previous " + Key);
               insertInThisNode(Key, Value);
           }

        }
        else if (MywithoutHashPort.equals("5562")){
           if((getHashedKey.compareTo(getHashValue("5554"))) >= 0 && getHashedKey.compareTo(getHashValue("5560")) <=0 ){
               Log.e(TAG, "Selected values are inserted Previous " + Key);
               insertInThisNode(Key, Value);
           }

        }

    }

    public void filterOutForwardValues(String Key , String Value){

        String getHashedKey = getHashValue(Key);
        Integer a  = (Integer.parseInt(returnWithoutHash(MyHashedPort))/2);
        String MywithoutHashPort  = a.toString();

        if (MywithoutHashPort.equals("5554")){
            if((getHashedKey.compareTo(getHashValue("5556"))>=0 && getHashedKey.compareTo(getHashValue("5554"))<=0)){
                Log.e(TAG , "Selected values are inserted Forward " + Key);

                insertInThisNode(Key,Value);
            }
        }
        else if (MywithoutHashPort.equals("5556")){

            if((getHashedKey.compareTo(getHashValue("5562"))>=0 && getHashedKey.compareTo(getHashValue("5556"))<=0)){
                Log.e(TAG , "Selected values are inserted Forward " + Key);

                insertInThisNode(Key,Value);
            }
        }
        else if (MywithoutHashPort.equals("5558")){
            if((getHashedKey.compareTo(getHashValue("5554"))>=0 && getHashedKey.compareTo(getHashValue("5558"))<=0)){
                Log.e(TAG , "Selected values are inserted Forward " + Key);

                insertInThisNode(Key,Value);
            }

        }
        else if (MywithoutHashPort.equals("5560")){
            if((getHashedKey.compareTo(getHashValue("5558"))>=0 && getHashedKey.compareTo(getHashValue("5560"))<=0)){
                Log.e(TAG , "Selected values are inserted Forward " + Key);

                insertInThisNode(Key,Value);
            }

        }
        else if (MywithoutHashPort.equals("5562")){
            if((getHashedKey.compareTo(getHashValue("5560"))>=0 || getHashedKey.compareTo(getHashValue("5562"))<=0)){
                Log.e(TAG , "Selected values are inserted Forward " + Key);

                insertInThisNode(Key,Value);
            }

        }


    }





}
