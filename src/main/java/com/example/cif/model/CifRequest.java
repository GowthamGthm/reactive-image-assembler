package com.example.cif.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@RedisHash
public class CifRequest {

    private String identifier;

    private Long chunkNumber;
    private String chunk;
    MetaData metadata;

}