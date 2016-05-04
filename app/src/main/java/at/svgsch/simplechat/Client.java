package at.svgsch.simplechat;

import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Mathias on 30.04.2016.
 */
public class Client {
    private String host = "10.0.2.2";
    private int port = 3000;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected = false;
    private Handler handler;

    private int retryInterval = 3;

    public static final int CLIENT_MESSAGE = 0;
    public static final int CLIENT_CONNECTED = 1;
    public static final int CLIENT_DISCONNECTED = 2;
    public static final int CLIENT_RETRY = 3;

    private SendClientDataHandler sendClientDataHandler;

    public boolean isConnected() {
        return isConnected;
    }

    public Socket getSocket() {
        return socket;
    }

    public Client(Handler handler) {
        this.handler = handler;
    }

    public void connect() {

        new Thread() {
            @Override
            public void run() {
                while (true) {

                    try {
                        socket = new Socket(host, port);
                        out = new ObjectOutputStream(socket.getOutputStream());
                        in = new ObjectInputStream(socket.getInputStream());
                        isConnected = true;
                        ClientReadThread cThread = new ClientReadThread();
                        cThread.start();
                        break; // break out of connecting loop
                    } catch (UnknownHostException e) {
                        Log.i("Client","Could not connect to " +host + ":" +port + ", retry in " + retryInterval + " seconds");
                        handler.sendEmptyMessage(CLIENT_RETRY);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }

                    try {
                        sleep(retryInterval*1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();



    }

    private class ClientReadThread extends Thread {

        @Override
        public void run() {

            // read accept info
            try {
                String data = (String)in.readObject();
                handler.obtainMessage(CLIENT_CONNECTED,data).sendToTarget();
                out.writeObject(sendClientDataHandler.generateClientData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    String data = (String)in.readObject();
                    handler.obtainMessage(CLIENT_MESSAGE,data).sendToTarget();
                } catch (IOException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            close();

            handler.sendEmptyMessage(CLIENT_DISCONNECTED);

        }
    }
    public void close() {
        Log.i("Client","Closing");
        try {
            socket.close();
            out.close();
            in.close();
        } catch (Exception e) {
        }
        Log.i("Client","Closing complete");
    }

    public void send(String data) {
        if (!isConnected) return;
        try {
            out.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSendClientDataHandler(SendClientDataHandler handler) {
        sendClientDataHandler = handler;
    }

    public interface SendClientDataHandler {
        String generateClientData();
    }

}
