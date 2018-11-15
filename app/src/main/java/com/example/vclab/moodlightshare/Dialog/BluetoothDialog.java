package com.example.vclab.moodlightshare.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vclab.moodlightshare.Fragment.FragmentShare;
import com.example.vclab.moodlightshare.R;
import com.example.vclab.moodlightshare.model.LightModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDialog {
    private Context context;

    public BluetoothDialog(Context context) {
        this.context = context;
    }


    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_bluetoothlist);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        final RecyclerView mRecyclerView = (RecyclerView) dlg.findViewById(R.id.bluetooth_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(new BluetoothRecyclerAdapter());
    }


    class BluetoothRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<LightModel> lightModels;

        public BluetoothRecyclerAdapter() {

        }

        @Override// 뷰 홀더 생성
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_device, parent, false);

            return new BluetoothRecyclerAdapter.ItemViewHolder(view);
        }

        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            // 해당 position 에 해당하는 데이터 결합
            ((BluetoothRecyclerAdapter.ItemViewHolder) holder).DeviceName.setText(lightModels.get(position).ShareUserName);
            ((BluetoothRecyclerAdapter.ItemViewHolder) holder).DeviceAddress.setText(lightModels.get(position).ShareLightDescription);



            // 이벤트처리 : 생성된 List 중 선택된 목록번호를 Toast로 출력
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, String.format("%d 선택 %s", position + 1, lightModels.get(position).SharePixel.get(0)), Toast.LENGTH_LONG).show();
                }

                // lightModels.get(position).pixelColor[lightModels.get(position).index - 1].getG_color(),
            });
        }

        @Override
        public int getItemCount() {
            return lightModels.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView DeviceName;
            private TextView DeviceAddress;


            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                DeviceName = (TextView) itemView.findViewById(R.id.device_name);
                DeviceAddress = (TextView) itemView.findViewById(R.id.device_address);

            }
        }
    }
}
