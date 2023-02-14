package me.wux1an;

import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Decompiler {
    private final Config config;

    public Decompiler(Config config) {
        this.config = config;
    }

    public void action() {
        File file = new File(this.config.output);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                System.out.println("[x] the output path '" + this.config.output + "'is not empty");
            }
        }


        File input = new File(this.config.input);
        if (input.exists() && input.isFile()) {
            new DecompileJob(input, config).decompileOne();
            System.exit(0);
        }

        PathScan pathScan = new PathScan(this.config.input);
        System.out.println("[+] scanning files in " + this.config.input + "...");
        try {
            pathScan.scan();
        } catch (FileNotFoundException ignored) {
            System.out.println("[x] path '" + this.config.input + "' is not exist");
            System.exit(0);
        }

        Cache cache;
        try {
            cache = new Cache("decompiled.txt", pathScan.getFiles(), config.resume);
        } catch (IOException e) {
            System.out.println("[x] failed to load cache, " + e.getMessage());
            return;
        }

        ProgressBar     bar  = new ProgressBar("decompiling", pathScan.count());
        ExecutorService pool = Executors.newFixedThreadPool(this.config.thread);
        pathScan.getFiles().forEach(path -> pool.submit(new DecompileJob(path.toFile(), bar, config, cache)));

        pool.shutdown();

        // capture shutdown
        capture(cache, pool);

        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bar.close();
    }

    private void capture(Cache cache, ExecutorService pool) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            pool.shutdownNow();

            String[] remains = cache.remain();
            if (remains.length == 0) return;

            System.out.println("\n[+] there are " + remains.length + " jars that have not been decompiled");
            if (remains.length < 20) {
                Arrays.stream(remains).forEach(System.out::println);
            } else {
                System.out.println("[+] saved remain path to 'remain.txt'");
                try (FileOutputStream output = new FileOutputStream("remain.txt")) {
                    for (String remain : remains) {
                        output.write((remain + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}
