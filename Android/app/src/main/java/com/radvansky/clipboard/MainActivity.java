package com.radvansky.clipboard;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TCPStatusListener {
    NetworkService mNetworkService;
    private Handler handler = new Handler();
    private boolean isServiceOnline = false;
    private static final String TAG = "MainActivity";
    private MenuItem statusMenu;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // unbindService(mConnection);
    }
    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            mNetworkService.setmListener(null);
            mNetworkService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            NetworkService.LocalBinder mLocalBinder = (NetworkService.LocalBinder) service;
            mNetworkService = mLocalBinder.getService();
            Intent currentIntent = getIntent();
            int portValue = currentIntent.getIntExtra("hostPort", -1);
            if (portValue == -1) {
                mNetworkService.startServer();
            }
            mNetworkService.setmListener(MainActivity.this);
        }
    };

    @Override
    public void TCPStatusChanged(final boolean isOnline) {
        isServiceOnline = isOnline;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isServiceOnline) {
                    statusMenu.setIcon(android.R.drawable.presence_online);
                } else {
                    statusMenu.setIcon(android.R.drawable.presence_offline);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceOnline) {
                    mNetworkService.sendData(new MyClipboardManager().readFromClipboard(MainActivity.this));
                    Toast.makeText(MainActivity.this, "Clipboard data has been sent...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Host is not connected!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        statusMenu = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        Intent mIntent = new Intent(this, NetworkService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        if (mNetworkService!=null) {
            Intent currentIntent = getIntent();
            int portValue = currentIntent.getIntExtra("hostPort", -1);
            if (portValue == -1) {
                mNetworkService.startServer();
            } else {
                mNetworkService.stopServer();
            }
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }
}
