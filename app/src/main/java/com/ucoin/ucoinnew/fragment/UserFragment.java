package com.ucoin.ucoinnew.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.ucoin.ucoinnew.activity.MainActivity;

public class UserFragment extends Fragment {
    private MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mMainActivity = (MainActivity) context;
    }
}