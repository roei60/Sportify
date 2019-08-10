package com.example.Sportify.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class Common {

    public static void hideKeyboard(Fragment fragment){
        ((InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
    }

    public static void scrollToBottom(RecyclerView recyclerView){
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }
}
