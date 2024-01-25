package org.example.user.app.domain.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@Slf4j
public class HashManagerImpl implements HashManager {

    @Override
    @SneakyThrows
    public String toHash(String data) {
        log.debug("called with args: data={}", data);
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashBytes = digest.digest(
                data.getBytes(StandardCharsets.UTF_8));
        return toHex(hashBytes);
    }

    private String toHex(byte[] hash) {
        log.debug("called with args: hash={}", hash);
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}