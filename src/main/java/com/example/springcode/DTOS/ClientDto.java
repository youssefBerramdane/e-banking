package com.example.springcode.DTOS;

import com.example.springcode.Entities.Transfere;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor @NoArgsConstructor
public class ClientDto {
    private Long id;
    private String name;
    private double Solde;
    private String email;
    private List<Transfere> transfereList;
}
