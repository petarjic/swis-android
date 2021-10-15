/*
package com.nisintechnologies.swiss.custom.customviews;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;



import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by nitesh on 13/2/17.
 *//*


public class NisinSlider extends ViewPager {
    private List<News> mBuzzList = new ArrayList<>();
    private CountDownTimer mTimer;

    public NisinSlider(Context context) {
        super(context);
    }

    public NisinSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setList(List<News> buzzList){

        this.mBuzzList = buzzList;

        FragmentActivity activity = (FragmentActivity) getContext();
        MyPager adapter = new MyPager(activity.getSupportFragmentManager(), mBuzzList);
        setAdapter(adapter);
        setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        mTimer = new CountDownTimer(60*60*1000,6000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(mBuzzList!=null && mBuzzList.size()>0) {
                    NisinSlider.this.setCurrentItem((NisinSlider.this.getCurrentItem() + 1) % mBuzzList.size());
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }


    public class MyPager extends FragmentStatePagerAdapter {

        private final List<News> mList;

        public MyPager(FragmentManager fm, List<News> list) {
            super(fm);
            this.mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            SliderPagerFragment fragment = new SliderPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstant.EXTRA_OBJECT, mList.get(position));
            fragment.setArguments(bundle);
            return fragment;

        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }
}
*/
