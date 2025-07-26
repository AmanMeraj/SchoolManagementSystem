package com.school.schoolmanagement.Students.Model;

public class StudentsExamResultResponse {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int status;
    public String message;
    public Data data;

    public class Data{
        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStudentClass() {
            return studentClass;
        }

        public void setStudentClass(String studentClass) {
            this.studentClass = studentClass;
        }

        public int getSubjectCount() {
            return subjectCount;
        }

        public void setSubjectCount(int subjectCount) {
            this.subjectCount = subjectCount;
        }

        public int getTotalMarks() {
            return totalMarks;
        }

        public void setTotalMarks(int totalMarks) {
            this.totalMarks = totalMarks;
        }

        public int getObtainedMarks() {
            return obtainedMarks;
        }

        public void setObtainedMarks(int obtainedMarks) {
            this.obtainedMarks = obtainedMarks;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public String profileImage;
        public String name;
        public int id;
        public String studentClass;
        public int subjectCount;
        public int totalMarks;
        public int obtainedMarks;
        public double percentage;
    }
}
