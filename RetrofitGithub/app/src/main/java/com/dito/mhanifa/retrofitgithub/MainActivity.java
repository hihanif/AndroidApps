package com.dito.mhanifa.retrofitgithub;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dito.mhanifa.retrofitgithub.data.remote.GithubAPI;
import com.dito.mhanifa.retrofitgithub.data.remote.GithubIssue;
import com.dito.mhanifa.retrofitgithub.data.remote.GithubRepo;
import com.dito.mhanifa.retrofitgithub.data.remote.GithubRepoDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CredentialsDialog.ICredentialsDialogCallback {

    Spinner repoSpinner;
    Spinner issueSpinner;
    private EditText commentsEditText;
    private Button loadReposButton;
    private String username;
    private String password;
    private GithubAPI githubAPI;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Button sendCommentButton;

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

        repoSpinner = findViewById(R.id.repo_spinner);
        repoSpinner.setEnabled(false);
        repoSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[] { "No repo available"}));
        repoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem() instanceof GithubRepo) {
                    GithubRepo githubRepo = (GithubRepo) parent.getSelectedItem();
                    compositeDisposable.add(githubAPI.getIssues(githubRepo.owner, githubRepo.name)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getIssuesObserver()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO
            }
        });

        issueSpinner = findViewById(R.id.issues_spinner);
        issueSpinner.setEnabled(false);
        issueSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[] { "Choose a repo first"}));

        commentsEditText = findViewById(R.id.comment_edittext);
        loadReposButton = findViewById(R.id.load_repo_button);
        sendCommentButton = findViewById(R.id.send_comment_button);

        createGithubAPI();
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

        if (id == R.id.menu_credentials) {
            new CredentialsDialog().show(getSupportFragmentManager(), "credentialsDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(String username, String password) {
//        this.username = username;
//        this.password = password;

        loadReposButton.setEnabled(true);
    }

    private void createGithubAPI () {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(GithubRepo.class, new GithubRepoDeserializer()).create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        return chain.proceed(request.newBuilder()
                                .header("Authorization", Credentials.basic(username, password))
                                .build());
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubAPI.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        githubAPI = retrofit.create(GithubAPI.class);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.load_repo_button:
                compositeDisposable.add(githubAPI.getRepos()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getRepositoriesObserver()));
                break;
            case R.id.send_comment_button:
                String givenComment = commentsEditText.getText().toString();
                GithubIssue githubIssue = (GithubIssue) issueSpinner.getSelectedItem();
                githubIssue.comment = givenComment;
                compositeDisposable.add(githubAPI.postComment(githubIssue.comments_url, githubIssue)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getCommentsObserver()));
                break;
        }
    }

    private DisposableSingleObserver<List<GithubRepo>> getRepositoriesObserver() {
        return new DisposableSingleObserver<List<GithubRepo>>() {
            @Override
            public void onSuccess(List<GithubRepo> githubRepos) {
                if (githubRepos.isEmpty()) {
                    repoSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            new String[] { "User has no repositories"}));
                    return;
                }
                repoSpinner.setEnabled(true);
                repoSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        githubRepos));
                return;
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Cannot load repos", Toast.LENGTH_SHORT).show();
                repoSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[] { "Cannot load repositories"}));
            }
        };
    }

    private DisposableSingleObserver<List<GithubIssue>> getIssuesObserver() {
        return new DisposableSingleObserver<List<GithubIssue>>() {
            @Override
            public void onSuccess(List<GithubIssue> githubIssues) {
                if (githubIssues.isEmpty()) {
                    issueSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            new String[] { "Your repo has no issues!"}));
                    return;
                }

                issueSpinner.setEnabled(true);
                issueSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        githubIssues));

                commentsEditText.setEnabled(true);
                sendCommentButton.setEnabled(true);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Failed to fetch issues", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private DisposableSingleObserver<ResponseBody> getCommentsObserver() {
        return new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                commentsEditText.setText("");
                Toast.makeText(MainActivity.this, "Comment created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Failed to create comment", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
