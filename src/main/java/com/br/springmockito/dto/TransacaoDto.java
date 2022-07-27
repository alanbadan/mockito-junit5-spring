package com.br.springmockito.dto;

import java.math.BigDecimal;

public class TransacaoDto { //para receber os dados do cliente
	
	private Long contaOrigem;
	private Long contaDestino;
	private BigDecimal valor;
	private Long bancoId;
	
	
	
	
	
	public Long getContaOrigem() {
		return contaOrigem;
	}
	public void setContaOrigem(Long contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	public Long getContaDestino() {
		return contaDestino;
	}
	public void setContaDestino(Long contaDestino) {
		this.contaDestino = contaDestino;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public Long getBancoId() {
		return bancoId;
	}
	public void setBancoId(Long bancoId) {
		this.bancoId = bancoId;
	}
	
	
	

}
