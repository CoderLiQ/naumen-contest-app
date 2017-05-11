package liq.developers.naumencontestapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import liq.developers.naumencontestapp.Net.GetASingleComputer;
import liq.developers.naumencontestapp.Net.GetSimilarComputers;

public class CardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ll = (LinearLayout) findViewById(R.id.cardLL); // пробовал сделать отслеживание прокрутки в самый низ
                                                        // через onTouchListener

        isimageloaded = false;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarSimilar = (ProgressBar) findViewById(R.id.progressBarSimilar);


        compNameTv = (TextView) findViewById(R.id.compNameTv);
        compCompanyTv = (TextView) findViewById(R.id.compCompanyTv);
        compDescTv = (TextView) findViewById(R.id.compDescTv);
        compDescTv.setMovementMethod(new ScrollingMovementMethod());
        compImageIv = (ImageView) findViewById(R.id.compImageIv);

        sepatatorTV = (View) findViewById(R.id.separatorTv);

        lvGetSimilar = (ListView) findViewById(R.id.lvGetSimilar);
        similar = new ArrayList<>();

        similarComputers = new ArrayList<>();


        Intent intent = getIntent();

        String compId = intent.getExtras().getString("compId");


        GetDataAsync tr = new GetDataAsync();
        tr.execute(String.valueOf(compId));


        sv  = (ScrollView) findViewById(R.id.sv); //реализация загрузки похожих, при прокрутке в самый низ
        sv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!similarloaded) {
                    if (sv.getChildAt(0).getBottom() <= (sv.getHeight() + sv.getScrollY())) {
                        //scroll view is at bottom
                        //Toast.makeText(getApplicationContext(), "da", Toast.LENGTH_SHORT).show();
                        progressBarSimilar.setVisibility(View.VISIBLE);
                        mTimer2 = new Timer();
                        mSimilarTimerTask = new SimilarTimerTask();
                        mTimer2.schedule(mSimilarTimerTask, 7000);

                        GetSimilarAsync tr = new GetSimilarAsync();
                        tr.execute(String.valueOf(curComp.id));
                        updateAdapter();
                        similarloaded = true;
                        progressBarSimilar.setVisibility(View.GONE);

                    }
                }
            }
        });
        lvGetSimilar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(CardActivity.this, CardActivity.class);
                String x = String.valueOf((similarComputers.get(position).id));
                intent.putExtra("compId", x);
                startActivity(intent);

            }
            @SuppressWarnings("unused")
            public void onClick(View v){
            };
        });


    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
        if (mTimer != null) mTimer.cancel();
        if (mTimer2 != null)mTimer2.cancel();
    }

    LinearLayout ll;

    boolean similarloaded = false; //чтобы не загружать похожие каждый раз при прокрутке в самый вниз
    boolean isimageloaded;

    ProgressBar progressBar, progressBarSimilar;
    Computer curComp;
    TextView compNameTv, compCompanyTv, compDescTv;
    View sepatatorTV;
    ImageView compImageIv;
    List<Computer> similarComputers;
    ScrollView sv;

    ListView lvGetSimilar;
    ArrayList<String> similar;

    Timer mTimer, mTimer2;
    ImageTimerTask mImageTimerTask;
    SimilarTimerTask mSimilarTimerTask;


    public void updateAdapter()
    {
        similar.clear();
        lvGetSimilar.setAdapter(null);

        for(Computer c: similarComputers)
        {
            similar.add(c.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, similar);
        lvGetSimilar.setAdapter(adapter);

    }

    public void expandTextOnClick(View view) {
        compDescTv.setMaxLines(compDescTv.getLineCount());
        //compDescTv.setMaxLines(40);
        view.setVisibility(View.GONE);
    }


    class ImageTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (!isimageloaded)
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Image loading error (Timed-out)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    class SimilarTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (!similarloaded)
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Similar items list loading error (Timed-out)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private class GetDataAsync extends AsyncTask<String, Void, String> { // Новый поток для работы с сетью

        @Override
        protected String doInBackground(String[] params) {
            try {
                curComp = GetASingleComputer.getComp(Integer.parseInt(params[0]));



            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            //return s;
            return "";
        }

        @Override
        protected void onPostExecute(String message) {

            checkPropertyAvailability(curComp.name, compNameTv, "Name");
            checkPropertyAvailability(curComp.company.name, compCompanyTv, "Company");
            checkPropertyAvailability(curComp.description, compDescTv, "Description");

            if(curComp.imageUrl != "")
            {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                mTimer = new Timer();
                mImageTimerTask = new ImageTimerTask();
                mTimer.schedule(mImageTimerTask, 10000);
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {

                        final Bitmap bitmap = BitmapFactory.decodeStream(new URL(curComp.imageUrl).openStream());
                        compImageIv.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(ProgressBar.GONE);
                                compImageIv.setImageBitmap(bitmap);
                                isimageloaded = true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ;
            };
            t.start();
            }
        }



        void checkPropertyAvailability(String s, TextView v, String text)
        {
            if (s != "")
            {
                v.setText(Html.fromHtml("<b>" + text + ": </b> " + s));
                if (text == "Description") {
                    compDescTv.setMaxLines(2);
                }
            }
            else {
                v.setVisibility(View.GONE);
            }
        }
    }

    private class GetSimilarAsync extends AsyncTask<String, Void, String> { // Новый поток для работы с сетью

        @Override
        protected String doInBackground(String[] params) {
            try {
                similarComputers = GetSimilarComputers.getData(Integer.parseInt(params[0]));

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String message) {
            updateAdapter();
        }
    }
}
