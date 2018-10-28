package com.example.vclab.moodlightshare.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vclab.moodlightshare.R;
import com.example.vclab.moodlightshare.model.LightModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class ShareDialog {

    private Context context;


    private DatabaseReference mDatabase;

    public ShareDialog(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_share);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText message = (EditText) dlg.findViewById(R.id.mesgase);
        final Button okButton = (Button) dlg.findViewById(R.id.dialog_okButton);
        final Button shareButton = (Button) dlg.findViewById(R.id.dialog_shareButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                Toast.makeText(context, "\"" +  message.getText().toString() + "\" 을 입력하였습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LightModel lightModel = new LightModel();

                lightModel.ShareUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                if(message.getText().toString().equals("")){
                    lightModel.ShareLightDescription = lightModel.ShareUserName.toString() +"의 조명";
                }else {
                    lightModel.ShareLightDescription = message.getText().toString();
                }

                String[] strs = {"1/2/24", "3/25/23", "43/42/16"};
                // 1번 조명   2번 조명  3번 조명 -------->?

                List<String> list = Arrays.asList(strs);
                lightModel.SharePixel = list;

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                mDatabase.child("recipe").child(ts).setValue(lightModel); // 데이터 쓰기.
                // FirebaseAuth.getInstance().getCurrentUser().getUid()  userId;

                Toast.makeText(context,"공유",Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
}