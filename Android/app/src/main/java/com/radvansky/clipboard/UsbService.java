package com.radvansky.clipboard;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.koushikdutta.async.AsyncNetworkSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;

/**
 * Created by tomasradvansky on 08/10/2016.
 */

public class UsbService extends Service
{
    public static final String LOGTAG = UsbService.class.getName();
    public static final int SERVER_PORT = 54321;
    private TCPStatusListener mListener;
    private AsyncServer asyncServer;
    private AsyncNetworkSocket asyncClient;
    // Binder given to clients
    private final IBinder mBinder = new UsbService.LocalBinder();
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        UsbService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UsbService.this;
        }
    }

    @Override
    public void onCreate() {
        asyncServer = new AsyncServer();
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
                    Log.i(LOGTAG, "Data received: " + bb.readString());
                }
            });
            asyncClient.setClosedCallback(new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    asyncClient = null;
                    Log.i(LOGTAG, "Client socket closed");
                    if (mListener!=null) {
                        mListener.TCPStatusChanged(false);
                    }
                }
            });
            Log.i(LOGTAG, "Client socket connected");
            if (mListener!=null) {
                mListener.TCPStatusChanged(true);
            }
        }

        @Override
        public void onListening(AsyncServerSocket socket) {
            Log.i(LOGTAG, "Server listening on port " + socket.getLocalPort());
            if (mListener!=null) {
                mListener.TCPStatusChanged(true);
            }
        }

        @Override
        public void onCompleted(Exception ex) {
            Log.i(LOGTAG, "Server socket closed");
            if (mListener!=null) {
                mListener.TCPStatusChanged(false);
            }
        }
    };

    // call this method to send data to the client socket
    public void sendData(String message) {
        try {
            asyncClient.write(new ByteBufferList(message.getBytes()));
            Log.i(LOGTAG, "Data sent: " + message);
        }
        catch (Exception ex)
        {
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}