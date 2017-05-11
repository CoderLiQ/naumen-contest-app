package liq.developers.naumencontestapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import liq.developers.naumencontestapp.Net.GetAllComputersOnPage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        computers = new ArrayList<>();
        computers.clear();
        arrList = new ArrayList<>();


        prevPageBtn =(Button) findViewById(R.id.prevPageBtn);
        curPageBtn =(Button) findViewById(R.id.curPageBtn);
        nextPageBtn =(Button) findViewById(R.id.nextPageBtn);

        lvMain = (ListView) findViewById(R.id.lvMain);

        getData(curPage);

        prevPageBtn.setOnClickListener(this);
        nextPageBtn.setOnClickListener(this);


        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                    Intent intent = new Intent(MainActivity.this, CardActivity.class);
                     String x = String.valueOf((computers.get(position).id));
                    intent.putExtra("compId", x);
                    startActivity(intent);

            }
            @SuppressWarnings("unused")
            public void onClick(View v){
            };
        });

    }

    ListView lvMain;
    SimpleAdapter adapter;
    ArrayList<Map<String, String>> arrList;
    List<Computer> computers;
    int curPage = 0; //текущая страница

    Button prevPageBtn, curPageBtn, nextPageBtn;

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.prevPageBtn:
                if(curPage > 0){
                curPage--;
                getData(curPage);
                }
                break;
            case R.id.nextPageBtn:
                curPage++;
                getData(curPage);
                break;
        }
    }


    public void getData(int curPage){
        GetDataAsync tr = new GetDataAsync();
        tr.execute(String.valueOf(curPage));

        updateAdapter();

    }
    public void updateAdapter()
    {
        arrList.clear();
        lvMain.setAdapter(null);
        for (Computer c: computers) {

            Map<String, String> map = new HashMap<>();
            map.put("computerName", c.name);
            map.put("companyName", c.company.name);
            arrList.add(map);
        }

        adapter = new SimpleAdapter(this, arrList, android.R.layout.simple_list_item_2,
                new String[]{"computerName", "companyName"},
                new int[]{android.R.id.text1, android.R.id.text2});
        lvMain.setAdapter(adapter);

        curPageBtn.setText("PAGE: ".concat(String.valueOf(curPage+1)));
    }

    public void test(View view) {
        GetDataAsync tr = new GetDataAsync();
        tr.execute(String.valueOf(0));
    }

    private class GetDataAsync extends AsyncTask<String, Void, String> { // Новый поток для работы с сетью

        @Override
        protected String doInBackground(String[] params) {
            try {
                computers = GetAllComputersOnPage.getData(Integer.parseInt(params[0]));

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
