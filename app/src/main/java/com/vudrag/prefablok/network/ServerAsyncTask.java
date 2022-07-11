package com.vudrag.prefablok.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAsyncTask extends AsyncTask {

    private Context context;

    public ServerAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            Log.d("TAG", "doInBackground: _____ SERVER ASYNC");
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
            InputStream inputstream = client.getInputStream();
            byte[] byteMessage = new byte[15];
            inputstream.read(byteMessage);
            String message = new String(byteMessage);
            serverSocket.close();
            return "dsad";
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
            return null;
        }

    }
}
