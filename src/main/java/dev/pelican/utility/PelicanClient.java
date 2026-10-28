package dev.pelican.utility;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.RenderLayer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashSet;
import java.util.Set;

public final class PelicanClient implements ClientModInitializer {
    public static final int PURPLE = 0xDDAF7CFF;
    public static final Set<Block> STORAGE_BLOCKS = new LinkedHashSet<>();
    public static boolean spawnerEsp = true;
    public static boolean storageEsp = true;
    public static boolean throughWalls = true;
    private static KeyBinding openGui;

    @Override public void onInitializeClient() {
        STORAGE_BLOCKS.addAll(Set.of(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
                Blocks.BARREL, Blocks.SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX,
                Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX,
                Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX,
                Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX,
                Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX,
                Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX));
        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pelican_utility.open_gui", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                KeyBinding.Category.MISC));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new PelicanScreen());
                else if (client.currentScreen instanceof PelicanScreen) client.setScreen(null);
            }
        });
        WorldRenderEvents.AFTER_ENTITIES.register(PelicanClient::renderEsp);
    }

    private static void renderEsp(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || context.matrixStack() == null || context.consumers() == null) return;
        Vec3d camera = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());
        if (throughWalls) RenderSystem.disableDepthTest();
        try {
            for (net.minecraft.block.entity.BlockEntity entity : client.world.getBlockEntities()) {
                BlockPos pos = entity.getPos();
                Block block = client.world.getBlockState(pos).getBlock();
                boolean target = (spawnerEsp && block == Blocks.SPAWNER) || (storageEsp && STORAGE_BLOCKS.contains(block));
                if (!target || client.player.squaredDistanceTo(Vec3d.ofCenter(pos)) > 96.0 * 96.0) continue;
                Box box = new Box(pos).expand(0.002).offset(-camera.x, -camera.y, -camera.z);
                WorldRenderer.drawBox(matrices, lines, box, 0.78f, 0.55f, 1.0f, 0.95f);
            }
        } finally {
            if (throughWalls) RenderSystem.enableDepthTest();
        }
    }
}
