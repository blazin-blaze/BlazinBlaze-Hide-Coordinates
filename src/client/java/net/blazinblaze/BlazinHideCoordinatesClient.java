package net.blazinblaze;

import net.blazinblaze.config.CoordinatesYAML;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.PlayerInput;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

public class BlazinHideCoordinatesClient implements ClientModInitializer {

	public static CoordinatesYAML config;
	public static KeyBinding binding;

	@Override
	public void onInitializeClient() {
		File configFileYaml = new File(FabricLoader.getInstance().getConfigDir().resolve("blazin-coordinates-config.yaml").toString());
		config = new CoordinatesYAML(configFileYaml);
		config.load();

		binding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.blazin-hide-coordinates.f6-key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, "key.category.blazin-hide-cooordinates.main"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (binding.wasPressed()) {
				if(config.getCoordinateVal(CoordinatesYAML.shouldHideCoordinates) instanceof Boolean value) {
					boolean newValue = !value;
					config.setCoordinateVal(CoordinatesYAML.shouldHideCoordinates, newValue);
					config.save();

					if(value) {
						client.player.sendMessage(Text.literal(Formatting.GREEN + "[Blazin-Hide-Coordinates] Coordinates revealed!"), false);
					}else {
						client.player.sendMessage(Text.literal(Formatting.RED + "[Blazin-Hide-Coordinates] Coordinates hidden!"), false);
					}
				}
			}
		});
	}
}