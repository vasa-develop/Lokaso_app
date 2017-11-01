package com.lokaso.model;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class Answer {

    private int id;
    private int discovery_id;
    private String question;
    private String answer_name;
    private String created_date;

    public Answer(int id, int discovery_id, String question, String answer_name, String created_date) {
        this.id = id;
        this.discovery_id = discovery_id;
        this.question = question;
        this.answer_name = answer_name;
        this.created_date = created_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscovery_id() {
        return discovery_id;
    }

    public void setDiscovery_id(int discovery_id) {
        this.discovery_id = discovery_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer_name() {
        return answer_name;
    }

    public void setAnswer_name(String answer_name) {
        this.answer_name = answer_name;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
