package com.example.bocalist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dates = new ArrayList<>();

        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dates);

        listView.setAdapter(adapter);

        button = findViewById(R.id.button);

        button.setOnClickListener(this);

        button.setEnabled(false);
        update();
    }

    public void update() {
        adapter.clear();
        new Thread(new Runnable() {
            public void run() {
                try {
                    getInfos();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                            button.setEnabled(true);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getInfos() {
        Scanner scanner = null;
        dates.clear();
        try {
            System.out.println("Trying to get the url");
            URL url = new URL("https://www.cameroncounty.us/spacex/");
            System.out.println("Trying to open stream");
            InputStream ip = url.openStream();
            scanner = new Scanner(ip);
        } catch (MalformedURLException e) {
            System.out.println("URL wrong");
        } catch (IOException e) {
            System.out.println("IO stuff");
        }
        String content;
        while (scanner.hasNext()) {
            content = scanner.nextLine();
            String date;
            if (content.startsWith("<tr>")) {
                content = scanner.nextLine();
                if (content.contains("<td")) date = (content.split(">", 2)[1]).split("<",2)[0] + " | Date: ";
                else continue;

                content = scanner.nextLine();
                if(content.contains("><"))continue;

                date = date.concat((content.split(">", 2)[1]).split("<",2)[0] + " | Time: ");
                date = date.concat((scanner.nextLine().split(">", 2)[1]).split("<",2)[0] + " | Status: ");
                date = date.concat((scanner.nextLine().split(">", 2)[1]).split("<",2)[0]);

                System.out.println("Date: " + date);

                dates.add(date);
            }
        }
        scanner.close();
        if(dates.size()==0){
            dates.add("No closures found.");
        }
        System.out.println("Data found:" + dates.size());
    }

    public void setContent(ArrayList<String> arrayList) {
        dates = arrayList;
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            button.setEnabled(false);
            update();
        }
    }
}
