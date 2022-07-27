package com.br.springmockito.service;

import java.math.BigDecimal;
import java.util.List;

import com.br.springmockito.model.Conta;

public interface ContaService  {
	
	List<Conta> findAll();
	
	Conta findById(Long id);
	
	Conta save(Conta conta);
	
	void deleteById(Long id);
	
	int revisarTotalTransferencia(Long bancoId);
	
	BigDecimal revisarSaldo(Long contaId);
	
	void transferir(Long numContaOrigem, Long numContaDestino, BigDecimal valor, Long bancoId );

}
