package me.wux1an;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cache {
    private final File        file;
    private       Set<String> decompiled = new HashSet<>();
    private final Set<String> remain     = new HashSet<>();

    public Cache(String path, List<Path> files, boolean resume) throws IOException {
        this.file = new File(path);
        if (resume && this.file.exists()) {
            decompiled.addAll(Files.readAllLines(Path.of(path)));
            System.out.println("[+] load cache, automatically ignore " + decompiled.size() + " files");
        }

        files.forEach(f -> remain.add(f.toAbsolutePath().toString()));
        decompiled.forEach(remain::remove);
    }

    public synchronized void markDecompiled(Path path) {
        String p = path.toAbsolutePath().toString();
        decompiled.add(p);
        remain.remove(p);
        try {
            Files.writeString(file.toPath(), p + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
    }

    public synchronized boolean hasDecompiled(Path p) {
        return decompiled.contains(p.toAbsolutePath().toString());
    }

    public synchronized String[] remain() {
        return this.remain.toArray(new String[0]);
    }
}
