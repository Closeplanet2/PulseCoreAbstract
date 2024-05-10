package com.pandapulsestudios.pulsecore.ScoreboardAPI.Object;

import com.pandapulsestudios.pulsecore.ChatAPI.API.MessageAPI;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class PulseScoreboardLines {
    private String scoreboardTitle = "";
    private List<PulseScoreboardData> scoreboardLines = new ArrayList<>();

    public PulseScoreboardLines(String scoreboardTitle, List<PulseScoreboardData> scoreboardLines){
        this.scoreboardTitle = scoreboardTitle;
        this.scoreboardLines = scoreboardLines;
    }

    public void CreateLine(Scoreboard scoreboard, String scoreboardID){
        var objective = scoreboard.registerNewObjective(scoreboardID, Criteria.DUMMY, scoreboardTitle);
        objective.setDisplayName(scoreboardTitle);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        var teamCount = 0;
        for(var scoreboardLine : scoreboardLines){
            scoreboardLine.CreateLine(scoreboard, scoreboardLines.indexOf(scoreboardLine), teamCount);
            teamCount += 1;
        }
    }

    public void UpdateLine(Scoreboard scoreboard){
        scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(scoreboardTitle);
        for(var scoreboardLine : scoreboardLines) scoreboardLine.UpdateLine(scoreboard, scoreboardLines.indexOf(scoreboardLine));
    }

    public static PulseScoreboardLinesBuilder builder(){ return new PulseScoreboardLinesBuilder(); }
    public static class PulseScoreboardLinesBuilder {
        private String scoreboardTitle = "Scoreboard Title";
        private List<PulseScoreboardData> scoreboardLines = new ArrayList<>();

        public PulseScoreboardLinesBuilder scoreboardTitle(Player player, String scoreboardTitle){
            this.scoreboardTitle = MessageAPI.FormatMessage(scoreboardTitle, true, true, player);
            return this;
        }

        public PulseScoreboardLinesBuilder addLine(PulseScoreboardData scoreboardLine){
            scoreboardLines.add(scoreboardLine);
            return this;
        }

        public PulseScoreboardLines build(){return new PulseScoreboardLines(scoreboardTitle, scoreboardLines);}
    }
}
