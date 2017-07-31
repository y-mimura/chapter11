package com.example.mimurayuuya.mydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import io.realm.Realm;
import io.realm.internal.IOException;

import static android.app.Activity.RESULT_OK;


public class InputDiaryFragment extends Fragment {
    private static final String DIARY_ID = "DIARY_ID";
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private long mDiaryId;
    private Realm mRealm;
    private EditText mTitleEdit;
    private EditText mBodyEdit;
    private ImageView mDiaryImage;

    public static InputDiaryFragment newInstance(long diaryId){
        InputDiaryFragment fragment = new InputDiaryFragment();
        Bundle args = new Bundle();
        args.putLong(DIARY_ID,diaryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mDiaryId = getArguments().getLong(DIARY_ID);
        }
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_input_diary,container,false);
        mTitleEdit = (EditText) v.findViewById(R.id.title);
        mBodyEdit = (EditText) v.findViewById(R.id.bodyEditText);
        mDiaryImage = (ImageView) v.findViewById(R.id.diary_photo);

        mDiaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReadStorage(view);
            }
        });

        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id",mDiaryId).findFirst();
                        diary.title = editable.toString();
                    }
                })
            }
        });

        mBodyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class).equalTo("id",
                                mDiaryId).findFirst();
                        diary.bodyText = editable.toString();
                    }
                });
            }
        });

        return v;

    }

    private void requestReadStorage(View view){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,R.string.rationale,Snackbar.LENGTH_LONG).show();
            }

            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },PERMISSION_REQUEST_CODE);

        }else{
            pickImage();
        }
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        getString(R.string.pick_image)
                ),REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Uri uri = (data == null) ? null:data.getData();
            if (uri != null){
                try{
                    Bitmap img = MyUtils.getImageFromStream(getActivity().getContentResolver(),uri);
                    mDiaryImage.setImageBitmap(img);
                }catch (java.io.IOException e){
                    e.printStackTrace();
                }
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary diary = realm.where(Diary.class)
                                .equalTo("id",mDiaryId)
                                .findFirst();
                        BitmapDrawable bitmap =
                                (BitmapDrawable) mDiaryImage.getDrawable();
                        byte[] bytes = MyUtils.getByteFromImage(bitmap.getBitmap());
                        if (bytes != null && bytes.length > 0){
                            diary.image = bytes;
                        }
                    }
                });
            }
        }
    }

    public void onRequestCodePermissionResult(int requestCode,
                                              @NonNull String[] permissions,
                                              @NonNull int[] grantResults){
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Snackbar.make(mDiaryImage,R.string.permission_deny,
                        Snackbar.LENGTH_LONG).show();
            }else{
                pickImage();
            }
        }
    }

}
