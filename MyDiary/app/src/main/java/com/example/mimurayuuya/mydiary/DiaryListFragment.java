package com.example.mimurayuuya.mydiary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
}
