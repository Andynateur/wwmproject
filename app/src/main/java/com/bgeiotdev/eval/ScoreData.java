package com.bgeiotdev.eval;
import java.util.Date;

public class ScoreData {
    private int idScore;
    private String name;
    private int score;
    private Date when;


    public ScoreData(int idScore, String name, int score, Date when) {
        this.setIdScore( idScore );
        this.setName( name );
        this.setScore( score );
        this.setWhen( when );
    }

    public int getIdScore() {
        return idScore;
    }

    public void setIdScore(int idScore) {
        this.idScore = idScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    @Override
    public String toString() {
        return idScore + ": " + name + " -> " + score + " at " + when.toString();
    }

}
