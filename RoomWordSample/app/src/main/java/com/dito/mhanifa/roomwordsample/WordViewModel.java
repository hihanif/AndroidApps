package com.dito.mhanifa.roomwordsample;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.dito.mhanifa.roomwordsample.model.WordRepository;
import com.dito.mhanifa.roomwordsample.model.local.Word;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordRepository mRepo;
    private LiveData<List<Word>> mAllWords;

    public WordViewModel(@NonNull Application application) {
        super(application);
        mRepo = new WordRepository(application);
        mAllWords = mRepo.getAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert(Word word) {
        mRepo.insert(word);
    }
}
