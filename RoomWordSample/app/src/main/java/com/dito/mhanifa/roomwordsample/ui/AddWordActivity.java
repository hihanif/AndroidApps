package com.dito.mhanifa.roomwordsample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dito.mhanifa.roomwordsample.R;

public class AddWordActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.dito.mhanifa.roomwordsample.EXTRA_NEW_WORD";

    private EditText mEditTextNewWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_word);
        mEditTextNewWord = findViewById(R.id.edittext_add_word);

        findViewById(R.id.button_addword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditTextNewWord.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    replyIntent.putExtra(EXTRA_REPLY, mEditTextNewWord.getText().toString());
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }

}
