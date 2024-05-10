package com.pandapulsestudios.pulsecore.BossBarAPI.Object;

import com.pandapulsestudios.pulsecore.BossBarAPI.Enum.PandaEntityBossValue;
import com.pandapulsestudios.pulsecore.PlayerAPI.API.PlayerActionAPI;
import com.pandapulsestudios.pulsecore.PlayerAPI.Enum.PlayerAction;
import com.pandapulsestudios.pulsecore.PulseCore;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PulseEntityBossBar {
    private String barID;
    private LivingEntity livingEntity;
    private BarData barData;
    private PandaEntityBossValue pandaEntityBossValue;
    private BarFlag[] barFlags;
    private BossBar bossBar;
    private BiConsumer<Double, LivingEntity> onTickConsumer;

    public void ResetBossBar(){
        this.bossBar = Bukkit.createBossBar(livingEntity.getCustomName(), barData.barColor(), barData.barStyle(), barFlags);
        this.bossBar.setProgress((Double) livingEntity.getHealth() / livingEntity.getMaxHealth());
        onTickConsumer.accept(this.bossBar.getProgress(), this.livingEntity);
    }

    public void AddPlayer(Player player){
        if(bossBar == null || !PlayerActionAPI.CanPlayerAction(PlayerAction.PlayerBossBar, player.getUniqueId()) || IsPlayerInBossBar(player)) return;
        bossBar.addPlayer(player);
    }

    public void DeleteBossBar(){
        RemoveAllPlayers();
        PulseCore.PandaEntityBossBars.remove(barID);
    }

    public void RemoveAllPlayers(){
        if(bossBar == null) return;
        for(var player : bossBar.getPlayers()) RemovePlayer(player);
    }

    public void RemovePlayer(Player player){
        if(bossBar == null || !IsPlayerInBossBar(player)) return;
        bossBar.removePlayer(player);
    }

    public boolean IsPlayerInBossBar(Player player){
        if(bossBar == null) return false;
        return bossBar.getPlayers().contains(player);
    }

    public void TickBossBar(){
        ResetBossBar();
        if(livingEntity == null || livingEntity.getHealth() <= 0) DeleteBossBar();
    }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private String barID = UUID.randomUUID().toString();
        private BarData barData = new BarData("TITLE", BarColor.BLUE, BarStyle.SEGMENTED_6, 0.5);
        private PandaEntityBossValue pandaEntityBossValue = PandaEntityBossValue.HEALTH;
        private List<Player> toAdd = new ArrayList<>();
        private BiConsumer<Double, LivingEntity> onTickConsumer;

        public Builder barID(String barID){
            this.barID = barID;
            return this;
        }

        public Builder barData(BarData barData){
            this.barData = barData;
            return this;
        }

        public Builder pandaEntityBossValue(PandaEntityBossValue pandaEntityBossValue){
            this.pandaEntityBossValue = pandaEntityBossValue;
            return this;
        }

        public Builder toAdd(Player... toAdd){
            this.toAdd.addAll(Arrays.asList(toAdd));
            return this;
        }

        public Builder onTickConsumer(BiConsumer<Double, LivingEntity> onTickConsumer){
            this.onTickConsumer = onTickConsumer;
            return this;
        }

        public PulseEntityBossBar build(LivingEntity livingEntity, BarFlag... barFlags){
            var storedBossBar = PulseCore.PandaEntityBossBars.getOrDefault(barID, null);
            if(storedBossBar != null){
                for(var player : toAdd) storedBossBar.AddPlayer(player);
                return storedBossBar;
            }

            var createdBossBar = new PulseEntityBossBar();
            createdBossBar.barID = barID;
            createdBossBar.livingEntity = livingEntity;
            createdBossBar.barData = barData;
            createdBossBar.barFlags = barFlags;
            createdBossBar.pandaEntityBossValue = pandaEntityBossValue;
            createdBossBar.onTickConsumer = onTickConsumer;
            for(var player : toAdd) createdBossBar.AddPlayer(player);
            PulseCore.PandaEntityBossBars.put(barID, createdBossBar);
            return createdBossBar;
        }
    }
}
