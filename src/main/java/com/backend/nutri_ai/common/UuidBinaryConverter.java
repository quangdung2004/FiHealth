package com.backend.nutri_ai.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.ByteBuffer;
import java.util.UUID;

@Converter(autoApply = true)
public class UuidBinaryConverter implements AttributeConverter<UUID, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(UUID uuid) {
        if (uuid == null) return new byte[0];
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    @Override
    public UUID convertToEntityAttribute(byte[] bytes) {
        if (bytes == null || bytes.length != 16) return null;
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }
}
