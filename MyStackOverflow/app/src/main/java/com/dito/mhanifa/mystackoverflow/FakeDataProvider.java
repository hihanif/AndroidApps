package com.dito.mhanifa.mystackoverflow;

import com.dito.mhanifa.mystackoverflow.data.remote.Answer;
import com.dito.mhanifa.mystackoverflow.data.remote.Question;

import java.util.ArrayList;
import java.util.List;

public class FakeDataProvider {
    public static List<Question> getQuestions() {
        List<Question> qns = new ArrayList<>();

        for (int i = 0 ; i < 10; i ++) {
            Question q = new Question();
            q.title ="question#" + i;
            q.body = "questionbody #" + i;
            q.questionId = String.valueOf(i);
            qns.add(q);
        }

        return qns;
    }

    public static List<Answer> getAnswers() {
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Answer a = new Answer();
            a.answerId = i;
            a.isAccepted = (i % 2 == 0) ? true : false;
            a.score = (int)Math.random() % 1000;
            answers.add(a);
        }

        return answers;
    }

}
