package com.radvansky.clipboard;

import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by tomasradvansky on 08/10/2016.
 */

public class Helpers {
    public static int getAvailablePort()
    {
        int SERVER_PORT = 0;
        ServerSocket mServerSocket = null;
        try {
            mServerSocket = new ServerSocket(0);
            // Store the chosen port.
            SERVER_PORT = mServerSocket.getLocalPort();
            mServerSocket.close();

            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            Log.d("Helpers", "Free port is " + Integer.toString(SERVER_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SERVER_PORT;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if ((inetAddress instanceof Inet4Address) && !inetAddress.isLoopbackAddress()) {
                        // I don’t know how NanoHTTPD, bonjour etc feel about ipv6 addresses
                        // So to be on the safe side, we filter to ipv4 only
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Helpers", ex.toString());
        }
        return null;
    }
}
