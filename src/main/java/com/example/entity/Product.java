package com.example.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter@Setter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_product_id", sequenceName = "seq_product_id")
	private Long id;

	@Version
	private int version;

	private String name;

	private double price;

}
