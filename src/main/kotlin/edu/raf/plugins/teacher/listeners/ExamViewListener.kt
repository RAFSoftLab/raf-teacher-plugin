package edu.raf.plugins.teacher.listeners

import edu.raf.plugins.teacher.models.Subject

interface ExamViewListener {
    fun onSubmitExam(subject: Subject, year: String, testName: String, group: String)
}