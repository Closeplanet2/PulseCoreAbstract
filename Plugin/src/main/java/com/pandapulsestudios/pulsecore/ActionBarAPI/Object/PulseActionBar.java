package com.pandapulsestudios.pulsecore.ActionBarAPI.Object;

import com.pandapulsestudios.api.Interface.ActionBarDataNMS;
import com.pandapulsestudios.pulsecore.BossBarAPI.Object.BarData;
import com.pandapulsestudios.pulsecore.BossBarAPI.Object.PulseBossBar;
import com.pandapulsestudios.pulsecore.ChatAPI.API.MessageAPI;
import com.pandapulsestudios.pulsecore.ChatAPI.Object.ChatBuilderAPI;
import com.pandapulsestudios.pulsecore.PlayerAPI.API.PlayerActionAPI;
import com.pandapulsestudios.pulsecore.PlayerAPI.Enum.PlayerAction;
import com.pandapulsestudios.pulsecore.PulseCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PulseActionBar {

    public static ActionBarDataNMS ReturnActionBarData(Player player, String message){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(".") + 1).toLowerCase();
        try{
            final Class<?> clazz = Class.forName("com.pandapulsestudios." + version + ".ActionBarData");
            ChatBuilderAPI.chatBuilder().SendMessage(ChatColor.RED + "Loading support for " + version, true);
            var actionBarNMS = (ActionBarDataNMS) clazz.getConstructor().newInstance();
            actionBarNMS.PassMessage(MessageAPI.FormatMessage(message, true, true, player));
            return actionBarNMS;
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String barID;
    private List<ActionBarDataNMS> barData = new ArrayList<>();
    private List<Player> barPlayers = new ArrayList<>();
    private int currentCount = 0;
    private BiConsumer<Integer, String> onTickConsumer;

    public void AddPlayer(Player player){
        if(barPlayers.contains(player) ||!PlayerActionAPI.CanPlayerAction(PlayerAction.ActionBar, player.getUniqueId())) return;
        barPlayers.add(player);
    }

    public void DeleteActionBar(){
        RemoveAllPlayers();
        PulseCore.PulseActionBars.remove(barID);
    }

    public void RemoveAllPlayers(){ for(var player : barPlayers) RemovePlayer(player); }

    public void RemovePlayer(Player player){ barPlayers.remove(player); }

    public boolean IsPlayerInBossBar(Player player){ return barPlayers.contains(player); }

    public void TickBossBar(){
        currentCount += 1;
        if(currentCount >= barData.size()) currentCount = 0;
        var bd = barData.get(currentCount);
        if(onTickConsumer != null) onTickConsumer.accept(currentCount, bd.ReturnMessage());
        for(var player : barPlayers) bd.DisplayActionBar(player);
    }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private String barID = UUID.randomUUID().toString();
        private List<ActionBarDataNMS> barData = new ArrayList<>();
        private List<Player> barPlayers = new ArrayList<>();
        private BiConsumer<Integer, String> onTickConsumer;

        public Builder barID(String barID){
            this.barID = barID;
            return this;
        }

        public Builder barData(ActionBarDataNMS barData, int numberOfFrames){
            for(var i = 0; i < numberOfFrames; i++) this.barData.add(barData);
            return this;
        }

        public Builder barPlayers(Player... toAdd){
            this.barPlayers.addAll(Arrays.asList(toAdd));
            return this;
        }

        public Builder onTickConsumer(BiConsumer<Integer, String> onTickConsumer){
            this.onTickConsumer = onTickConsumer;
            return this;
        }

        public PulseActionBar build(){
            var storedBossBar = PulseCore.PulseActionBars.getOrDefault(barID, null);
            if(storedBossBar != null){
                for(var player : barPlayers) storedBossBar.AddPlayer(player);
                return storedBossBar;
            }
            var createdBossBar = new PulseActionBar();
            createdBossBar.barID = barID;
            createdBossBar.barData.addAll(barData);
            createdBossBar.onTickConsumer = onTickConsumer;
            createdBossBar.barPlayers.addAll(barPlayers);
            PulseCore.PulseActionBars.put(createdBossBar.barID, createdBossBar);
            return createdBossBar;
        }
    }

}
