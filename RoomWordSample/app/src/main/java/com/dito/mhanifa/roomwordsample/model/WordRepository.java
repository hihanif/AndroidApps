package com.dito.mhanifa.roomwordsample.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.dito.mhanifa.roomwordsample.model.local.Word;
import com.dito.mhanifa.roomwordsample.model.local.WordDao;
import com.dito.mhanifa.roomwordsample.model.local.WordRoomDatabase;

import java.util.List;

public class WordRepository {
    private WordDao mWordDao;
    private LiveData<List<Word>> mAllWords;

    public WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert(Word word) {
        new InsertAsyncTask(mWordDao).execute(word);
    }

    private static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mAsyncWordDao;

        public InsertAsyncTask(WordDao dao) {
            mAsyncWordDao = dao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            mAsyncWordDao.insert(words[0]);
            return null;
        }
    }
}
