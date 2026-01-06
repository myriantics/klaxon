package net.myriantics.klaxon.resource;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.minecraft.splashes.SplashTextResourceSupplierAccessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class KlaxonSplashTextResourceSupplier implements SimpleSynchronousResourceReloadListener {

    private static final Identifier PATH = KlaxonCommon.locate("texts/splashes.txt");
    private static final Identifier ID = KlaxonCommon.locate("splashes");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        manager.getResource(PATH).ifPresent(resource -> {
            try {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                final List<String> klaxonSplashes = bufferedReader.lines().map(String::trim).filter((string) -> string.hashCode() != 125780783).toList();

                // eye love accessors
                List<String> splashes = ((SplashTextResourceSupplierAccessor) MinecraftClient.getInstance().getSplashTextLoader()).klaxon$getSplashTexts();
                splashes.addAll(klaxonSplashes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
