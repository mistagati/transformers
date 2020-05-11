package com.surpassplus.transformers.model;

import java.util.ArrayList;

public class War {

    private ArrayList<Battle> battles;
    private Integer autobotWinTally;
    private Integer decepticonWinTally;

    private Winner winner;

    public War(ArrayList<Battle> battles) {
        this.battles = battles;
        this.autobotWinTally = 0;
        this.decepticonWinTally = 0;
        this.winner = Winner.NONE;
    }

    public void fightWar() {
        for (Battle battle: battles) {
            battle.fight();
            if (battle.getWinner() == Winner.AUTOBOT) {
                autobotWinTally++;
            } else if (battle.getWinner() == Winner.DECEPTICON) {
                decepticonWinTally++;
            }
        }
        if (autobotWinTally > decepticonWinTally) {
            winner = Winner.AUTOBOT;
        } else if (decepticonWinTally > autobotWinTally) {
            winner = Winner.DECEPTICON;
        } else if (decepticonWinTally == autobotWinTally) {
            winner = Winner.TIE;
        }
    }

    public Winner getWinner() {
        return this.winner;
    }

    public Integer getAutobotWinTally() {
        return this.autobotWinTally;
    }

    public Integer getDecepticonWinTally() {
        return this.decepticonWinTally;
    }

    public ArrayList<Battle> getBattles() {
        return this.battles;
    }
}
