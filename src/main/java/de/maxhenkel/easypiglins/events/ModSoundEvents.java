package de.maxhenkel.easypiglins.events;

import de.maxhenkel.easypiglins.EasyPiglinsMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

public class ModSoundEvents {

    @SubscribeEvent
    public void onSound(PlayLevelSoundEvent.AtEntity event) {
        if (event.getSound() != null && event.getSource() != null && isPiglinSound(event.getSound().value()) && event.getSource().equals(SoundSource.BLOCKS)) {
            event.setNewVolume(EasyPiglinsMod.CLIENT_CONFIG.piglinVolume.get().floatValue());
        }
    }

    @SubscribeEvent
    public void onSound(PlayLevelSoundEvent.AtPosition event) {
        if (event.getSound() != null && event.getSound().value() != null && event.getSource() != null && isPiglinSound(event.getSound().value()) && event.getSource().equals(SoundSource.BLOCKS)) {
            event.setNewVolume(EasyPiglinsMod.CLIENT_CONFIG.piglinVolume.get().floatValue());
        }
    }

    private boolean isPiglinSound(SoundEvent event) {
        return event.equals(SoundEvents.PIGLIN_ADMIRING_ITEM)
                || event.equals(SoundEvents.PIGLIN_JEALOUS)
                || event.equals(SoundEvents.PIGLIN_AMBIENT)
                || event.equals(SoundEvents.PIGLIN_ANGRY)
                || event.equals(SoundEvents.PIGLIN_DEATH)
                || event.equals(SoundEvents.PIGLIN_CELEBRATE)
                || event.equals(SoundEvents.PIGLIN_HURT)
                || event.equals(SoundEvents.PIGLIN_RETREAT)
                || event.equals(SoundEvents.PIGLIN_STEP);
    }

}
