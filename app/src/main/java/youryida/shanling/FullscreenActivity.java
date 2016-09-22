package youryida.shanling;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String;

/**
 *
 */
public class FullscreenActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler utilHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
//    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private EditText editText;

    private TextView blinkTextView;
    private Blink blink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        editText = (EditText) findViewById(R.id.editText_input);
        blinkTextView = (TextView) findViewById(R.id.fullscreen_content);
        blinkTextView.setText(editText.getText());
        editText.setSelection(editText.getText().length());// focus position

        blink=new Blink(blinkTextView);

        editText.addTextChangedListener(textWatcher);
    }

    //    字体闪动
    private  class Blink{
        TextView btv;
        int repeatCount;
        final int[] colorArr= {0xffffffff,0xff000000};
        private Blink(TextView tv){
            btv=tv;
            repeatCount=0;
        }

        private  void textColorToggle(){
            int index=repeatCount % 2;
            btv.setTextColor(colorArr[index]);
        }
        private final Runnable runnable=new Runnable() {
            @Override
            public void run() {
                textColorToggle();
                utilHandler.postDelayed(this,100);
                repeatCount ++;
                if(repeatCount>=200){
                    stop();
                    repeatCount=0;
                }
            }
        };
        private void start(){
            utilHandler.post(runnable);
        }
        private void stop(){
            utilHandler.removeCallbacks(runnable);
            btv.setTextColor(colorArr[0]);
        }

    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text= editText.getText().toString();
            blinkTextView.setText(text);
        }
    };


    private void toggle() {
        if (mVisible) {
            String s=editText.getText().toString().trim();
            if(s == null || s.length() <= 0) return;
            hide();
            editText.setVisibility(View.INVISIBLE);
            blink.start();
        } else {
            show();
            editText.setVisibility(View.VISIBLE);
            blink.stop();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        utilHandler.removeCallbacks(mShowPart2Runnable);
        utilHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        utilHandler.removeCallbacks(mHidePart2Runnable);
        utilHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

}
