package com.vudrag.prefablok;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vudrag.prefablok.network.WiFiDirectBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final Integer ACCESS_FINE_LOCATION_PERMISSION = 100;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    PeerRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView peerRecycler;

    Button search;
    Button send;

    public List<WifiP2pDevice> wifiDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        setupRecycler();
        searchForPeers();
        sendMessage();

    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void searchForPeers() {
        search = findViewById(R.id.search_button);
        search.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION);

            } else {
                discoverPeers();
            }
        });
    }

    private void sendMessage(){
        send = findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION) {
            discoverPeers();
        }
    }

    @SuppressLint("MissingPermission")
    private void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "onSuccess: _____SUCCESS");
            }

            @Override
            public void onFailure(int i) {
                Log.d("TAG", "onFailure: _____FAILURE");
            }
        });
    }

    private void setupRecycler() {
        peerRecycler = findViewById(R.id.peer_recycler);
        layoutManager = new LinearLayoutManager(this);
        adapter = new PeerRecyclerAdapter(new ArrayList<>(), getOnPeerClickListener());
        peerRecycler.setLayoutManager(layoutManager);
        peerRecycler.setAdapter(adapter);
    }

    private PeerRecyclerAdapter.OnPeerClickListener getOnPeerClickListener() {
        return position -> {
            Log.d("TAG", "getOnPeerClickListener: _____ " + position);
            connect(wifiDeviceList.get(position));
        };
    }

    public void setPeerList(List<String> names) {
        adapter.setList(names);
    }

    public void setWifiDeviceList(List<WifiP2pDevice> wifiDeviceList) {
        this.wifiDeviceList = wifiDeviceList;
    }

    @SuppressLint("MissingPermission")
    public void connect(WifiP2pDevice device) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("TAG", "onSuccess: _____ CONNECTED");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}