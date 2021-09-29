package bnorbert.onlineshop.service;

import org.springframework.core.io.Resource;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {

    void init();
    Resource load(String name);
    void deleteAll();
    Stream<Path> loadAll();
}
