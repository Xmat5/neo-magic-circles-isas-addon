package net.xavier.neomagiccircles.types;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntitySnapshot {
    public final float eyeHeight;

    public double x;
    public double y;
    public double z;

    public double xo;
    public double yo;
    public double zo;

    public float xRotO;
    public float yRotO;

    public float yRot;
    public float xRot;

    public ResourceKey<Level> levelKey;

    public long gameTick;

    public EntitySnapshot(LivingEntity caster) {
        this.capture(caster);
        this.eyeHeight = caster.getEyeHeight();
    }

    public void capture(LivingEntity entity) {
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.yRot = entity.getYRot();
        this.xRot = entity.getXRot();
        this.xo = entity.xo;
        this.yo = entity.yo;
        this.zo = entity.zo;
        this.xRotO = entity.xRotO;
        this.yRotO = entity.yRotO;
        this.levelKey = entity.level().dimension();
        this.gameTick = entity.level().getGameTime();
    }
}
