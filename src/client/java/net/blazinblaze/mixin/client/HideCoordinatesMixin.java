package net.blazinblaze.mixin.client;

import net.blazinblaze.BlazinHideCoordinates;
import net.blazinblaze.BlazinHideCoordinatesClient;
import net.blazinblaze.config.CoordinatesYAML;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(DebugHud.class)
public class HideCoordinatesMixin {
    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    public void hideCoordinatesLeft(CallbackInfoReturnable<List<String>> cir) {
        List<String> list = cir.getReturnValue();
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        //Fix wrong coordinate for Y (it appears as the X/Z coordinate instead; wrong number for matcher.find)
        //Add F6 disabling
        //See what ChunkSectionPos means
        CoordinatesYAML config = BlazinHideCoordinatesClient.config;
        if(config != null && config.getShouldHide(CoordinatesYAML.shouldHideCoordinates) instanceof Boolean bool && bool) {
            for(String str : list) {
                if(str.startsWith("XYZ:")) {
                    Matcher matcher = pattern.matcher(str);
                    ArrayList<Float> floats = new ArrayList<Float>();
                    for(int i = 0; i < 3 & matcher.find(); i++) {
                        try {
                            floats.add(Float.parseFloat(matcher.group()));
                        }catch(NumberFormatException e) {
                            floats.add(0F);
                            BlazinHideCoordinates.LOGGER.warn("Error whilst gettng floats for XYZ: ", e);
                        }
                    }

                    if(floats.size() >= 2) {
                        int index = list.indexOf(str);
                        list.set(index, String.format(Locale.ROOT, "XYZ: ??? / %.5f / ???", floats.get(1)));
                    }
                }else if(str.startsWith("Block:")) {
                    Matcher matcher = pattern.matcher(str);

                    ArrayList<Integer> ints = new ArrayList<Integer>();
                    for(int i = 0; i < 6 & matcher.find(); i++) {
                        try {
                            ints.add(Integer.parseInt(matcher.group()));
                        }catch(NumberFormatException e) {
                            ints.add(0);
                            BlazinHideCoordinates.LOGGER.warn("Error whilst gettng floats for XYZ: ", e);
                        }
                    }

                    if(ints.size() >= 4) {
                        int index = list.indexOf(str);
                        list.set(index, String.format(Locale.ROOT, "Block: ??? / %d / ??? [%d %d %d]", ints.get(1), ints.get(3), ints.get(4), ints.get(5)));
                    }
                }else if(str.startsWith("Chunk:")) {
                    ArrayList<Integer> ints = getInts(str);

                    if(ints.size() >= 2) {
                        int index = list.indexOf(str);
                        list.set(index, String.format(Locale.ROOT, "Chunk: ??? %d ??? [??? ??? in r.???.???.mca]", ints.get(1)));
                    }
                }
            }
            if(BlazinHideCoordinatesClient.binding != null && BlazinHideCoordinatesClient.binding.getBoundKeyLocalizedText() != null) {
                if(BlazinHideCoordinatesClient.binding.isUnbound()) {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Unbound key set to toggle", Formatting.RED));
                }else {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Press [%s] to toggle", Formatting.RED, BlazinHideCoordinatesClient.binding.getBoundKeyLocalizedText().getString()));
                }
            }else {
                if(BlazinHideCoordinatesClient.binding != null && BlazinHideCoordinatesClient.binding.isDefault()) {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Press [F6] to toggle", Formatting.RED));
                }else {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Unknown key to toggle", Formatting.RED));
                }
            }
        }else {
            if(BlazinHideCoordinatesClient.binding != null && BlazinHideCoordinatesClient.binding.getBoundKeyLocalizedText() != null) {
                if(BlazinHideCoordinatesClient.binding.isUnbound()) {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Unbound key set to toggle", Formatting.GREEN));
                }else {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Press [%s] to toggle", Formatting.GREEN, BlazinHideCoordinatesClient.binding.getBoundKeyLocalizedText().getString()));
                }
            }else {
                if(BlazinHideCoordinatesClient.binding != null && BlazinHideCoordinatesClient.binding.isDefault()) {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Press [F6] to toggle", Formatting.GREEN));
                }else {
                    list.addLast(String.format("%sBlazin Hide Coordinates installed - Unknown key to toggle", Formatting.GREEN));
                }
            }
        }

        cir.setReturnValue(list);
    }

    @Inject(method = "getRightText", at = @At("RETURN"), cancellable = true)
    public void hideCoordinatesRight(CallbackInfoReturnable<List<String>> cir) {
        List<String> list = cir.getReturnValue();
        String underline = String.valueOf(Formatting.UNDERLINE);

        CoordinatesYAML config = BlazinHideCoordinatesClient.config;
        if(config != null && config.getShouldHide(CoordinatesYAML.shouldHideCoordinates) instanceof Boolean bool && bool) {
            for(String str : list) {
                if(str.startsWith(underline + "Targeted Block:")) {
                    ArrayList<Integer> ints = getInts(str);

                    if(ints.size() >= 2) {
                        int index = list.indexOf(str);
                        list.set(index, underline + "Targeted Block: " + "???" + ", " + String.valueOf(ints.get(1)) + ", " + "???");
                    }
                }else if(str.startsWith(underline + "Targeted Fluid:")) {
                    ArrayList<Integer> ints = getInts(str);

                    if(ints.size() >= 2) {
                        int index = list.indexOf(str);
                        list.set(index, underline + "Targeted Fluid: " + "???" + ", " + String.valueOf(ints.get(1)) + ", " + "???");
                    }
                }
            }
        }
        cir.setReturnValue(list);
    }

    @Unique
    private ArrayList<Integer> getInts(String str) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(str);

        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < 3 & matcher.find(); i++) {
            try {
                list.add(Integer.parseInt(matcher.group()));
            }catch(NumberFormatException e) {
                list.add(0);
                BlazinHideCoordinates.LOGGER.warn("Error whilst gettng floats for XYZ: ", e);
            }
        }
        return list;
    }
}
