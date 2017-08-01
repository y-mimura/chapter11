package com.example.mimurayuuya.mydiary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmResults;


public class DiaryListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Realm mRealm;

    public DiaryListFragment() {
        // Required empty public constructor
    }

    public static DiaryListFragment newInstance() {
        DiaryListFragment fragment = new DiaryListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diary_list,container,false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        RealmResults<Diary> diaries = mRealm.where(Diary.class).findAll();
        DiaryRealmAdapter adapter =
                new DiaryRealmAdapter(getActivity(),diaries,true);

        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    public interface OnFragmentInteractionListener {
        void onAddDiarySelected();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_diary_list,menu);
        MenuItem addDiary = menu.findItem(R.id.menu_item_add_diary);
        MenuItem deleteAll = menu.findItem(R.id.menu_item_delete_all);
        MyUtils.tintMenuIcon(getContext(),addDiary,android.R.color.white);
        MyUtils.tintMenuIcon(getContext(),deleteAll,android.R.color.white);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_add_diary:
                if (mListener != null) mListener.onAddDiarySelected();
                return true;
            case R.id.menu_item_delete_all:
                final RealmResults<Diary> diaries =
                        mRealm.where(Diary.class).findAll();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        diaries.deleteAllFromRealm();
                    }
                });
                return true;
        }
        return false;
    }
}
