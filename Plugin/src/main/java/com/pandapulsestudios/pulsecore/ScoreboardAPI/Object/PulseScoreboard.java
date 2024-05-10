package com.pandapulsestudios.pulsecore.ScoreboardAPI.Object;

import com.pandapulsestudios.pulsecore.ActionBarAPI.Object.PulseActionBar;
import com.pandapulsestudios.pulsecore.PlayerAPI.API.PlayerActionAPI;
import com.pandapulsestudios.pulsecore.PlayerAPI.Enum.PlayerAction;
import com.pandapulsestudios.pulsecore.PulseCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PulseScoreboard {
    private Scoreboard scoreboard;
    private String scoreboardID = "Scoreboard";
    private List<PulseScoreboardLines> scoreboardData = new ArrayList<>();
    private int currentPosition = 0;
    private BiConsumer<Integer, PulseScoreboardLines> onTickConsumer;

    public void AddPlayer(Player player){
        if(!PlayerActionAPI.CanPlayerAction(PlayerAction.Scoreboard, player.getUniqueId())) return;
        player.setScoreboard(scoreboard);
    }

    public void RemoveAllPlayers(){
        for(var player : Bukkit.getOnlinePlayers()){
            RemovePlayer(player);
        }
    }

    public void RemovePlayer(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void TickScoreboard(){
        currentPosition += 1;
        if(currentPosition >= scoreboardData.size()) currentPosition = 0;
        var bd = scoreboardData.get(currentPosition);
        if(onTickConsumer != null) onTickConsumer.accept(currentPosition, bd);
        bd.UpdateLine(scoreboard);
    }

    public static PulseScoreboardBuilder builder(){ return new PulseScoreboardBuilder(); }
    public static class PulseScoreboardBuilder {
        private String scoreboardID = "Scoreboard";
        private List<Player>  scoreboardPlayers = new ArrayList<>();
        private List<PulseScoreboardLines>  scoreboardData = new ArrayList<>();
        private BiConsumer<Integer, PulseScoreboardLines> onTickConsumer;

        public PulseScoreboardBuilder scoreboardID(String scoreboardID){
            this.scoreboardID = scoreboardID;
            return this;
        }

        public PulseScoreboardBuilder scoreboardData(PulseScoreboardLines data, int numberOfFrames){
            for(var i = 0; i < numberOfFrames; i++) this.scoreboardData.add(data);
            return this;
        }

        public PulseScoreboardBuilder scoreboardPlayers(Player... players){
            scoreboardPlayers.addAll(Arrays.asList(players));
            return this;
        }

        public PulseScoreboardBuilder onTickConsumer(BiConsumer<Integer, PulseScoreboardLines> onTickConsumer){
            this.onTickConsumer = onTickConsumer;
            return this;
        }

        public PulseScoreboard create(){
            var storedScoreboard = PulseCore.PulseScoreboards.getOrDefault(scoreboardID, null);
            if(storedScoreboard != null){
                for(var player : scoreboardPlayers) storedScoreboard.AddPlayer(player);
                return storedScoreboard;
            }

            var pulseScoreboard = new PulseScoreboard();
            pulseScoreboard.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            pulseScoreboard.scoreboardID = scoreboardID;
            pulseScoreboard.scoreboardData.addAll(scoreboardData);
            pulseScoreboard.onTickConsumer = onTickConsumer;
            for(var player : scoreboardPlayers) pulseScoreboard.AddPlayer(player);
            if(!pulseScoreboard.scoreboardData.isEmpty()) pulseScoreboard.scoreboardData.get(0).CreateLine(pulseScoreboard.scoreboard, scoreboardID);
            PulseCore.PulseScoreboards.put(scoreboardID, pulseScoreboard);
            return pulseScoreboard;
        }
    }
}
