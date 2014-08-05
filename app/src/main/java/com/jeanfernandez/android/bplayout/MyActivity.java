package com.jeanfernandez.android.bplayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {

    String username = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Button login = (Button) findViewById(R.id.loginButton);

        login.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                EditText usernameText = (EditText) findViewById(R.id.usernameText);
                EditText passwordText = (EditText) findViewById(R.id.passwordText);

                username = usernameText.getText().toString();
                password = passwordText.getText().toString();

                String url = "http://api.jeanfernandez.site50.net/acces.php";
                new ReadJSONFeed(MyActivity.this).execute(url);
            }
        });
    }

    private class ReadJSONFeed extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public ReadJSONFeed(MyActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("loading ...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder builder = new StringBuilder();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urls[0]);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("usuario",username));
                params.add(new BasicNameValuePair("password",password));
                httppost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        protected void onPostExecute(String result) {

            Integer status = 0;
            String name = "";

            try {
                JSONArray jsonUsuario = new JSONArray(result);
                for (int i = 0; i < jsonUsuario.length(); i++) {
                    JSONObject e = jsonUsuario.getJSONObject(i);
                    status = e.getInt("logstatus") ;
                    name = e.getString("username");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status == 1){
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Welcome "+name, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Username or password not valid", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
