package ru.sersh_perm.alertboard;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TrafficLightMain";

    public RelativeLayout mRelativeLayout;
    private TextView mInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout);
        mInfoTextView = (TextView) findViewById(R.id.textView);


        SimpleWebServer.context = this;
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.d(TAG, "IP is " + ip);
        SimpleWebServer webServerStart = new SimpleWebServer(8080, mRelativeLayout, this);
        webServerStart.start();
    }

    public void onClick(View view) {
        //mInfoTextView.setText("RED");
    }
}
