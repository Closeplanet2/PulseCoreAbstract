package com.pandapulsestudios.pulsecore._Common.Events.Entity;

import com.pandapulsestudios.pulsecore.EnchantmentAPI.API.EnchantmentAPI;
import com.pandapulsestudios.pulsecore.EventsAPI.API.EventAPI;
import com.pandapulsestudios.pulsecore.InventoryAPI.API.InventoryAPI;
import com.pandapulsestudios.pulsecore.ItemsAPI.API.ItemStackAPI;
import com.pandapulsestudios.pulsecore.JavaAPI.Interface.PulseAutoRegister;
import com.pandapulsestudios.pulsecore.LocationAPI.API.LocationAPI;
import com.pandapulsestudios.pulsecore.NBTAPI.API.NBTAPI;
import com.pandapulsestudios.pulsecore.PlayerAPI.API.PlayerActionAPI;
import com.pandapulsestudios.pulsecore.PlayerAPI.Enum.PlayerAction;
import com.pandapulsestudios.pulsecore.PulseCore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

@PulseAutoRegister
public class EntityCombustByEntity implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void OnBlockBreak(EntityCombustByEntityEvent event){
        Handler(event.getEntity(), event, false);
        Handler(event.getCombuster(), event, true);
    }

    private void Handler(Entity entity, EntityCombustByEntityEvent event, boolean isAttacker){
        if(!(entity instanceof LivingEntity livingEntity)) return;
        var isEntityPlayer = livingEntity instanceof Player;

        for(var pulseCoreEvent : PulseCore.PulseCoreEvents.values()){
            if(EventAPI.CanDoEvent(isEntityPlayer ? (Player) event.getEntity() : null, event.getEntity().getLocation(), pulseCoreEvent)){
                var state = pulseCoreEvent.EntityCombustByEntityEvent(event, isAttacker);
                if(!event.isCancelled()) event.setCancelled(state);
            }
        }

        for(var pulseLocation :  LocationAPI.ReturnAllPulseLocations(livingEntity.getLocation(), true)){
            var state = pulseLocation.EntityCombustByEntityEvent(event, livingEntity.getLocation(), isAttacker);
            if(!event.isCancelled()) event.setCancelled(state);
        }

        var state1 = PlayerActionAPI.CanPlayerAction(PlayerAction.EntityCombustByEntity, event.getEntity().getUniqueId());
        if(!event.isCancelled()) event.setCancelled(!state1);

        var playerWorld = livingEntity.getWorld();
        if(PulseCore.PlayerActionLock.containsKey(playerWorld) && isEntityPlayer){
            var state = PulseCore.PlayerActionLock.get(playerWorld).contains(PlayerAction.EntityCombustByEntity);
            if(!event.isCancelled()) event.setCancelled(state);
        }

        for(var pulseLocation : LocationAPI.ReturnAllPulseLocations(livingEntity.getLocation(), true)){
            var state = pulseLocation.EntityCombustByEntityEvent(event, livingEntity.getLocation(), isAttacker);
            if(!event.isCancelled()) event.setCancelled(state);
        }

        var playerInventoryItems = InventoryAPI.ReturnALlItemsWithLocation(livingEntity);
        for(var itemStack : playerInventoryItems.keySet()){
            if(itemStack == null || itemStack.getItemMeta() == null) continue;

            for(var nbtListener : PulseCore.PulseNBTListeners.values()){
                if(!NBTAPI.DoesItemStackContainNBTTags(itemStack, nbtListener.BlockHasTags())) continue;
                var state = nbtListener.EntityCombustByEntityEvent(event, itemStack, NBTAPI.GetAll(itemStack), livingEntity, isAttacker);
                if(!event.isCancelled()) event.setCancelled(state);
            }

            for(var pulseEnchantment : EnchantmentAPI.ReturnAllCustomEnchantmentsFromItem(itemStack)){
                var state = pulseEnchantment.EntityCombustByEntityEvent(event, itemStack, playerInventoryItems.get(itemStack), isAttacker);
                if(!event.isCancelled()) event.setCancelled(state);
            }

            var pulseItemStack = ItemStackAPI.ReturnPulseItem(itemStack);
            if(pulseItemStack != null){
                var state = pulseItemStack.EntityCombustByEntityEvent(event, itemStack, playerInventoryItems.get(itemStack), isAttacker);
                if(!event.isCancelled()) event.setCancelled(state);
            }
        }
    }

}
