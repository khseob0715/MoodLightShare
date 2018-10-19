package com.example.vclab.moodlightshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vclab.moodlightshare.model.LightModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecyclerAdapter mAdapter;

    DatabaseReference mDatabase;

    String TestName = "김한섭";
    String TestDescription = "김한섭의 조명";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.MainActivity_ShareButton).setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 데이터베이스 레퍼런스.
        mDatabase = FirebaseDatabase.getInstance().getReference();


        // ArrayList 에 Item 객체(데이터) 넣기

        // 데이터 읽어 오기.

        // receipe의 자식의 수 = 공유된 총 레시피 수 .
        // 레시피의 자식의 자식에 sharePixel값을 가져와서 // 가공한 뒤에 뿌려주면..? 될려나...




        int[] test_a = {1,3};
        int[] test_b = {1,3};
        int[] test_c = {1,3};

        ArrayList<Item> items = new ArrayList();
        items.add(new Item("1", "하나",2, test_a, test_b, test_c));
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
                LightModel lightModel = new LightModel();
                lightModel.ShareUserName = TestName;
                lightModel.ShareLightDescription = TestDescription;

              //  List<Item> list = Arrays.asList(new Item("2", "둘", 2, test_a, test_b, test_c));

                //lightModel.SharePixel = list;

                String[] strs = {"1/2/24", "3/25/23", "43/42/16"};
                // 1번 조명   2번 조명  3번 조명 -------->?

                //List<String> list = Arrays.asList(new String[]{"alpha", "beta", "charlie"});
                List<String> list = Arrays.asList(strs);
                lightModel.SharePixel = list;

                mDatabase.child("recipe").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(lightModel); // 데이터 쓰기.
                // FirebaseAuth.getInstance().getCurrentUser().getUid()  userId;
                // Time Stamp로 바꿔놓을 것.


                Toast.makeText(getApplicationContext(),"공유",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
