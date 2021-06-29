package com.example.graduationproject.sign_language;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.example.graduationproject.ui.ChatPageDeaf;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class KeyboardManager {
    private Context context;
    private Activity activity;
    private EditText textSend;
    private LinearLayout keyboard;
    private boolean isOpen = false;

    public KeyboardManager(Context context, Activity activity, EditText textSend, LinearLayout keyboard) {
        this.context = context;
        this.activity = activity;
        this.textSend = textSend;
        this.keyboard = keyboard;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void hidePrimaryKeyboardApi21() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        textSend.setShowSoftInputOnFocus(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showPrimaryKeyboardApi21() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        textSend.setShowSoftInputOnFocus(true);
    }

    public void hidePrimaryKeyboardApi11() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        textSend.setTextIsSelectable(true);
    }

    public void showPrimaryKeyboardApi11() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        textSend.setTextIsSelectable(false);
    }

    public void hideSignLanguageKeyboard() {
        ViewGroup.LayoutParams params = keyboard.getLayoutParams();
        params.height = 0;
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        keyboard.setLayoutParams(params);
    }

    public void showSignLanguageKeyboard() {
        ViewGroup.LayoutParams params = keyboard.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        keyboard.setLayoutParams(params);
    }


    public void changeKeyboard(OnHidePrimaryKeyboard onHidePrimaryKeyboard) {


        KeyboardVisibilityEvent.setEventListener(
                activity,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpened) {

                        isOpen = isOpened;
                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// API 21
            Log.v("TAG", "states keyborad:" + isOpen);
            if (!isOpen) {
                showPrimaryKeyboardApi21();
                hideSignLanguageKeyboard();
            } else {
                hidePrimaryKeyboardApi21();
                showSignLanguageKeyboard();
                onHidePrimaryKeyboard.afterHidePrimaryKeyboard();
            }
        } else {// API 11-20
            if (!isOpen) {
                showPrimaryKeyboardApi11();
                hideSignLanguageKeyboard();
            } else {
                hidePrimaryKeyboardApi11();
                showSignLanguageKeyboard();
                onHidePrimaryKeyboard.afterHidePrimaryKeyboard();
            }
        }

    }

    public interface OnHidePrimaryKeyboard {
        void afterHidePrimaryKeyboard();
    }

}
