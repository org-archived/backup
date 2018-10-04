package io.vilya.backup.itellyou;

import com.alibaba.fastjson.JSON;
import io.vilya.backup.BackupException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author vilya
 */
@Slf4j
public class JsonDataRepository implements DataRepository {

    private Path file;

    public JsonDataRepository(Path file) {
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void insert(List<Product> data) {
        List<Product> localData = Objects.requireNonNullElse(data, Collections.emptyList());
        try {
            Files.deleteIfExists(file);
            Files.writeString(file, JSON.toJSONString(localData, true));
        } catch (IOException e) {
            throw new BackupException(e);
        }
    }

}
