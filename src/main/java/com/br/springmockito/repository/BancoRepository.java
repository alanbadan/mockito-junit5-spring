package com.br.springmockito.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.springmockito.model.Banco;

public interface BancoRepository extends JpaRepository<Banco, Long> {
 //metodos para teste sem uso de jpaRepository
//	  List<Banco> findAll();
//	  Banco findById(Long id);
//	  void update(Banco banco);
	 
	
}
