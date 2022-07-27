package com.br.springmockito.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bancos")
public class Banco {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String nome;
	@Column(name = "total_transferencia", nullable = false)
	private Integer totalTrasnferencia;
	
	
	public Banco() {
		
	}
	
	public Banco(Long id, String nome, Integer totalTransferencia) {
		this.id = id;
		this.nome = nome;
		this.totalTrasnferencia = totalTransferencia;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getTotalTrasnferencia() {
		return totalTrasnferencia;
	}
	public void setTotalTrasnferencia(Integer totalTrasnferencia) {
		this.totalTrasnferencia = totalTrasnferencia;
	}

	
}
