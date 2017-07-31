package com.example.mimurayuuya.mydiary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity
        implements DiaryListFragment.OnFragmentInteractionListener {

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();

        createTestData();
        showDiaryList();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    private void createTestData(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // idフィールドの最大値を取得
                Number maxId = mRealm.where(Diary.class).max("id");
                long nextId = 0;
                if (maxId != null) nextId = maxId.longValue() + 1;
                // createObjectではIDを渡してオブジェクトを生成する
                Diary diary = realm.createObject(Diary.class,new Long(nextId));
                diary.title = "テストタイトル";
                diary.bodyText = "テスト本文です。";
                diary.date = "Feb 22";
            }
        });
    }

    private void showDiaryList(){
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("DiaryListFragment");
        if (fragment == null){
            fragment = new DiaryListFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.content,fragment,"DiaryListFragment");
            transaction.commit();
        }
    }

    @Override
    public void onAddDiarySelected(){
        // 新規ダイアリー追加処理
    }

}
