package it.thesis.springboot.config;

import it.thesis.springboot.enums.FileType;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFileTypeConverter implements Converter<String, FileType> {
    @Override
    public FileType convert(@NonNull String source) {
        return FileType.findFileType(source);
    }
}
