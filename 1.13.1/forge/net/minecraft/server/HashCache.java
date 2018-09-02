package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HashCache {
    private static final Logger a = LogManager.getLogger();
    private final java.nio.file.Path b;
    private final java.nio.file.Path c;
    private int d;
    private final Map<java.nio.file.Path, String> e = Maps.newHashMap();
    private final Map<java.nio.file.Path, String> f = Maps.newHashMap();

    public HashCache(java.nio.file.Path path, String s) throws IOException {
        this.b = path;
        java.nio.file.Path path1 = path.resolve(".cache");
        Files.createDirectories(path1);
        this.c = path1.resolve(s);
        this.c().forEach((path2) -> {
            String s1 = (String)this.e.put(path2, "");
        });
        if (Files.isReadable(this.c)) {
            IOUtils.readLines(Files.newInputStream(this.c), Charsets.UTF_8).forEach((s1) -> {
                int i = s1.indexOf(32);
                this.e.put(path.resolve(s1.substring(i + 1)), s1.substring(0, i));
            });
        }

    }

    public void a() throws IOException {
        this.b();

        BufferedWriter bufferedwriter;
        try {
            bufferedwriter = Files.newBufferedWriter(this.c);
        } catch (IOException ioexception) {
            a.warn("Unable write cachefile {}: {}", this.c, ioexception.toString());
            return;
        }

        IOUtils.writeLines((Collection)this.f.entrySet().stream().map((entry) -> {
            return (String)entry.getValue() + ' ' + this.b.relativize((java.nio.file.Path)entry.getKey());
        }).collect(Collectors.toList()), System.lineSeparator(), bufferedwriter);
        bufferedwriter.close();
        a.debug("Caching: cache hits: {}, created: {} removed: {}", this.d, this.f.size() - this.d, this.e.size());
    }

    @Nullable
    public String a(java.nio.file.Path path) {
        return (String)this.e.get(path);
    }

    public void a(java.nio.file.Path path, String s) {
        this.f.put(path, s);
        if (Objects.equals(this.e.remove(path), s)) {
            ++this.d;
        }

    }

    public boolean b(java.nio.file.Path path) {
        return this.e.containsKey(path);
    }

    private void b() throws IOException {
        this.c().forEach((path) -> {
            if (this.b(path)) {
                try {
                    Files.delete(path);
                } catch (IOException ioexception) {
                    a.debug("Unable to delete: {} ({})", path, ioexception.toString());
                }
            }

        });
    }

    private Stream<java.nio.file.Path> c() throws IOException {
        return Files.walk(this.b).filter((path) -> {
            return !Objects.equals(this.c, path) && !Files.isDirectory(path, new LinkOption[0]);
        });
    }
}