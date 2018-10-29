package auriau.vincent.hcs_huawei;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener, Response.Listener<String>, Response.ErrorListener{

    private SensorManager sensorManager;
    private TextView txt;
    private TextView txt2;
    private boolean started = false;
    public Sensor Gyro;
    public Sensor accelerometer;
    private Button launch;

    private Socket socket;

    public String doc = "0";

    private static final int SERVERPORT = 8080;
    private static final String SERVER_IP = "90.84.44.79";

    public double acc_x;
    public double acc_y;
    public double acc_z;
    public double gyro_x;
    public double gyro_y;
    public double gyro_z;

    public int loop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        txt = (TextView)findViewById(R.id.textviou);
        txt2 = (TextView)findViewById(R.id.textviou2);
        launch = (Button)findViewById(R.id.launch);

        launch.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            sensorManager.unregisterListener(this, Gyro);
            sensorManager.unregisterListener(this, accelerometer);
        }
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.main, menu);
    //    return true;
    //}

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if ((started)&&((event.sensor.getType() == Sensor.TYPE_GYROSCOPE))){
            gyro_x = event.values[0];
            gyro_y = event.values[1];
            gyro_z = event.values[2];

        }

        if ((started)&&((event.sensor.getType() == Sensor.TYPE_ACCELEROMETER))){
            acc_x = event.values[0];
            acc_y = event.values[1];
            acc_z = event.values[2];


            //txt.setText("0" + force);
        }

        /*String url ="http://httpbin.org/ip";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        txt.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txt.setText("Erreur");
            }
        });

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                // Here goes the new timeout 3 minutes
                return 3 * 60 * 1000;
            }

            @Override
            public int getCurrentRetryCount() {
                // The max number of attempts
                return 5;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        //Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        //cal.set(currentDate.get(Calendar.YEAR), month, currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        //long timesince1970 = cal.getTime().getTime();*/
        long millis = System.currentTimeMillis();
        if(millis%10==5){
            //txt.setText("");
        }
        if(millis%10==0){
            new Thread(new ClientThread()).start();
            //txt.setText("5");
            //RequestQueue queue = Volley.newRequestQueue(this);
            //queue.add(request);

            RelativeLayout layout = (RelativeLayout)findViewById(R.id.Rel );
            ImageView imgVw = (ImageView)findViewById(R.id.ImgVw);

            //Log.e("doc", doc);

            if(doc.equals("1")){
                imgVw.setBackgroundResource(R.drawable.sit_2);
            }if(doc.equals("2")){
                imgVw.setBackgroundResource(R.drawable.walk_bl);
            }if(doc.equals("3")){
                imgVw.setBackgroundResource(R.drawable.run_2);
            }if(doc.equals("4")){
                imgVw.setBackgroundResource(R.drawable.bicycle_bl);
            }

            layout.setGravity(Gravity.CENTER);
        }
        //RequestQueue queue = Volley.newRequestQueue(this);
        //queue.add(request);


    }

    @Override
    public void onClick(View v) {
        started = true;
        Gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, Gyro, SensorManager.SENSOR_DELAY_NORMAL);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onResponse(String response) {
        txt.setText(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        txt.setText("Erreur");
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAddr, SERVERPORT);
                OutputStream out = socket.getOutputStream();
                PrintWriter output = new PrintWriter(out);

                //Log.e("marche", "marche");

                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRENCH);

                String s_x1 = ""+formatter.format(gyro_x);
                s_x1 = s_x1 + "000000";
                s_x1 = s_x1.substring(0,5);
                String s_x2 = ""+formatter.format(acc_x);
                s_x2 = s_x2 + "000000";
                s_x2 = s_x2.substring(0,5);
                String s_y1 = ""+formatter.format(gyro_y);
                s_y1 = s_y1 + "000000";
                s_y1 = s_y1.substring(0,5);
                String s_y2 = ""+formatter.format(acc_y);
                s_y2 = s_y2 + "000000";
                s_y2 = s_y2.substring(0,5);
                String s_z1 = ""+formatter.format(gyro_z);
                s_z1 = s_z1 + "000000";
                s_z1 = s_z1.substring(0,5);
                String s_z2 = ""+formatter.format(acc_z);
                s_z2 = s_z2 + "000000";
                s_z2 = s_z2.substring(0,5);

                //s_x1 = s_x1.substring(0,8);

                output.print(s_x2);
                output.print(s_y2);
                output.print(s_z2);
                output.print(s_x1);
                output.print(s_y1);
                output.print(s_z1);

                output.flush();

                loop = loop + 1;

                if(loop>10){
                    //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //String response = in.readLine();
                    //Log.e("ee", response);
                    //txt2.setText(response);
                    //in.close();

                    InputStream is = socket.getInputStream();
                    byte[] bbb = new byte[1];
                    while (true) {
                        // Read next message.
                        int bytesToRead = is.read(bbb);

                        // handle message...
                        // If you need to stop communication use 'break' to exit loop;
                        if (bytesToRead!=-1){
                            //Log.e("ee", bytesToRead+"");
                            final String doc=new String(bbb, "ISO-8859-1");
                            //Log.e("Essai", doc);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.Rel);
                                    ImageView imgVw = (ImageView) findViewById(R.id.ImgVw);

                                    //Log.e("doc", doc);

                                    if (doc.equals("1")) {
                                        imgVw.setBackgroundResource(R.drawable.sit_2);
                                        txt.setText("STILL");
                                    }
                                    if (doc.equals("2")) {
                                        imgVw.setBackgroundResource(R.drawable.walk_bl);
                                        txt.setText("WALK");
                                    }
                                    if (doc.equals("3")) {
                                        imgVw.setBackgroundResource(R.drawable.run_2);
                                        txt.setText("RUN");
                                    }
                                    if (doc.equals("4")) {
                                        imgVw.setBackgroundResource(R.drawable.bicycle_bl);
                                        txt.setText("BIKE");
                                    }

                                    layout.setGravity(Gravity.CENTER);
                                    txt.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                                }
                            });

                            break;
                        }
                    }

                    is.close();

                }

                output.close();
                socket.close();



            } catch (UnknownHostException e1){
                e1.printStackTrace();
                //Log.e("e1", "e1");
            } catch (IOException e1) {
                e1.printStackTrace();
                //Log.e("e2", "e2");
            }
        }
    }
}
