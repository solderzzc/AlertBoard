package ru.sersh_perm.alertboard;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Implementation of a very basic HTTP server. The contents are loaded from the assets folder. This
 * server handles one request at a time. It only supports GET method.
 */
public class SimpleWebServer implements Runnable {

    public static MainActivity context = null;
    private AppCompatActivity mApp = null;
    private RelativeLayout mLayout = null;

    private static final String TAG = "SimpleInfaredRESTAPI";

    /**
     * The port number we listen to
     */
    private final int mPort;

    /**
     * True if the server is running.
     */
    private boolean mIsRunning;

    /**
     * The {@link java.net.ServerSocket} that we listen to.
     */
    private ServerSocket mServerSocket;

    /**
     * WebServer constructor.
     */
    public SimpleWebServer(int port, RelativeLayout layout, AppCompatActivity app) {
        mPort = port;
        mApp = app;
        mLayout = layout;
    }

    /**
     * This method starts the web server listening to the specified port.
     */
    public void start() {
        mIsRunning = true;
        new Thread(this).start();
    }

    /**
     * This method stops the web server
     */
    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    public int getPort() {
        return mPort;
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(mPort);
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                handle(socket);
                socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        }
    }

    private void handle(Socket socket) throws IOException {
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            String route = null;

            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while (!TextUtils.isEmpty(line = reader.readLine())) {
                if (line.startsWith("GET /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                }
            }

            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());

            // Prepare the content to send.
            if (null == route) {
                writeServerError(output);
                return;
            }
            byte[] bytes = loadContent(route);
            if (null == bytes) {
                writeServerError(output);
                return;
            }

            // Send out the content.
            output.println("HTTP/1.0 200 OK");
            output.println("Content-Type: " + detectMimeType(route));
            output.println("Content-Length: " + bytes.length);
            output.println();
            output.write(bytes);
            output.flush();
        } finally {
            if (null != output) {
                output.close();
            }
            if (null != reader) {
                reader.close();
            }
        }
    }

    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.flush();
    }

    protected int[] dec2SamsungIRDec(double[] irDec) {
        int[] pattern = new int[irDec.length];
        for (int i = 0; i < irDec.length; i++) {
            irDec[i] = irDec[i] * 26.3;
            pattern[i] += (int) Math.rint(irDec[i]);
        }
        return pattern;
    }

    private byte[] loadContent(String fileName) throws IOException {
        InputStream input = null;
        final ConsumerIrManager mCIR;
        mCIR = (ConsumerIrManager) SimpleWebServer.context.getSystemService(Context.CONSUMER_IR_SERVICE);
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (fileName.equals("") || fileName.equals("/")) {
                input = new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8));
            } else if (fileName.toLowerCase().contains("red/".toLowerCase()) || fileName.toLowerCase().contains("red".toLowerCase())) {
                try {
                    input = new ByteArrayInputStream("red".getBytes(StandardCharsets.UTF_8));

                    mLayout.setBackgroundColor(mApp.getResources().getColor(R.color.colorRed));

                } catch (Exception ex) {
                    Log.d(TAG, "FAILED");
                    input = new ByteArrayInputStream("red failed".getBytes(StandardCharsets.UTF_8));
                }
            } else if (fileName.toLowerCase().contains("yellow/".toLowerCase()) || fileName.toLowerCase().contains("yellow".toLowerCase())) {
                try {
                    input = new ByteArrayInputStream("yellow".getBytes(StandardCharsets.UTF_8));

                    mLayout.setBackgroundColor(mApp.getResources().getColor(R.color.colorYellow));

                } catch (Exception ex) {
                    Log.d(TAG, "FAILED");
                    input = new ByteArrayInputStream("yellow cmd failed".getBytes(StandardCharsets.UTF_8));
                }
            } else if (fileName.toLowerCase().contains("green/".toLowerCase()) || fileName.toLowerCase().contains("green".toLowerCase())) {
                try {
                    input = new ByteArrayInputStream("green".getBytes(StandardCharsets.UTF_8));

                    mLayout.setBackgroundColor(mApp.getResources().getColor(R.color.colorGreen));

                } catch (Exception ex) {
                    Log.d(TAG, "FAILED");
                    input = new ByteArrayInputStream("green cmd failed".getBytes(StandardCharsets.UTF_8));
                }
            } else if (fileName.toLowerCase().contains("white/".toLowerCase()) || fileName.toLowerCase().contains("white".toLowerCase())) {
                try {
                    input = new ByteArrayInputStream("white".getBytes(StandardCharsets.UTF_8));

                    mLayout.setBackgroundColor(mApp.getResources().getColor(R.color.colorWhite));

                } catch (Exception ex) {
                    Log.d(TAG, "FAILED");
                    input = new ByteArrayInputStream("white cmd failed".getBytes(StandardCharsets.UTF_8));
                }
            }
            byte[] buffer = new byte[1024];
            int size;
            while (-1 != (size = input.read(buffer))) {
                output.write(buffer, 0, size);
            }
            output.flush();
            return output.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            if (null != input) {
                input.close();
            }
        }
    }

    private String detectMimeType(String fileName) {
        return null;
    }

}
