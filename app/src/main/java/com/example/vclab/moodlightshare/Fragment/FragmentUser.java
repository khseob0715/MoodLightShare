package com.example.vclab.moodlightshare.Fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vclab.moodlightshare.MainActivity;
import com.example.vclab.moodlightshare.R;
import com.example.vclab.moodlightshare.model.LightModel;
import com.example.vclab.moodlightshare.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUser extends Fragment {


    private static final String TAG = "FragmentUser";
    RecyclerView mRecyclerView;
    DatabaseReference mDatabase;

    StorageReference mStoragedRef;

    TextView UserName, UserId;
    CircleImageView profileImage;
    String UserUid;

    String uirl;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStoragedRef = FirebaseStorage.getInstance().getReference();
        UserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, null);

        UserName = (TextView) view.findViewById(R.id.fragment_user_userName);
        UserId = (TextView) view.findViewById(R.id.fragment_user_userId);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        profileImage = (CircleImageView) view.findViewById(R.id.profile_image);

       // profileImage.setImageURI(MainActivity.profileUri);
        Picasso.get().load(MainActivity.profileUri).into(profileImage);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(getContext())
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                profileImage.setImageURI(uri);
                                Log.e("uri",uri.toString());
                                StorageReference riversRef = mStoragedRef.child("images/" + UserUid + "/" +uri.getLastPathSegment());

                                // Register observers to listen for when the download is done or if it fails
                                riversRef.putFile(uri).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot task) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                        UserModel userModel = new UserModel();
                                        userModel.uid = UserUid;
                                        userModel.profileImageUrl = task.getDownloadUrl().toString();

                                        mDatabase.child("users").child(UserUid).setValue(userModel); // 데이터 쓰기..
                                    }
                                });
                            }
                        })
                        .create();

                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager());
            }

        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserName.setText(user.getDisplayName());
        UserId.setText(user.getEmail());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.userRecyclerView);
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
                        if (snapshot.child("ShareUserUid").getValue(String.class).toString().equals(UserUid)) {
                            lightModels.add(lightModel);
                        }
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
