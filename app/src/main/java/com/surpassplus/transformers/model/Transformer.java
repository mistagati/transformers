package com.surpassplus.transformers.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Transformer implements Comparable<Transformer> {


    @SerializedName("courage")
    private Integer courage;
    @SerializedName("endurance")
    private Integer endurance;
    @SerializedName("firepower")
    private Integer firepower;
    @SerializedName("id")
    private String id;
    @SerializedName("intelligence")
    private Integer intelligence;
    @SerializedName("name")
    private String name;
    @SerializedName("rank")
    private Integer rank;
    @SerializedName("skill")
    private Integer skill;
    @SerializedName("speed")
    private Integer speed;
    @SerializedName("strength")
    private Integer strength;
    @SerializedName("team")
    private String team;
    @SerializedName("team_icon")
    private String teamIcon;

    public Transformer() {
        this.id = "";
        this.courage = 1;
        this.endurance = 1;
        this.firepower = 1;
        this.intelligence = 1;
        this.rank = 1;
        this.skill = 1;
        this.speed = 1;
        this.strength = 1;
        this.team = "A";
    }

    public Integer getCourage() {
        return courage;
    }

    public void setCourage(Integer courage) {
        this.courage = courage;
    }

    public Integer getEndurance() {
        return endurance;
    }

    public void setEndurance(Integer endurance) {
        this.endurance = endurance;
    }

    public void setFirepower(Integer firepower) {
        this.firepower = firepower;
    }

    public Integer getFirepower() {
        return firepower;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Integer getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(Integer intelligence) {
        this.intelligence = intelligence;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setSkill(Integer skill) {
        this.skill = skill;
    }

    public Integer getSkill() {
        return skill;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Integer getStrength() {
        return strength;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamIcon() {
        return teamIcon;
    }

    public void setTeamIcon(String teamIcon) {
        this.teamIcon = teamIcon;
    }

    public Integer getOverallRating() {
        return this.strength + this.intelligence + this.speed +  this.endurance + this.firepower;
    }
    @Override
    public int compareTo(@NonNull Transformer compare) {
        return this.getRank() < compare.getRank() ? 1 : -1;
        //return 0;
        /*
        if (compare.getName().equals(this.name) &&
                compare.getName().equals(this.picture) &&
                compare.getScore().equals(this.score)){
            return 0;
        } else {
            return this.getScore() < compare.getScore() ? 1 : -1;
        }*/
    }

}
