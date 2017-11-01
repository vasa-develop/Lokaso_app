package com.lokaso.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Laksh on 24-02-2017
 */
public class CustomEditText {

    private static final String TAG = CustomEditText.class.getSimpleName();

    private Context context;
    private EditText editText;

    private static CustomEditText instance;

    // Define default constants
    private static final int DELAY = 500;
    private static final int THRESHOLD = 1;

    private static final int MESSAGE_TYPING = 1;

    private int delay = DELAY;
    private int threshold = THRESHOLD;

    public CustomEditText(Context ctx) {
        context = ctx;
        init(context);
    }

    public static CustomEditText with(Context context) {
        instance = new CustomEditText(context);
        return instance;
    }

    private void init(Context context) {
    }

    public CustomEditText delay(int delay) {
        this.delay = delay;
        return this;
    }



    private TextWatcherListener textWatcherListener;
    public interface TextWatcherListener {
        void onTextChangeComplete(String text);

        /**
         * This method is called for every text change without any conditions
         *
         * @param text
         */
        void onTextChange(String text);
    }

    public <T> CustomEditText watcher(final TextWatcherListener listener) {
        textWatcherListener = listener;
        return this;
    }


    private ThresholdListener thresholdListener;
    public interface ThresholdListener {
        void lessThanThreshold(String text);
        void equalToThreshold(String text);
        void moreThanThreshold(String text);
    }

    public CustomEditText threshold(int value) {
        this.threshold = value;
        return this;
    }

    public <T> CustomEditText threshold(final ThresholdListener listener) {
        thresholdListener = listener;
        return this;
    }

    public <T> CustomEditText threshold(int value, final ThresholdListener listener) {
        thresholdListener = listener;
        this.threshold = value;
        return this;
    }


    public <T> CustomEditText into(EditText editText) {
        this.editText = editText;
        return this;
    }


    public <T> CustomEditText start() {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String text = charSequence.toString();

                if(textWatcherListener!=null) {
                    textWatcherListener.onTextChange(text);
                }

                if(thresholdListener!=null) {
                    if(text.length()>threshold) {
                        thresholdListener.moreThanThreshold(text);
                    }
                    else if(text.length()<threshold) {
                        thresholdListener.lessThanThreshold(text);
                    }
                    else if(text.length()==threshold) {
                        thresholdListener.equalToThreshold(text);
                    }
                }

                if (handler != null) {

                    if(handler.hasMessages(MESSAGE_TYPING))
                        handler.removeMessages(MESSAGE_TYPING);

                    if(text.length()>threshold) {

                        Message msg = new Message();
                        msg.obj = text;
                        msg.what = MESSAGE_TYPING;

                        handler.sendMessageDelayed(msg, delay);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return this;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MESSAGE_TYPING) {

                String text = (String) msg.obj;

                if(textWatcherListener!=null) {
                    textWatcherListener.onTextChangeComplete(text);
                }
            }
        }

        @Override
        public String getMessageName(Message message) {
            return super.getMessageName(message);
        }
    };

}
