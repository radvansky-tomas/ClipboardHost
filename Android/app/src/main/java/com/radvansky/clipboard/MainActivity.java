package com.radvansky.clipboard;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.koushikdutta.async.callback.CompletedCallback;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TCPStatusListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    NetworkService mNetworkService;
    private Handler handler = new Handler();
    private boolean isServiceOnline = false;
    private static final String TAG = "MainActivity";
    private MenuItem statusMenu;
    private Menu filesMenu;

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
        Log.d(TAG, "Status changed:" + isOnline);
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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Check online status
                filesMenu.clear();
                List<File> currentFiles = Helpers.getListFiles(MainActivity.this.getFilesDir());
                if (currentFiles.size() == 0) {
                    filesMenu.add("");
                } else {
                    for (File entry : currentFiles) {
                        filesMenu.add(entry.getName());
                    }
                }
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get menu from navigationView
        Menu menu = navigationView.getMenu();
        filesMenu = menu.getItem(1).getSubMenu();

        Button fab_view = (Button) findViewById(R.id.viewBtn);
        fab_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, new MyClipboardManager().readFromClipboard(MainActivity.this), Toast.LENGTH_SHORT).show();
            }
        });

        Button fab_send = (Button) findViewById(R.id.sendBtn);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceOnline) {
                    mNetworkService.sendData(NetworkService.MessageType.NORMAL, new MyClipboardManager().readFromClipboard(MainActivity.this));
                    Toast.makeText(MainActivity.this, "Clipboard data has been sent...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Host is not connected!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button fab_paste = (Button) findViewById(R.id.pasteBtn);
        fab_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceOnline) {
                    mNetworkService.sendData(NetworkService.MessageType.PASTE, new MyClipboardManager().readFromClipboard(MainActivity.this));
                    Toast.makeText(MainActivity.this, "Clipboard data has been pasted...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Host is not connected!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (id == R.id.nav_new_file) {
            //Create new file
            fragment.createNewFile();
        } else {
            //Open file
            fragment.openFile(item.getTitle().toString());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        statusMenu = menu.findItem(R.id.action_status);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        Intent mIntent = new Intent(this, NetworkService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        if (mNetworkService != null) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                        }
                    }
                }

            } else {
                final Uri uri = data.getData();
                // Do something with the URI
                final MaterialDialog dialog =  new MaterialDialog.Builder(this)
                        .title("File Upload")
                        .content("Uploading...")
                        .progress(true, 0)
                        .show();
                mNetworkService.sendFile(uri.getPath(), new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        dialog.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "File: '" + uri.getLastPathSegment() + "' + has been sent", Toast.LENGTH_LONG).show();
                            }
                        });
                      }
                });
            }
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
                final Uri uri = data.getData();
                // Do something with the URI
                Hawk.put("DefaultPath",uri.getPath());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "New Default Directory: '" + uri.getPath() + "'", Toast.LENGTH_LONG).show();
                    }
                });
        }
    }
}

