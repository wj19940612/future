package com.jnhyxx.html5.view;

/**
 * Created by licunkuan on 2017/2/7.
 * 禁止黏贴的EditText
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class NoPasteEditText extends EditText {

    public NoPasteEditText(Context context) {
        super(context);
        init();
    }

    public NoPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());
        this.setLongClickable(false);
    }

    private class ActionModeCallbackInterceptor implements ActionMode.Callback {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }


        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}
