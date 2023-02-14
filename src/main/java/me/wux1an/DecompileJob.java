package me.wux1an;

import me.tongfei.progressbar.ProgressBar;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

public class DecompileJob implements Runnable {
    private final File        src;
    private final Config      config;
    private       ProgressBar bar;
    private       Cache       cache;

    /**
     * decompile a directory
     */
    public DecompileJob(File dir, ProgressBar bar, Config config, Cache cache) {
        this.src    = dir;
        this.bar    = bar;
        this.cache  = cache;
        this.config = config;
    }

    public DecompileJob(File file, Config config) {
        this.src    = file;
        this.config = config;
    }

    public void decompileOne() {
        String inputRoot   = Path.of(this.config.input).toAbsolutePath().getParent().toString();
        String srcPath     = src.toPath().toAbsolutePath().toString();
        String outputRoot  = Path.of(this.config.output).toAbsolutePath().toString();
        Path   newFilePath = Path.of(srcPath.replace(inputRoot, outputRoot));

        ConsoleDecompiler decompiler = new ConsoleDecompiler(
                newFilePath.getParent().toFile(),
                new HashMap<>(),
                new QuiteLogger()
        );
        decompiler.addSource(src);
        try {
            decompiler.engine.decompileContext();
        } catch (Exception e) {
            System.out.println("[x] failed to decompile file '" + src.getAbsolutePath().toString() + "'");
        } finally {
            decompiler.engine.decompileContext();
        }

        if (newFilePath.toString().endsWith(".jar") && config.unzip) {
            unzip(newFilePath.toAbsolutePath().toString());
        }

        System.out.println("[+] decompiled to '" + newFilePath + "'");
    }

    private void unzip(String path) {
        File unpack = new File(path + ".unpack");
        File zip    = new File(path);
        ZipUtil.unpack(zip, unpack);
        if (this.config.backup) {
            return;
        }

        if (!new File(path).delete()) {
            System.out.println("[x] failed to delete temp jar file at '" + zip.toPath() + "'");
        } else if (!unpack.renameTo(zip)) {
            System.out.println("[x] failed to rename unpacked jar directory from '" +
                    unpack.toPath() +
                    "' to '" +
                    zip.toPath() +
                    "'"
            );
        }
    }

    @Override
    public void run() {
        boolean decompileSuccess = false;
        try {
            if (this.cache.hasDecompiled(src.toPath())) return;

            String inputRoot   = Path.of(this.config.input).toAbsolutePath().toString();
            String srcPath     = src.toPath().toAbsolutePath().toString();
            String outputRoot  = Path.of(this.config.output).toAbsolutePath().toString();
            String newFilePath = srcPath.replace(inputRoot, outputRoot);

            // 1. decompile
            ConsoleDecompiler decompiler = new ConsoleDecompiler(
                    Path.of(newFilePath).getParent().toFile(),
                    new HashMap<>(),
                    new QuiteLogger()
            );
            decompiler.addSource(src);
            try {
                decompiler.engine.decompileContext();
                decompileSuccess = true;
            } catch (Exception e) {
                System.out.println("[x] failed to decompile file '" + src.getAbsolutePath().toString() + "'");
            } finally {
                decompiler.engine.decompileContext();
            }

            // 2. unzip
            if (newFilePath.endsWith(".jar") && config.unzip) {
                unzip(newFilePath);
            }

            if (decompileSuccess) {
                cache.markDecompiled(src.toPath());
            }
        } finally {
            if (decompileSuccess) {
                bar.step();
            }
        }
    }
}
