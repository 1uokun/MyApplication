package com.github.react.sextant.recyclerview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.react.sextant.viewpager.CrimePagerActivity;

import java.util.List;

public class CrimeFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private CrimeLab mCrimeLab = CrimeLab.get(getActivity());

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private boolean mSubtitleVisible = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){
        //选择设备时
        if(saveInstanceState != null){
            mSubtitleVisible = saveInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }


        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);

        /**
         * setLayoutManager
         *
         * RecyclerView视图创建完成后，就理解转交给了LayoutManager对象。
         * RecyclerView类不会亲自摆放屏幕上的列表项。实际上，摆放的任务被委托给类LayoutManager。
         * 除了在屏幕上摆放列表项，LayoutManager还负责定义屏幕滚动行为。
         *
         * LayoutManager除了内置LinearLayoutManager类，还有很多第三方库实现版本。
         * **/
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Crime> crimeList = mCrimeLab.getmCrimeList();
        mCrimeAdapter = new CrimeAdapter(crimeList);
        mCrimeRecyclerView.setAdapter(mCrimeAdapter);

        updateSubtitle();
        return v;
    }

    /**
     * 存储state
     * **/
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("LUOKUNL: "+mSubtitleVisible);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //将XML资源填充到Menu中
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_crime) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + mCrimeLab.getmCrimeList().size());
            crime.setSolved(mCrimeLab.getmCrimeList().size() % 2 == 0);
            mCrimeLab.addCrime(crime);

            Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
            startActivity(intent);
            return true;
        }else if(item.getItemId() == R.id.show_subtitle){
            getActivity().invalidateOptionsMenu();
            mSubtitleVisible = !mSubtitleVisible;
            updateSubtitle();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 定义ViewHolder内部类
     * **/
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;


        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.crime_item, parent, false));

            /**
             * itemView
             *
             * RecyclerView.ViewHolder自带变量，指向super内传递的layout
             * 比如：CrimeHolder crimeHolder = new CrimeHolder(inflater.inflate(R.layout.crime_item, parent, false))
             * View view = crimeHolder.itemView;
             * **/
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mDateTextView = (TextView) itemView.findViewById(R.id.date);

            //为拓展类View.OnClickListener绑定context
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getId().toString());
        }

        /**
         * 重写点击事件
         *
         * 光标移到implements上 快捷键Alert+Enter
         * **/
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), mCrime.getTitle(),Toast.LENGTH_LONG).show();

            //move scrollTo
//            mCrimeRecyclerView.getAdapter().notifyItemMoved(0, 98);
        }


    }

    /**
     * 定义RecyclerView.Adapter内部类
     *
     * 借助Adapter提供视图
     * **/
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimeList;

        public CrimeAdapter(List<Crime> crimeList) {
            mCrimeList = crimeList;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, viewGroup);
        }

        /**
         * onBindViewHolder
         *
         * 任何时候都要保证该方法轻巧、高效
         * **/
        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int position) {
            Crime crime = mCrimeList.get(position);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimeList.size();
        }
    }

    /**
     * 设置工具栏子标题
     * **/
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getmCrimeList().size();
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String subtitle = getString(R.string.subtitle_format, crimeCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
}
