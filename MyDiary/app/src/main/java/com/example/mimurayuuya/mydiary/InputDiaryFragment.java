package com.example.mimurayuuya.mydiary;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.internal.IOException;

import static android.app.Activity.RESULT_OK;


public class InputDiaryFragment extends Fragment implements LocationListener {
    private static final String DIARY_ID = "DIARY_ID";
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private long mDiaryId;
    private Realm mRealm;
    private EditText mTitleEdit;
    private EditText mBodyEdit;
    private TextView mLocationEdit;
    private ImageView mDiaryImage;

    private LocationManager mLocationManager;

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
        mLocationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
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
        mLocationEdit = (TextView) v.findViewById(R.id.location);
        mDiaryImage = (ImageView) v.findViewById(R.id.diary_photo);

        mDiaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(view);
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
                });
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

    private void requestPermissions(View view){
        List<String> permissions = new ArrayList<String>();

        if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)){
                // ストレージへのアクセスが許可されていない
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,R.string.rationale,Snackbar.LENGTH_LONG).show();
            }

            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        }

        if (!isPermissionGranted(Manifest.permission.CAMERA)){
            // カメラへのアクセスが許可されていない
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA)){
                Snackbar.make(view,R.string.rationale_camera,Snackbar.LENGTH_LONG).show();
            }

            permissions.add(Manifest.permission.CAMERA);
        }

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            // GPSへのアクセスが許可されていない
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(view,R.string.rationale_location,Snackbar.LENGTH_LONG).show();
            }

            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissions.size() > 0){
            requestPermissions((String[]) permissions.toArray(new String[]{}),
                    PERMISSION_REQUEST_CODE);
        }else{
            pickImage();
        }

    }

    private boolean isPermissionGranted(String permission){
        return ContextCompat.checkSelfPermission(getActivity(), permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void pickImage(){
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(pickImageIntent,getString(R.string.pick_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{cameraIntent});

        startActivityForResult(chooserIntent,REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null && data.getExtras() != null &&
                    data.getExtras().get("data") != null){
                // カメラで撮影した画像を利用
                try{
                    Bitmap img = (Bitmap) data.getExtras().get("data");
                    setImage(img);
                    if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
                        boolean gpsEnabled =
                                mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (!gpsEnabled){
                            // GPSが無効になっていた場合
                            // 有効化するダイアログを表示
                            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settingsIntent);
                        }
                        if (mLocationManager != null)
                            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,this);
                    }
                }catch(SecurityException se){
                    se.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                // ギャラリーから選択した画像を利用
                Uri uri = (data == null) ? null:data.getData();

                if (uri != null){
                    try{
                        Bitmap img = MyUtils.getImageFromStream(getActivity().getContentResolver(),uri);
                        setImage(img);
                    }catch (java.io.IOException ie){
                        ie.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /*
    * 画像の登録
    * */
    private void setImage(final Bitmap img){
        mDiaryImage.setImageBitmap(img);
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Diary diary = realm.where(Diary.class)
                        .equalTo("id",mDiaryId)
                        .findFirst();
                byte[] bytes = MyUtils.getByteFromImage(img);
                if (bytes != null && bytes.length > 0){
                    diary.image = bytes;
                }
            }
        });
    }

    /*
    *
    * Permission要求の結果
    *
    * requestCode:requestPermissionsで送ったrequestCode
    * permissions:要求したパーミッション
    * grantResults:結果
    *
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if (requestCode == PERMISSION_REQUEST_CODE){

            boolean cameraPermissionGranted =
                    isPermissionGranted(Manifest.permission.CAMERA);
            boolean pickImagePermissionGranted =
                    isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE);

            // 位置情報のパーミッションについては、画像取得には影響がないので特に処理は行わない

            if (cameraPermissionGranted && pickImagePermissionGranted){
                // カメラへのアクセスも内部ストレージへのアクセスも許可
                // 汎用メソッドのpickImageを利用
                pickImage();
            } else if(cameraPermissionGranted && !pickImagePermissionGranted){
                // カメラへのアクセスのみ許可
                // カメラで画像取得
                startActivityForResult(
                        Intent.createChooser(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                                getString(R.string.capture_image)),
                        REQUEST_CODE);
            } else if(!cameraPermissionGranted && pickImagePermissionGranted){
                // 内部ストレージへのアクセスのみ許可
                // 内部ストレージから画像取得
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageIntent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(pickImageIntent,getString(R.string.pick_image)),
                        REQUEST_CODE);
            } else {
                // 両方とも許可されなかった
                // メッセージ表示
                Snackbar.make(mDiaryImage,R.string.permission_deny,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // 住所の取得
        final StringBuffer strAddress = new StringBuffer();
        Geocoder geocorder = new Geocoder(getContext(), Locale.JAPAN);
        try{
            List<Address> addressList =
                    geocorder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            for(Address addr:addressList){
                int index = addr.getMaxAddressLineIndex();
                for (int i = 1;i < index ; i++)
                    strAddress.append(addr.getAddressLine(i));

            }
            mLocationEdit.setText(strAddress.toString());
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Diary diary = realm.where(Diary.class).equalTo("id",
                            mDiaryId).findFirst();
                    diary.location = strAddress.toString();
                }
            });
            if (mLocationManager != null)
                mLocationManager.removeUpdates(this);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
