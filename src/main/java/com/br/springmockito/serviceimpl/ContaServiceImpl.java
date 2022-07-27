package com.br.springmockito.serviceimpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.springmockito.model.Banco;
import com.br.springmockito.model.Conta;
import com.br.springmockito.repository.BancoRepository;
import com.br.springmockito.repository.ContaRepository;
import com.br.springmockito.service.ContaService;

@Service
public class ContaServiceImpl implements ContaService {

	@Autowired
	ContaRepository contaRepository;
	
	@Autowired
	BancoRepository bancoRepository;
	
	//conrtutor por causa do mock
	public ContaServiceImpl(ContaRepository contaRepository, BancoRepository bancoRepository) {
		super();
		this.contaRepository = contaRepository;
		this.bancoRepository = bancoRepository;
	}
	

	@Override
	@Transactional
	public List<Conta> findAll() {
       return contaRepository.findAll();
	}


	@Override
	@Transactional
	public Conta save(Conta conta) {
	    return contaRepository.save(conta);
	}
	
	@Override
	@Transactional
	public Conta findById(Long id) {
	    return contaRepository.findById(id).orElseThrow();
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
         contaRepository.deleteById(id);		
		
	}
	
	@Override
	@Transactional
	public int revisarTotalTransferencia(Long bancoId) {
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();; //peagando o banco por id
		return  banco.getTotalTrasnferencia();
	}
	

	@Override
	@Transactional
	public BigDecimal revisarSaldo(Long contaId) {
	     Conta conta = contaRepository.findById(contaId).orElseThrow();;
		return conta.getSaldo();
	}

	@Override
	@Transactional
	public void transferir(Long numContaOrigem, Long numContaDestino, BigDecimal valor,Long bancoId) {
	   //debito
		Conta contaOrigem = contaRepository.findById(numContaDestino).orElseThrow();
		contaOrigem.debitoConta(valor);
		contaRepository.save(contaOrigem);
		
		//credito
		Conta contadestino = contaRepository.findById(numContaDestino).orElseThrow();
		contadestino.creditoConta(valor);
		contaRepository.save(contadestino);
		
		
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();// PARAMETRO ESTATICO por causa do tst unitario mas pode ser fazer diferente
		int totalTransferencia = banco.getTotalTrasnferencia();
		banco.setTotalTrasnferencia(++totalTransferencia);
		bancoRepository.save(banco);
	}


}
