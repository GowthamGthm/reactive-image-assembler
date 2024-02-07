package com.example.cif.model;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MetaData {

    boolean first;
    boolean second;
    boolean finall;

    String hash;
    String fileName;
}