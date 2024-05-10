package com.pandapulsestudios.pulsecore.EntityMaskAPI.Object;

import com.pandapulsestudios.pulsecore.EntityMaskAPI.Enum.EntityMaskType;
import com.pandapulsestudios.pulsecore.PulseCore;
import com.pandapulsestudios.pulsecore.VanishAPI.VanishAPI;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

//TODO add world to limit mask to world
//TODO add region to limit mask to region
//TODO same chunk
public class EntityMask {
    private Player player;
    private boolean deleteMaskOnNull;
    private double minDistance;
    private double maxDistance;
    private EntityMaskType entityMaskType;
    private ArrayList<EntityType> entityTypeExceptions = new ArrayList<>();
    private ArrayList<UUID> uuidExceptions = new ArrayList<>();;
    private ArrayList<Entity> lastFrameEntities = new ArrayList<>();

    public void AddToExceptions(ArrayList<EntityType> entityTypeExceptions){
        this.entityTypeExceptions.addAll(entityTypeExceptions);
    }

    public void AddToExceptionsUUID(ArrayList<UUID> uuidExceptions){
        this.uuidExceptions.addAll(uuidExceptions);
    }

    public boolean UpdateEntityMask(){
        if(player == null || !player.isOnline()) return deleteMaskOnNull;

        var newEntityStates = new ArrayList<Entity>();
        var playerWorld = player.getWorld();

        for(var entity : playerWorld.getEntities()){
            if(entityTypeExceptions.contains(entity.getType())) continue;
            if(uuidExceptions.contains(entity.getUniqueId())) continue;
            if(!IsCorrectForMaskType(entity)) continue;
            var distanceToEntity = player.getLocation().distance(entity.getLocation());
            if(distanceToEntity < minDistance || distanceToEntity > maxDistance) continue;
            newEntityStates.add(entity);
        }

        for(var entity : lastFrameEntities) VanishAPI.ShowTargetToViewer(entity, player);
        for(var entity : newEntityStates) VanishAPI.HideTargetFromViewer(entity, player);
        lastFrameEntities = newEntityStates;
        return false;
    }

    private boolean IsCorrectForMaskType(Entity entity){
        if(entityMaskType == EntityMaskType.Entity) return true;
        if(entityMaskType == EntityMaskType.LivingEntity && entity instanceof LivingEntity) return true;
        return entityMaskType == EntityMaskType.Player && entity instanceof Player;
    }

    public void CancelMask(){
        PulseCore.EntityMasks.remove(player.getUniqueId());
    }

    public static Builder builder(){return new Builder();}
    public static class Builder {
        private boolean deleteMaskOnNull = false;
        private double minDistance = 0;
        private double maxDistance = 5;
        private EntityMaskType entityMaskType = EntityMaskType.Player;
        private ArrayList<UUID> uuidExceptions = new ArrayList<>();
        private ArrayList<EntityType> entityTypeExceptions = new ArrayList<>();

        public Builder entityTypeExceptions(ArrayList<EntityType> entityTypeExceptions){
            this.entityTypeExceptions.addAll(entityTypeExceptions);
            return this;
        }

        public Builder uuidExceptions(ArrayList<UUID> uuidExceptions){
            this.uuidExceptions.addAll(uuidExceptions);
            return this;
        }

        public Builder deleteMaskOnNull(boolean deleteMaskOnNull){
            this.deleteMaskOnNull = deleteMaskOnNull;
            return this;
        }

        public Builder minDistance(double minDistance){
            this.minDistance = minDistance;
            return this;
        }

        public Builder maxDistance(double maxDistance){
            this.maxDistance = maxDistance;
            return this;
        }

        public Builder entityMaskType(EntityMaskType entityMaskType){
            this.entityMaskType = entityMaskType;
            return this;
        }

        public EntityMask CreateMask(Player player){
            var storedEntityMask = PulseCore.EntityMasks.getOrDefault(player.getUniqueId(), null);
            if(storedEntityMask != null){
                storedEntityMask.AddToExceptions(entityTypeExceptions);
                return storedEntityMask;
            }

            var createdEntityMask = new EntityMask();
            createdEntityMask.player = player;
            createdEntityMask.deleteMaskOnNull = deleteMaskOnNull;
            createdEntityMask.maxDistance = maxDistance;
            createdEntityMask.minDistance = minDistance;
            createdEntityMask.entityMaskType = entityMaskType;
            createdEntityMask.AddToExceptions(entityTypeExceptions);
            createdEntityMask.AddToExceptionsUUID(uuidExceptions);
            PulseCore.EntityMasks.put(player.getUniqueId(), createdEntityMask);
            return createdEntityMask;
        }
    }


}
