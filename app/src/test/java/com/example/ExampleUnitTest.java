package com.example;

import com.example.data.entity.QuestionEntity;
import com.example.data.seed.QuestionDataSeeder;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExampleUnitTest {

    @Test
    public void testAdmissionMarkingScoring() {
        int correctCount = 8;
        int wrongCount = 2;

        // Engineering admission marking: +1.0 for correct, -0.25 for wrong
        double score = (correctCount * 1.0) - (wrongCount * 0.25);
        assertEquals(7.50, score, 0.001);
    }

    @Test
    public void testAllSubjectsCoveredInSeeder() {
        QuestionDataSeeder seeder = new QuestionDataSeeder();
        List<QuestionEntity> questions = seeder.getInitialQuestions();

        assertTrue("Questions should not be empty", !questions.isEmpty());

        Set<String> subjects = new HashSet<>();
        for (QuestionEntity q : questions) {
            subjects.add(q.getSubject());
        }

        assertTrue(subjects.contains("Physics"));
        assertTrue(subjects.contains("Chemistry"));
        assertTrue(subjects.contains("Higher Math"));
        assertTrue(subjects.contains("English"));
    }

    @Test
    public void testQuestionOptionIndicesValid() {
        QuestionDataSeeder seeder = new QuestionDataSeeder();
        List<QuestionEntity> questions = seeder.getInitialQuestions();

        for (QuestionEntity q : questions) {
            assertTrue("Correct option index must be 0..3", q.getCorrectOptionIndex() >= 0 && q.getCorrectOptionIndex() <= 3);
            assertTrue("Question text should not be blank", q.getQuestionText() != null && !q.getQuestionText().trim().isEmpty());
            assertTrue("Explanation should not be blank", q.getExplanation() != null && !q.getExplanation().trim().isEmpty());
        }
    }
}
