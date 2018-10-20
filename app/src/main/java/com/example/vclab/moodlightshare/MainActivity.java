package com.example.vclab.moodlightshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vclab.moodlightshare.model.LightModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    DatabaseReference mDatabase;

    String TestName = "김한섭";
    String TestDescription = "김한섭의 조명";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.MainActivity_ShareButton).setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerAdapter());

    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<LightModel> lightModels;

        public RecyclerAdapter() {
            lightModels = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("recipe").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 처음 넘어오는 데이터 // ArrayList 값.
                    lightModels.clear();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                        LightModel lightModel = snapshot.getValue(LightModel.class);
                        lightModels.add(lightModel);
                        Log.e("Tag", snapshot.child("ShareLightDescription").getValue(String.class).toString());
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override// 뷰 홀더 생성
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);

            return new ItemViewHolder(view);
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            // 해당 position 에 해당하는 데이터 결합
            ((ItemViewHolder)holder).NameText.setText(lightModels.get(position).ShareUserName);
            ((ItemViewHolder)holder).DescriptionText.setText(lightModels.get(position).ShareLightDescription);

            // 이벤트처리 : 생성된 List 중 선택된 목록번호를 Toast로 출력
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), String.format("%d 선택 %s", position + 1,lightModels.get(position).SharePixel.get(0)), Toast.LENGTH_LONG).show();
                }

                // lightModels.get(position).pixelColor[lightModels.get(position).index - 1].getG_color(),
            });
        }

        @Override
        public int getItemCount() {
            return lightModels.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView DescriptionText;
            private TextView NameText;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                DescriptionText = (TextView) itemView.findViewById(R.id.DescriptionText);
                NameText = (TextView) itemView.findViewById(R.id.NameText);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.MainActivity_ShareButton:
                LightModel lightModel = new LightModel();

                lightModel.ShareUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                lightModel.ShareLightDescription = TestDescription;

                String[] strs = {"1/2/24", "3/25/23", "43/42/16"};
                // 1번 조명   2번 조명  3번 조명 -------->?

                List<String> list = Arrays.asList(strs);
                lightModel.SharePixel = list;

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                mDatabase.child("recipe").child(ts).setValue(lightModel); // 데이터 쓰기.
                // FirebaseAuth.getInstance().getCurrentUser().getUid()  userId;

                Toast.makeText(getApplicationContext(),"공유",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
