package com.school.schoolmanagement.Students.Model;

public class StudentTestReport {
    private String subjectName;
    private int iconResId;
    private int progress;
    private String totalScore;
    private String scoreObtained;
    private String averageScore;

    public StudentTestReport(String subjectName, int iconResId, int progress, String totalScore, String scoreObtained, String averageScore) {
        this.subjectName = subjectName;
        this.iconResId = iconResId;
        this.progress = progress;
        this.totalScore = totalScore;
        this.scoreObtained = scoreObtained;
        this.averageScore = averageScore;
    }

    public String getSubjectName() { return subjectName; }
    public int getIconResId() { return iconResId; }
    public int getProgress() { return progress; }
    public String getTotalScore() { return totalScore; }
    public String getScoreObtained() { return scoreObtained; }
    public String getAverageScore() { return averageScore; }
}

