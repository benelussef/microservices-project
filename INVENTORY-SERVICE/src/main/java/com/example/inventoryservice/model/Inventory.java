package com.example.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_inventory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String skuCode;
    private Integer quantity;

}
