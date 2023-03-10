package me.wux1an;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathScan {
    private final String     root;
    private       List<Path> files;

    public PathScan(String root) {
        this.root  = root;
        this.files = new ArrayList<>();
    }

    public int count() {
        return this.files.size();
    }

    public List<Path> getFiles() {
        return files;
    }

    public void scan() throws FileNotFoundException {
        File root = new File(this.root);
        if (root.isFile()) {
            files.add(root.toPath());
        } else if (root.isDirectory()) {
            scan0(root);
        } else {
            throw new FileNotFoundException();
        }
    }

    private void scan0(File file) {
        if (file.isDirectory()) {
            File[] subs = file.listFiles();
            if (subs == null) return;
            Arrays.stream(subs).forEach(this::scan0);
        } else if (file.isFile()) {
            files.add(file.toPath());
        }
    }
}
