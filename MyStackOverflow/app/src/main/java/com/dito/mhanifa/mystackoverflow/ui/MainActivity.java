package com.dito.mhanifa.mystackoverflow.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.dito.mhanifa.mystackoverflow.R;
import com.dito.mhanifa.mystackoverflow.data.remote.Answer;
import com.dito.mhanifa.mystackoverflow.data.remote.ListWrapper;
import com.dito.mhanifa.mystackoverflow.data.remote.Question;
import com.dito.mhanifa.mystackoverflow.data.remote.StackoverFlowAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String token;

    private Spinner questionSpinner;
    private RecyclerView answerRecyclerView;
    private Button authenticateButton;

    private StackoverFlowAPI stackOverflowApi;

    @Override
    protected void onRestart() {
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        questionSpinner = findViewById(R.id.questions_spinner);
        questionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Spinner view is selected", Toast.LENGTH_SHORT)
                        .show();
                Question q = (Question)parent.getAdapter().getItem(position);
                stackOverflowApi.getAnswersForQuestion(q.questionId).enqueue(answersCallback);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        List<Question> qns = FakeDataProvider.getQuestions();
//        ArrayAdapter<Question> arrayAdapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_dropdown_item, qns);
//        questionSpinner.setAdapter(arrayAdapter);


        authenticateButton = findViewById(R.id.button_auth);

        answerRecyclerView = findViewById(R.id.answer_recyclerview);
        answerRecyclerView.setHasFixedSize(true);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        RecyclerViewAdapter recyclerViewAdapter =
//                new RecyclerViewAdapter(FakeDataProvider.getAnswers());
//        answerRecyclerView.setAdapter(recyclerViewAdapter);

        createStackOverflowAPI();
        stackOverflowApi.getQuestions().enqueue(questionsCallback);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (token != null) {
            authenticateButton.setEnabled(false);
        }
    }

    
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case android.R.id.text1:
                if (token != null) {
                    // TODO
                } else {
                    Toast.makeText(this, "You need to authenticate first", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_auth:
                // TODO
                break;
        }
    }

    private void createStackOverflowAPI() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StackoverFlowAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        stackOverflowApi = retrofit.create(StackoverFlowAPI.class);
    }

    Callback<ListWrapper<Question>> questionsCallback = new Callback<ListWrapper<Question>>() {
        @Override
        public void onResponse(Call<ListWrapper<Question>> call, Response<ListWrapper<Question>> response) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "QuestionsCallback onResponse" + " errorBody=" + response.errorBody()
                        + " code=" + response.code() + " msg=" + response.message()) ;
                return;
            }

            ArrayAdapter<Question> spinnerAdapter =
                    new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item,
                            response.body().items);
            questionSpinner.setAdapter(spinnerAdapter);
        }

        @Override
        public void onFailure(Call<ListWrapper<Question>> call, Throwable t) {
            Toast.makeText(MainActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    };

    Callback<ListWrapper<Answer>> answersCallback = new Callback<ListWrapper<Answer>>() {
        @Override
        public void onResponse(Call<ListWrapper<Answer>> call, Response<ListWrapper<Answer>> response) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "AnswerCallback onResponse" + " code="
                        + response.code() + " msg=" + response.message());
                return;
            }

            List<Answer> answers = new ArrayList<>();
            answers.addAll(response.body().items);
            answerRecyclerView.setAdapter(new RecyclerViewAdapter(MainActivity.this, answers));
        }

        @Override
        public void onFailure(Call<ListWrapper<Answer>> call, Throwable t) {
            t.printStackTrace();
        }
    };

}
