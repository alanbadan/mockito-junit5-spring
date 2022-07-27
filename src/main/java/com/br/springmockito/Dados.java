package com.br.springmockito;

import java.math.BigDecimal;
import java.util.Optional;

import com.br.springmockito.model.Banco;
import com.br.springmockito.model.Conta;

//classe para dados para teste mockito 
public class Dados {
  
//	public static final Conta conta1 = new Conta(1L, "Jose", new BigDecimal("1000"));
//	public static final Conta conta2 = new Conta(2L, "Carlos", new BigDecimal("2000"));
	
//	public static final Banco banco = new Banco(1L, "Banco Financeiro", 0);
	
	public static Optional<Conta> criarConta() {
		return Optional.of(new Conta(1L, "Jose", new BigDecimal("1000")));
	}
	
	public static Optional<Conta> criarConta2() {
		return Optional.of(new Conta(2L, "Carlos", new BigDecimal("2000")));
	}
	
	public static Optional<Banco> CriarBanco() {
		return Optional.of(new Banco(1L, "Banco Financeiro", 0));
	}
}
