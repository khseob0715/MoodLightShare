package com.example.vclab.moodlightshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    ArrayList<Item> mItems;

    Context mContext;

    public RecyclerAdapter(ArrayList<Item> items){
        mItems = items;
    }

    @Override// 뷰 홀더 생성
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,parent,false);
        mContext = parent.getContext();
        RecyclerViewHolder  holder = new RecyclerViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
// 해당 position 에 해당하는 데이터 결합
        holder.name.setText(mItems.get(position).name);
        holder.Description.setText(mItems.get(position).description);

        // 이벤트처리 : 생성된 List 중 선택된 목록번호를 Toast로 출력
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, String.format("%d %d 선택 %d", position + 1, mItems.get(position).pixelColor[mItems.get(position).index-1].getG_color(), mItems.get(position).pixelColor[mItems.get(position).index-2].getG_color()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView DescriptionText;
        private TextView NameText;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            DescriptionText = (TextView)itemView.findViewById(R.id.DescriptionText);
            NameText = (TextView)itemView.findViewById(R.id.NameText);

       }
    }
}
