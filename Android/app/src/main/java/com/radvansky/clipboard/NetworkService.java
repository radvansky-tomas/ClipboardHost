package com.radvansky.clipboard;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.common.io.ByteStreams;
import com.koushikdutta.async.AsyncNetworkSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Map;

import static com.radvansky.clipboard.Helpers.getLocalIpAddress;

/**
 * Created by tomasradvansky on 08/10/2016.
 */

public class NetworkService extends Service {
    public static final String LOGTAG = NetworkService.class.getName();

    public enum MessageType {
        NORMAL(1, "e265o00lgI"),
        PASTE(2, "0BrvGy1AFC"),
        ENTER(3, "1FgqIn5IKE");

        public int code;
        public String name;

        private MessageType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static MessageType fromInt(int code) {
            switch (code) {
                case 1:
                    return NORMAL;
                case 2:
                    return PASTE;
                case 3:
                    return ENTER;
            }

            // we had some exception handling for this
            // as the contract for these was between 2 independent applications
            // liable to change between versions (mostly adding new stuff)
            // but keeping it simple here.
            return null;
        }
    }

    //File Transfer
    private Long fileSize;
    private FileChannel channel;

    //Service Discovery
    public int DNS_PORT;
    private MyHTTPD server;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdServiceInfo serviceInfo;
    //TCP-IP Server
    public int SERVER_PORT;
    public boolean isUSB = false;
    private TCPStatusListener mListener;
    private AsyncServer asyncServer;
    private AsyncNetworkSocket asyncClient;
    // Binder given to clients
    private final IBinder mBinder = new NetworkService.LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        NetworkService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NetworkService.this;
        }
    }

    @Override
    public void onCreate() {
        asyncServer = new AsyncServer();
        SERVER_PORT = Helpers.getAvailablePort();
        asyncServer.listen(null, SERVER_PORT, listenCallback);
    }

    private ListenCallback listenCallback = new ListenCallback() {
        @Override
        public void onAccepted(AsyncSocket socket) {
            // this example service shows only a single server <-> client communication
            if (asyncClient != null) {
                asyncClient.close();
            }
            asyncClient = (AsyncNetworkSocket) socket;
            asyncClient.setDataCallback(new DataCallback() {
                @Override
                public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                    if ((channel == null) || (fileSize == null))
                    {
                        String msg = bb.readString();
                        Log.i(LOGTAG, "Data received: " + msg);
                    }
                }
            });
            asyncClient.setClosedCallback(new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    asyncClient = null;
                    Log.i(LOGTAG, "Client socket closed");
                    if (mListener != null) {
                        mListener.TCPStatusChanged(false);
                    }
                }
            });

            Log.i(LOGTAG, "Client socket connected");
            if (mListener != null) {
                mListener.TCPStatusChanged(true);
            }
        }

        @Override
        public void onListening(AsyncServerSocket socket) {
            Log.i("CLIENT-PORT", "" + socket.getLocalPort());
            DNS_PORT = Helpers.getAvailablePort();
            if (mListener != null) {
                mListener.TCPStatusChanged(true);
            }
        }

        @Override
        public void onCompleted(Exception ex) {
            Log.i(LOGTAG, "Server socket closed");
            if (mListener != null) {
                mListener.TCPStatusChanged(false);
            }
        }
    };

    public void restartConnection()
    {
     asyncClient.close();
        asyncServer.listen(null, SERVER_PORT, listenCallback);
    }


    // call this method to send data to the client socket
    public void sendData(MessageType type, String message) {
        try {
            String composedMessage = type.name + message;
            asyncClient.write(new ByteBufferList(composedMessage.getBytes(Charset.forName("UTF-8"))));
            Log.i(LOGTAG, "Data sent: " + composedMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setmListener(TCPStatusListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (asyncServer.isRunning()) {
            asyncServer.stop();
        }
        stopServer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startServer() {
        if (server != null) {
            if (server.isAlive()) {
                return;
            }
        }
        // create the registration listener who will be the callback object for service registration
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                String mServiceName = NsdServiceInfo.getServiceName();
                Log.d(LOGTAG, "Registered service. Actual name used: " + mServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.d(LOGTAG, "Failed to register service");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d(LOGTAG, "Unregistered service");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(LOGTAG, "Service unregistration failed");
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };

        // run the server
        try {
            server = new MyHTTPD();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // after server is started, register the bonjour service
        serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("jktest_androidprovider");
        serviceInfo.setServiceType("_jktest._tcp.");
        serviceInfo.setPort(SERVER_PORT);

        NsdManager mNsdManager = (NsdManager) this.getApplicationContext().getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

        // Report the address to UI
        Log.d(LOGTAG, "Now listening on http://" + getLocalIpAddress() + ":" + Integer.toString(DNS_PORT) + "/");
    }

    public void stopServer() {
        if (server != null) {
            if (server.isAlive()) {
                // unregister the service
                NsdManager mNsdManager = (NsdManager) this.getApplicationContext().getSystemService(Context.NSD_SERVICE);
                mNsdManager.unregisterService(mRegistrationListener);
                server.stop();
            }
        }
    }

    private class MyHTTPD extends NanoHTTPD {
        public MyHTTPD() throws IOException {
            super(DNS_PORT);
        }

        @Override
        public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms,
                              Map<String, String> files) {

            final String html = "Clipboard DNS service. Running at http://" + getLocalIpAddress() + ":" + DNS_PORT;
            return new NanoHTTPD.Response(html);
        }
    }
}