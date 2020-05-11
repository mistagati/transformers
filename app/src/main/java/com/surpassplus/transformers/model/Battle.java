package com.surpassplus.transformers.model;

import android.util.Log;

import java.util.ArrayList;

public class Battle {

    private Transformer autobot;
    private Transformer decepticon;

    private ArrayList<String> invalidMatchErrors;
    private ArrayList<String> winnerStats;

    private Winner winner;
    private Integer number;

    private String TAG = "BATTLE";

    public Battle(Integer number, Transformer autobot, Transformer decepticon) {
        this.autobot = autobot;
        this.decepticon = decepticon;
        this.invalidMatchErrors = new ArrayList<>();
        this.winnerStats = new ArrayList<>();
        this.winner = Winner.NONE;
        this.number = number;
    }

    private boolean isOpposingTeams() {
        if (this.autobot.getTeam().trim().equals("A") && this.decepticon.getTeam().trim().equals("D"))
            return true;
        else
            return false;
    }

    private boolean isBattlingSelf() {
        if (this.autobot.getId().trim().equals(this.decepticon.getId().trim()))
            return true;
        else
            return false;
    }

    private boolean isValidMatch() {
        this.invalidMatchErrors.clear();
        if (isBattlingSelf()) {
            invalidMatchErrors.add("It appears the opponent is fighting himself (same id)");
        }
        if (!isOpposingTeams()) {
            invalidMatchErrors.add("The opponents cannot be on the same team");
        }
        if (invalidMatchErrors.size() > 0) {
            winner = Winner.INVALID_MATCH;
            return false;
        }
        else
            return true;
    }

    // a beats b if a has 4 or more courage points than b
    private boolean beatsByCourage4(Transformer a, Transformer b) {
        if ((a.getCourage() - b.getCourage()) >= 4)
            return true;
        else
            return false;
    }
    // a beats b if a has 3 or more strengths points than b
    private boolean beatsByStrength3(Transformer a, Transformer b) {
        if ((a.getStrength() - b.getStrength()) >= 3)
            return true;
        else
            return false;
    }

    // a beats b if a has 3 or more skill points than b
    private boolean beatsBySkill3(Transformer a, Transformer b) {
        if ((a.getSkill() - b.getSkill()) >= 3)
            return true;
        else
            return false;
    }

    // a beats b if a has more overall rating b
    private boolean beatsByOverallRating(Transformer a, Transformer b) {
        if ((a.getOverallRating() > b.getOverallRating()))
            return true;
        else
            return false;
    }

    // a wins if he's Optimus Prime
    private boolean beatsByBeingOptimusPrime(Transformer a) {
        if (a.getName().trim().equals("Optimus Prime"))
            return true;
        else
            return false;
    }

    // a wins if he's Predaking
    private boolean beatsByBeingPredaking(Transformer a) {
        if (a.getName().trim().equals("Predaking"))
            return true;
        else
            return false;
    }

    private void fightByCourageAndStrength() {
        if (beatsByCourage4(this.autobot, this.decepticon) && beatsByStrength3(this.autobot, this.decepticon)) {
            winner = Winner.AUTOBOT;
            winnerStats.add(this.autobot.getName() + " wins by Courage >= 4 and Strength >= 3");
        } else if (beatsByCourage4(this.decepticon, this.autobot) && beatsByStrength3(this.decepticon, this.autobot)) {
            winner = Winner.DECEPTICON;
            winnerStats.add(this.decepticon.getName() + " wins by Courage >= 4 and Strength >= 3");
        }
    }

    private void fightBySkill() {
        if (beatsBySkill3(this.autobot, this.decepticon)) {
            winner = Winner.AUTOBOT;
            winnerStats.add(this.autobot.getName() + " wins by Skill >= 3");
        } else if (beatsBySkill3(this.decepticon, this.autobot)) {
            winner = Winner.DECEPTICON;
            winnerStats.add(this.decepticon.getName() + " wins by Skill >= 3");
        }
    }

    private void fightByOverallRating() {
        if (beatsByOverallRating(this.autobot, this.decepticon)) {
            winner = Winner.AUTOBOT;
            winnerStats.add(this.autobot.getName() + " wins by > Overall Rating");
        } else if (beatsByOverallRating(this.decepticon, this.autobot)) {
            winner = Winner.DECEPTICON;
            winnerStats.add(this.decepticon.getName() + " wins by > Overall Rating");
        }
    }

    private void fightBySpecialTransformer() {
        Winner win = Winner.NONE;
        if (beatsByBeingOptimusPrime(this.autobot) || beatsByBeingPredaking(this.autobot)) {
            win = Winner.AUTOBOT;
        }
        if (beatsByBeingOptimusPrime(this.decepticon) || beatsByBeingPredaking(this.decepticon)) {
            if (win == Winner.AUTOBOT) {
                // no one wins
                win = Winner.ALLDESTROYED;
            } else {
                win = Winner.DECEPTICON;
            }
        }
        winner = win;
        if (winner == Winner.AUTOBOT) {
            winnerStats.add(this.autobot.getName() + " wins by Special Name");
        } else if (winner == Winner.DECEPTICON) {
            winnerStats.add(this.decepticon.getName() + " wins by Special Name");
        } else if (winner == Winner.ALLDESTROYED) {
            winnerStats.add("Both opponents were destroyed for being duplicates");
        }
    }

    private boolean winnerReached() {
        if (this.winner != Winner.NONE)
            return true;
        else
            return false;
    }

    public Winner getWinner() {
        return this.winner;
    }

    public String getWinnerText() {
        if (this.winner == Winner.AUTOBOT) {
            return "AUTOBOT";
        } else if (this.winner == Winner.DECEPTICON) {
            return "DECEPTION";
        } else if (this.winner == Winner.TIE) {
            return "TIE";
        } else if (this.winner == Winner.INVALID_MATCH) {
            return "INVALID MATCH";
        } else if (this.winner == Winner.ALLDESTROYED) {
            return "ALL DESTROYED";
        } else if (this.winner == Winner.NONE) {
            return "NONE";
        } else
            return "UNKNOWN";
    }

    public void fight() {
        Log.i(TAG, this.autobot.getName() + " vs. " + this.decepticon.getName());
        if (isValidMatch()) {

            fightBySpecialTransformer();

            if (!winnerReached())
                fightByCourageAndStrength();

            if (!winnerReached())
                fightBySkill();

            if (!winnerReached())
                fightByOverallRating();

            if (!winnerReached()) {
                winner = Winner.TIE;
                winnerStats.add("A tie was determined, so both have opponents are destroyed");
            }
        }
        Log.i(TAG, "The winner is " + getWinnerText());
        if (invalidMatchErrors.size() > 0) {
            for (int i=0; i<invalidMatchErrors.size(); i++) {
                Log.e(TAG, "Invalid Match Error: " + invalidMatchErrors.get(i));
            }
        }
    }


    public Transformer getAutobot() {
        return this.autobot;
    }

    public Transformer getDecepticon() {
        return this.decepticon;
    }

    public Integer getBattleNumber() {
        return this.number;
    }

    public ArrayList<String> getWinnerStats() {
        return winnerStats;
    }

    public ArrayList<String> getInvalidMatchErrors() {
        return invalidMatchErrors;
    }
}
