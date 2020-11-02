package com.erbe.fiadeveloper.viewmodel;

import androidx.lifecycle.ViewModel;

/**
 * ViewModel for {@link com.erbe.fiadeveloper.ui.MainActivity}.
 */

public class MainActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;

    public MainActivityViewModel() {
        mIsSigningIn = false;
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

}
