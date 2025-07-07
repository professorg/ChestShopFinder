package dev.kosdt.chestshopfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

import static dev.kosdt.chestshopfinder.ChestShopFinder.LOGGER;

public class ChestShopUtils {

    public static List<SignBlockEntity> getAllSigns() {
        ClientWorld world = MinecraftClient.getInstance().world;
        AtomicReferenceArray<WorldChunk> chunks = world.getChunkManager().chunks.chunks;

        return IntStream.range(0, chunks.length())
                .mapToObj(chunks::get)
                .filter(Objects::nonNull)
                .flatMap(wc -> wc.getBlockEntities().values().stream())
                .filter(SignBlockEntity.class::isInstance)
                .map(SignBlockEntity.class::cast)
                .toList();
    }

    public static List<ChestShop> getAllChestShops() {
        return getAllSigns().stream()
                .filter(ChestShop::isAChestShopPreliminary)
                .map(ChestShop::new)
                .filter(ChestShop::validShop)
                .collect(Collectors.toList());
    }

}