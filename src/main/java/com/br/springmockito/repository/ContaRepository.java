package com.br.springmockito.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.springmockito.model.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long>  {
	//metodos para teste sem uso de jpaRepository
//    List<Conta> findAll();
//	Conta findById(Long id);
//	void upDate(Conta conta);
	
//	@Query("select c from Contas c where c.pessoa=?1") //exemplo de comsulta com query
	Optional<Conta> findByPessoa(String pessoa);
	
	
	
}
