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
    private final ProgressBar bar;
    private final Cache       cache;

    public DecompileJob(File src, ProgressBar bar, Config config, Cache cache) {
        this.src    = src;
        this.bar    = bar;
        this.cache  = cache;
        this.config = config;
    }

    @Override
    public void run() {
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
            decompiler.decompileContext();

            // 2. unzip
            if (newFilePath.endsWith(".jar") && config.unzip) {
                File unpack = new File(newFilePath + ".unpack");
                File zip    = new File(newFilePath);
                ZipUtil.unpack(zip, unpack);
                if (!new File(newFilePath).delete()) {
                    System.out.println("[x] failed to delete temp jar file at '" + zip.toPath() + "'");

                } else {
                    if (!unpack.renameTo(zip)) {
                        System.out.println("[x] failed to rename unpacked jar directory from '" +
                                unpack.toPath() +
                                "' to '" +
                                zip.toPath() +
                                "'"
                        );
                    }
                }
            }

            cache.markDecompiled(src.toPath());
        } finally {
            bar.step();
        }
    }
}
