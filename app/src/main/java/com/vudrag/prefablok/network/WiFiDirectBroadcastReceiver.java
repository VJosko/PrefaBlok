package com.vudrag.prefablok.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.vudrag.prefablok.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "WiFiDirectBroadcastReceiver";

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    MainActivity activity;

    WifiP2pManager.PeerListListener myPeerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;

        myPeerListListener = wifiP2pDeviceList -> {
            List<String> names = new ArrayList<>();
            for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                Log.d(TAG, "_____" + device.deviceName);
                names.add(device.deviceName);
            }
            activity.setPeerList(names);
            activity.setWifiDeviceList(new ArrayList<>(wifiP2pDeviceList.getDeviceList()));
        };

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                String groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();

                if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                    Log.d(TAG, "onConnectionInfoAvailable: _____ GROUP OWNER");
                    new ServerAsyncTask(activity.getBaseContext()).execute();
                } else if (wifiP2pInfo.groupFormed) {
                    Context context = activity.getBaseContext();
                    int port;
                    int len;
                    Socket socket = new Socket();
                    byte buf[] = new byte[1024];

                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                socket.bind(null);
                                socket.connect((new InetSocketAddress(groupOwnerAddress, 8888)), 500);

                                OutputStream outputStream = socket.getOutputStream();
                                byte[] message = "fafdasf".getBytes(StandardCharsets.UTF_8);
                                outputStream.write(message);
                                outputStream.close();
                            } catch (FileNotFoundException e) {
                                //catch logic
                            } catch (IOException e) {
                                //catch logic
                            }finally {
                                if (socket != null) {
                                    if (socket.isConnected()) {
                                        try {
                                            socket.close();
                                        } catch (IOException e) {
                                            //catch logic
                                        }
                                    }
                                }

                            }
                        }
                    };
                    thread.start();
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "onReceive: _____P2P enabled");
            } else {
                Log.d(TAG, "onReceive: _____P2P disabled");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
                Log.d(TAG, "onReceive: _____ WIFI_P2P_PEERS_CHANGED_ACTION");
                manager.requestPeers(channel, myPeerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "onReceive: _____ WIFI_P2P_CONNECTION_CHANGED_ACTION");
            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                manager.requestConnectionInfo(channel, connectionInfoListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

}
