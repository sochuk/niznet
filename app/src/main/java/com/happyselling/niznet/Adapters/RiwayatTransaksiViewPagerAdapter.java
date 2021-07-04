package com.happyselling.niznet.Adapters;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.happyselling.niznet.Fragments.RiwayatTransaksiFragment;
import com.happyselling.niznet.Models.StatusTrx;

import java.util.List;

public class RiwayatTransaksiViewPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    Fragment fragment = null;
    List<List<StatusTrx>> data;

    public RiwayatTransaksiViewPagerAdapter(FragmentManager fm, int NumOfTabs, List<List<StatusTrx>> data) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
   /* ProductFragment pf = ProductFragment.newInstance(data.get(position),position);
    return pf;*/
        Log.d("###ADAPTER", "getItem: "+data.size());
        return RiwayatTransaksiFragment.newInstance(data.get(position),position);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
