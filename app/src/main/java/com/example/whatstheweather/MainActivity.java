package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;

    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            try{
                String result = "";
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current =(char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather ",weatherInfo);
                String message = "";

                JSONArray array = new JSONArray(weatherInfo);
                for (int i=0; i<array.length();i++){
                    JSONObject jsonObject1 = array.getJSONObject(i);
                    String main = jsonObject1.getString("main");
                    String description = jsonObject1.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message = main + ": " + description + "\r\n";
                    }
                }
                textView.setText(message);


            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Weather Not Found :( ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getWeather(View view){

        DownloadTask task = new DownloadTask();
        try{
            String encodedName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q="+encodedName+"&appid=b6907d289e10d714a6e88b30761fae22");
            InputMethodManager mgr =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Weather Not Found :( ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView2);
        editText = findViewById(R.id.editText);
    }
}
