package com.unimib.unimibike.Util;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import com.unimib.unimibike.Util.ServerRoutes;
public class InternetService{
    public static boolean isServerReachable()
    // To check if server is reachable
    {
        boolean exists = false;

        try {
            SocketAddress sockaddr = new InetSocketAddress(ServerRoutes.IP, Integer.parseInt(ServerRoutes.PORT));
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 2000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);
            exists = true;
        } catch(IOException e) {
            // Handle exception
        }
        return exists;
    }
}
