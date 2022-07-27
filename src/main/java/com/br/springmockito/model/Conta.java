package com.br.springmockito.model;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.br.springmockito.exception.SaldoInsuficienteException;

@Entity
@Table(name = "contas")
public class Conta {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String pessoa;
	@Column(nullable = false)
	private BigDecimal saldo;
	
	public Conta() {
		
	}
	
	public Conta(Long id, String pessoa, BigDecimal saldo) {
		this.id = id;
		this.pessoa = pessoa;
		this.saldo = saldo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPessoa() {
		return pessoa;
	}

	public void setPessoa(String pessoa) {
		this.pessoa = pessoa;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, pessoa, saldo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conta other = (Conta) obj;
		return Objects.equals(id, other.id) && Objects.equals(pessoa, other.pessoa)
				&& Objects.equals(saldo, other.saldo);
	}
	
	//metodo debito 
	public void debitoConta(BigDecimal valor) {
//		this.saldo = saldo.subtract(valor); metodo sem lançar exception pra vapor negativo
		BigDecimal novoSaldo = this.saldo.subtract(valor);
		if(novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new SaldoInsuficienteException("Saldo Insuficiente em conta");
		}
		 this.saldo = novoSaldo;
	}
	//metodo credito
	public void creditoConta(BigDecimal valor) {
		this.saldo = saldo.add(valor);
		
	}
}
