package com.pandapulsestudios.pulsecore.BossBarAPI.Object;

import com.pandapulsestudios.pulsecore.PlayerAPI.API.PlayerActionAPI;
import com.pandapulsestudios.pulsecore.PlayerAPI.Enum.PlayerAction;
import com.pandapulsestudios.pulsecore.PulseCore;
import com.pandapulsestudios.pulsecore.ScoreboardAPI.Object.PulseScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PulseBossBar {
    private String barID;
    private List<BarData> barData = new ArrayList<>();
    private BarFlag[] barFlags;
    private BossBar bossBar;
    private int currentCount = 0;
    private BiConsumer<Integer, BarData> onTickConsumer;

    public void ResetBossBar(){
        currentCount = 0;
        var firstBarData = this.barData.isEmpty() ? new BarData("TITLE", BarColor.BLUE, BarStyle.SEGMENTED_12, 0.5) : GetCurrentBossBarData();
        bossBar = Bukkit.createBossBar(firstBarData.barTitle(), firstBarData.barColor(), firstBarData.barStyle(), barFlags);
    }

    public void AddPlayer(Player player){
        if(bossBar == null || !PlayerActionAPI.CanPlayerAction(PlayerAction.PlayerBossBar, player.getUniqueId()) || bossBar.getPlayers().contains(player)) return;
        bossBar.addPlayer(player);
    }

    public void DeleteBossBar(){
        RemoveAllPlayers();
        PulseCore.PandaBossBars.remove(barID);
    }

    public void RemoveAllPlayers(){
        if(bossBar == null) return;
        for(var player : bossBar.getPlayers()) RemovePlayer(player);
    }

    public void RemovePlayer(Player player){
        if(bossBar == null || !bossBar.getPlayers().contains(player)) return;
        bossBar.removePlayer(player);
    }

    public void TickBossBar(){
        currentCount += 1;
        if(currentCount >= barData.size()) currentCount = 0;
        if(onTickConsumer != null) onTickConsumer.accept(currentCount,  GetCurrentBossBarData());
        GetCurrentBossBarData().DisplayBarData(bossBar);
    }

    private BarData GetCurrentBossBarData(){
        return barData.get(currentCount);
    }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private String barID = UUID.randomUUID().toString();
        private List<BarData> barData = new ArrayList<>();
        private List<Player> toAdd = new ArrayList<>();
        private BiConsumer<Integer, BarData> onTickConsumer;

        public Builder barID(String barID){
            this.barID = barID;
            return this;
        }

        public Builder barData(BarData barData, int numberOfFrames){
            for(var i = 0; i < numberOfFrames; i++) this.barData.add(barData);
            return this;
        }

        public Builder toAdd(Player... toAdd){
            this.toAdd.addAll(Arrays.asList(toAdd));
            return this;
        }

        public Builder onTickConsumer(BiConsumer<Integer, BarData> onTickConsumer){
            this.onTickConsumer = onTickConsumer;
            return this;
        }

        public PulseBossBar build(BarFlag... barFlags){
            var storedBossBar = PulseCore.PandaBossBars.getOrDefault(barID, null);
            if(storedBossBar != null){
                for(var player : toAdd) storedBossBar.AddPlayer(player);
                return storedBossBar;
            }

            var createdBossBar = new PulseBossBar();
            createdBossBar.barID = barID;
            createdBossBar.barData.addAll(barData);
            createdBossBar.barFlags = barFlags;
            createdBossBar.onTickConsumer = onTickConsumer;
            createdBossBar.ResetBossBar();
            for(var player : toAdd) createdBossBar.AddPlayer(player);
            PulseCore.PandaBossBars.put(barID, createdBossBar);
            return createdBossBar;
        }
    }
}
