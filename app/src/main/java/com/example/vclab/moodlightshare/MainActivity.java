package com.example.vclab.moodlightshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.MainActivity_ShareButton).setOnClickListener(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // ArrayList 에 Item 객체(데이터) 넣기
        ArrayList<Item> items = new ArrayList();
        int[] test_a = {1,3};
        int[] test_b = {1,3};
        int[] test_c = {1,3};

        items.add(new Item("1", "하나",2, test_a, test_b, test_c));


        test_b[0] = 4;
        test_b[1] = 5;

        items.add(new Item("2", "둘",2, test_a, test_b, test_c));



        // LinearLayout으로 설정
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Animation Defualt 설정
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Decoration 설정
        // mRecyclerView.addItemDecoration(new RecyclerViewDecoration(this, RecyclerViewDecoration.VERTICAL_LIST));
        // Adapter 생성
        mAdapter = new RecyclerAdapter(items);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.MainActivity_ShareButton:
                // 조명 공유자 이름/
                // 조명 내용 // 뭐 Array;;;
                //

                Toast.makeText(getApplicationContext(),"공유",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
