package bagu_chan.bagus_lib.client.dialog;

import bagu_chan.bagus_lib.util.DialogHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DialogType {
    @Nullable
    protected DialogHandler.DrawString dialogue;
    @Nullable
    protected MutableComponent dialogueBase;
    @Nullable
    protected Holder<SoundEvent> soundEvent;

    protected float scaleX = 1;
    protected float scaleY = 1;
    protected int posX = 1;
    protected int posY = 1;
    protected int renderDialogY = 16;

    public void render(GuiGraphics guiGraphics, PoseStack poseStack, float f, float tickCount) {
    }

    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack, float f, float tickCount) {

        Font font = Minecraft.getInstance().font;
        float g = (float) tickCount + f;
        if (this.dialogue == null && this.dialogueBase != null) {
            MutableComponent component = dialogueBase;
            this.dialogue = beginString(guiGraphics, g, 2.0, font, component.getString(), 0xFFFFFF, guiGraphics.guiWidth() - 72);
        }


        if (this.dialogue != null && this.dialogue.draw(g, 72, renderDialogY)) {
            if (this.soundEvent != null) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent.value(), 1.0F, 0.75F));
            }
        }
    }

    public DialogHandler.DrawString beginString(GuiGraphics guiGraphics, double d, double e, Font font, String string2, int i, int j2) {
        List<FormattedText> list = font.getSplitter().splitLines(string2, j2, Style.EMPTY);
        String string22 = list.stream().map(FormattedText::getString).collect(Collectors.joining("\n"));
        return new DialogHandler.DrawString(d, e, string22, (string, j, k) -> {
            String[] strings = string.split("\\r?\\n");
            int l = k;
            for (String string3 : strings) {
                guiGraphics.drawString(font, string3, j, l, i);
                l += font.lineHeight + 4;
            }
        });
    }

    public CompoundTag writeTag() {
        CompoundTag tag = new CompoundTag();
        if (this.dialogueBase != null) {
            tag.putString("message", this.dialogueBase.getString());
        }
        tag.putFloat("scaleX", this.scaleX);
        tag.putFloat("scaleY", this.scaleY);
        tag.putInt("posX", this.posX);
        tag.putInt("posY", this.posY);
        tag.putInt("dialogY", this.renderDialogY);
        if (this.soundEvent != null) {
            tag.putString("SoundEvent", BuiltInRegistries.SOUND_EVENT.getKey(this.soundEvent.value()).toString());
        }
        return tag;
    }

    public void readTag(CompoundTag tag) {
        if (tag.contains("message")) {
            this.dialogueBase = Component.literal(tag.getString("message"));
        }
        if (tag.contains("scaleX")) {
            this.scaleX = tag.getFloat("scaleX");
        }
        if (tag.contains("scaleY")) {
            this.scaleY = tag.getFloat("scaleY");
        }
        if (tag.contains("posX")) {
            this.posX = tag.getInt("posX");
        }
        if (tag.contains("posY")) {
            this.posY = tag.getInt("posY");
        }
        if (tag.contains("dialogY")) {
            this.renderDialogY = tag.getInt("dialogY");
        }
        if (tag.contains("SoundEvent")) {
            Optional<Holder.Reference<SoundEvent>> soundEventHolder = BuiltInRegistries.SOUND_EVENT
                    .getHolder(new ResourceLocation(tag.getString("SoundEvent")));
            soundEventHolder.ifPresent(soundEventReference -> this.soundEvent = soundEventReference);
        }
    }

    public void setDialogueBase(@Nullable MutableComponent dialogueBase) {
        this.dialogue = null;
        this.dialogueBase = dialogueBase;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void setPos(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void setSoundEvent(@Nullable Holder<SoundEvent> soundEvent) {
        this.soundEvent = soundEvent;
    }

    public void setRenderDialogY(int renderDialogY) {
        this.renderDialogY = renderDialogY;
    }
}
