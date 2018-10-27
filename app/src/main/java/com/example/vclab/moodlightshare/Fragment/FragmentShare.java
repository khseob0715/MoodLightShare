package com.example.vclab.moodlightshare.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vclab.moodlightshare.R;
import com.example.vclab.moodlightshare.ShareFunc;
import com.example.vclab.moodlightshare.model.LightModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentShare extends Fragment {

    RecyclerView mRecyclerView;
    DatabaseReference mDatabase;

    ProgressBar mProgressBar;

    Animation slide_up;

    public static int Once = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        slide_up = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share, null);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.share_fragment_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new ShareRecyclerAdapter());

        return view;
    }


    class ShareRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<LightModel> lightModels;

        public ShareRecyclerAdapter() {
            lightModels = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("recipe").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 처음 넘어오는 데이터 // ArrayList 값.
                    lightModels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LightModel lightModel = snapshot.getValue(LightModel.class);
                        lightModels.add(lightModel);
                        Log.e("Tag", snapshot.child("ShareLightDescription").getValue(String.class).toString());
                    }
                    notifyDataSetChanged();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (Once == 0) {
                        mRecyclerView.startAnimation(slide_up);
                    }
                    mProgressBar.setVisibility(View.GONE);

                    Once = 1;

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override// 뷰 홀더 생성
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);

            return new ShareRecyclerAdapter.ItemViewHolder(view);
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            // 해당 position 에 해당하는 데이터 결합
            ((ShareRecyclerAdapter.ItemViewHolder) holder).NameText.setText(lightModels.get(position).ShareUserName);
            ((ShareRecyclerAdapter.ItemViewHolder) holder).DescriptionText.setText(lightModels.get(position).ShareLightDescription);

            // 이벤트처리 : 생성된 List 중 선택된 목록번호를 Toast로 출력
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), String.format("%d 선택 %s", position + 1, lightModels.get(position).SharePixel.get(0)), Toast.LENGTH_LONG).show();
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
}
