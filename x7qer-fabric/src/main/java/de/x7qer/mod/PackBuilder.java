package de.x7qer.mod;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/** Small client-side resource-pack combiner. Existing source packs are only read. */
final class PackBuilder {
    private static final Map<String, String> CHOICES = new LinkedHashMap<>();
    private static final Map<String, Path> FOUND = new LinkedHashMap<>();
    private PackBuilder() {}

    static List<String> findPackNames(Path root) {
        FOUND.clear();
        if (!Files.isDirectory(root)) return List.of();
        try (var items = Files.list(root)) {
            items.filter(p -> Files.isDirectory(p) || p.toString().toLowerCase(Locale.ROOT).endsWith(".zip") || p.toString().toLowerCase(Locale.ROOT).endsWith(".zipx") || p.toString().toLowerCase(Locale.ROOT).endsWith(".mcpack"))
                .forEach(p -> FOUND.put(displayName(p), p));
        } catch (IOException ignored) { }
        return FOUND.keySet().stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }
    private static String displayName(Path p) { String n = p.getFileName().toString(); int dot = n.lastIndexOf('.'); return dot > 0 ? n.substring(0, dot) : n; }
    static void setChoice(String category, String pack) { CHOICES.put(category, pack); }
    static String getChoice(String category) { return CHOICES.get(category); }

    static Path build(Path root) throws IOException {
        findPackNames(root);
        if (FOUND.isEmpty()) throw new IOException("Keine Resourcepacks im resourcepacks-Ordner.");
        Path output = root.resolve("x7qer-style.zip");
        Map<String, byte[]> files = new TreeMap<>();
        for (var choice : CHOICES.entrySet()) {
            Path source = FOUND.get(choice.getValue());
            if (source == null) continue;
            readCategory(source, choice.getKey(), files);
        }
        if (files.isEmpty()) throw new IOException("Wähle erst Packs für Kategorien aus.");
        files.put("pack.mcmeta", ("{\n  \"pack\": {\n    \"pack_format\": 75,\n    \"description\": \"x7qer Style - zusammengefuehrtes Pack\"\n  }\n}\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(output))) {
            for (var entry : files.entrySet()) { zip.putNextEntry(new ZipEntry(entry.getKey())); zip.write(entry.getValue()); zip.closeEntry(); }
        }
        return output;
    }

    private static void readCategory(Path source, String category, Map<String, byte[]> target) throws IOException {
        if (Files.isDirectory(source)) {
            try (var paths = Files.walk(source)) { for (Path p : paths.filter(Files::isRegularFile).toList()) {
                String rel = source.relativize(p).toString().replace('\\', '/');
                if (isCategoryFile(rel, category)) target.put(rel, Files.readAllBytes(p));
            }}
        } else try (ZipFile zip = new ZipFile(source.toFile())) {
            var entries = zip.entries();
            while (entries.hasMoreElements()) { ZipEntry e = entries.nextElement(); if (!e.isDirectory() && isCategoryFile(e.getName(), category)) {
                try (InputStream in = zip.getInputStream(e)) { target.put(e.getName(), in.readAllBytes()); }
            }}
        }
    }
    private static boolean isCategoryFile(String path, String category) {
        String p = path.toLowerCase(Locale.ROOT);
        if (!p.startsWith("assets/minecraft/")) return false;
        String area = p.startsWith("assets/minecraft/textures/") ? p.substring("assets/minecraft/textures/".length()) : p.substring("assets/minecraft/".length());
        return switch (category) {
            case "Blöcke" -> area.startsWith("block/") || area.startsWith("blocks/");
            case "Gear" -> area.startsWith("item/") && (area.contains("sword") || area.contains("armor") || area.contains("shield") || area.contains("bow") || area.contains("crossbow") || area.contains("trident"));
            case "PvP" -> area.startsWith("item/") && (area.contains("sword") || area.contains("bow") || area.contains("arrow") || area.contains("shield"));
            case "HUD" -> area.startsWith("gui/") || area.startsWith("font/") || area.startsWith("textures/gui/");
            case "Effekte" -> area.startsWith("particle/") || area.startsWith("textures/particle/");
            default -> area.startsWith("item/") || area.startsWith("entity/");
        };
    }
}
