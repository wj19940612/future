package com.jnhyxx.html5.domain.live;

public class LastTeacherCommand {

    private LiveHomeChatInfo msg;
    private LiveMessage.TeacherInfo teacher;

    public LiveHomeChatInfo getMsg() {
        return msg;
    }

    public LiveMessage.TeacherInfo getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "LastTeacherCommand{" +
                "msg=" + msg +
                ", teacher=" + teacher +
                '}';
    }
}
