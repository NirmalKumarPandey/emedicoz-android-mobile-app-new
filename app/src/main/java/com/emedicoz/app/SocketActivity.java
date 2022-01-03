package com.emedicoz.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tech.gusavila92.websocketclient.WebSocketClient;

//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;


public class SocketActivity extends AppCompatActivity {
    public static final int SERVER_PORT = 2050;

    public static final String SERVER_IP = "3.7.22.193";
    private ClientThread clientThread;
    private Thread thread;
    private Socket socket;
    private Handler handler;
    private LinearLayout msgList;
    private int clientTextColor;
    private EditText edMessage;

    private WebSocketClient webSocketClient;
//    private WebSocketClient mWebSocketClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        setTitle("Client");
        clientTextColor = ContextCompat.getColor(this, R.color.green);
        handler = new Handler();
        msgList = findViewById(R.id.msgList);
        edMessage = findViewById(R.id.edMessage);

        //directly connect to 3.7.22.193:2050
        createWebSocketClient();
//        connectNativeWebSocket();
    }

    public TextView textView(String message, int color, Boolean value) {
        if (null == message || message.trim().isEmpty()) {
            message = "<Empty Message>";
        }
        TextView tv = new TextView(this);
        tv.setTextColor(color);
        tv.setText(message + " [" + getTime() + "]");
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        tv.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0));
        if (value) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
        return tv;
    }

    public void showMessage(final String message, final int color, final Boolean value) {
        handler.post(() -> msgList.addView(textView(message, color, value)));
    }

    public void onClick(View view) {
        //optional way to connect and received data
        if (view.getId() == R.id.connect_server) {
            msgList.removeAllViews();
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();
            return;
        }
        //optional way to send data
        if (view.getId() == R.id.send_data) {
            String clientMessage = edMessage.getText().toString().trim();
            showMessage(clientMessage, Color.BLUE, false);
            if (null != clientThread) {
                if (clientMessage.length() > 0) {
                    clientThread.sendMessage(clientMessage);
                }
                edMessage.setText("");
            }
        }
    }

    String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clientThread) {
            clientThread.sendMessage("Disconnect");
            clientThread = null;
        }
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://3.7.22.193:2050");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("Websocket", "onOpen");
            }

            @Override
            public void onTextReceived(String message) {
                showMessage(message, Color.BLACK, false);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Log.i("Websocket", "onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                Log.i("Websocket", "onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                Log.i("Websocket", "onPongReceived");
            }

            @Override
            public void onException(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(1800000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();

    }

    /* clientThread class defined to run the client connection to the socket network using the server ip and port
     * and send message */
    class ClientThread implements Runnable {

        private BufferedReader input;

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                showMessage("Connecting to Server...", clientTextColor, true);
                socket = new Socket(serverAddr, SERVER_PORT);

                if (socket.isBound() && socket.isConnected()) {
                    showMessage("Connected to Server...", clientTextColor, true);
                }


                while (!Thread.currentThread().isInterrupted()) {
                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = input.readLine();
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected...";
                        showMessage(message, Color.RED, false);
                        break;
                    }
                    showMessage("Server: " + message, clientTextColor, true);
                }

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                showMessage("Problem Connecting to server... Check your server IP and Port and try again", Color.RED, false);
                Thread.interrupted();
                e1.printStackTrace();
            } catch (NullPointerException e3) {
                showMessage("error returned", Color.RED, true);
            }

        }

        void sendMessage(final String message) {
            new Thread(() -> {
                try {
                    if (null != socket) {
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    /*private void connectNativeWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://3.7.22.193:2050");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgList.addView(textView(message, Color.BLUE, false));
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }*/
}





