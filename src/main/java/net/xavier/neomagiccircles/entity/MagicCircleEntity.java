package net.xavier.neomagiccircles.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.xavier.neomagiccircles.logger.ModLogger;

public class MagicCircleEntity extends Entity {
    private LivingEntity caster;

    public MagicCircleEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.noPhysics = true;
    }

    @Override
    public void tick() {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distSq) {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        ModLogger.error("MagicCircleEntity is client-side only");
        throw new UnsupportedOperationException();
    }

    public LivingEntity getCaster() {
        return this.caster;
    }

    public void setCaster(LivingEntity caster) {
        this.caster = caster;
    }
}
