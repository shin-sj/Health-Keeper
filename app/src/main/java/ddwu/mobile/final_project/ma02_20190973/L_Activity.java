package ddwu.mobile.final_project.ma02_20190973;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class L_Activity extends Activity{
    public static final String TAG = "L_Activity";

    EditText editSearch;
    ListView lvList;
    String apiAddress;
    String query;
    LocalAdapter adapter;
    ArrayList<NaverLocalDto> resultList = null;
    NaverLocalXmlParser parser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_search);

        editSearch = findViewById(R.id.etLsearch);

        lvList = findViewById(R.id.lvLocal);

        //Local
        resultList = new ArrayList();
        adapter = new LocalAdapter(this, R.layout.local_list, resultList);
        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.api_url2);
        parser = new NaverLocalXmlParser();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLsearch:
                query = editSearch.getText().toString();
                try {
                    new NaverAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class NaverAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(L_Activity.this, "Wait", "Downloading...");
        }


        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) return "Error!";
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);

            resultList = parser.parse(result);      // ?????? ??????

            adapter.setList(resultList);    // Adapter ??? ?????? ????????? ?????? ?????? ArrayList ??? ??????
            adapter.notifyDataSetChanged();

            progressDlg.dismiss();
        }

        /* ???????????? ?????? ?????? */
        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }


        /* URLConnection ??? ???????????? ???????????? ?????? ??? ??????, ?????? ??? ????????? InputStream ??????
         * ??????????????? ?????? - ClientID, ClientSeceret ?????? strings.xml ?????? ?????????*/
        private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {

            // ??????????????? ????????? ??? ????????? ????????? ?????? URL ??????
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("X-Naver-Client-Id", getResources().getString(R.string.client_id));
            conn.setRequestProperty("X-Naver-Client-Secret", getResources().getString(R.string.client_secret));

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + conn.getResponseCode());
            }

            return conn.getInputStream();
        }


        /* InputStream??? ???????????? ???????????? ?????? ??? ?????? */
        protected String readStreamToString(InputStream stream){
            StringBuilder result = new StringBuilder();

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String readLine = bufferedReader.readLine();

                while (readLine != null) {
                    result.append(readLine + "\n");
                    readLine = bufferedReader.readLine();
                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }


        /* ??????(address)??? ???????????? ????????? ???????????? ????????? ??? ?????? */
        protected String downloadContents(String address) {
            HttpURLConnection conn = null;
            InputStream stream = null;
            String result = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection)url.openConnection();
                stream = getNetworkConnection(conn);
                result = readStreamToString(stream);
                if (stream != null) stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }

            return result;
        }
    }
}
