package com.br.springmockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.br.springmockito.model.Conta;
import com.br.springmockito.repository.ContaRepository;

@DataJpaTest
public class IntegracaoJpaTest {
	
	
	@Autowired
	ContaRepository contaRepository;

	@Test
	void testFindById() {
		Optional<Conta> conta = contaRepository.findById(1L);
		assertTrue(conta.isPresent());
		assertEquals("Jose", conta.orElseThrow().getPessoa());
	}
	
	@Test
	void testFindByPessoa() {
		Optional<Conta> conta = contaRepository.findByPessoa("Jose");
		assertTrue(conta.isPresent());
		assertEquals("Jose", conta.orElseThrow().getPessoa());
		assertEquals("1000.00", conta.orElseThrow().getSaldo().toPlainString());
	}
	
	@Test
	void testFindByPessoaThrowsException() {
		Optional<Conta> conta = contaRepository.findByPessoa("Raul");
		assertThrows(NoSuchElementException.class, conta :: orElseThrow); //o :: vc esta evovabnco o metodo (ou deja caonta evoca trhows
		assertFalse(conta.isPresent());
  }
	
	@Test
	void testFindAll() {
		List<Conta> conta = contaRepository.findAll();
		assertFalse(conta.isEmpty()); 
		assertEquals(2, conta.size());
	}	
	
	@Test
	void testSave() {
	//	given
		Conta contaPedro = new Conta(null, "Pedro", new BigDecimal("3000"));
	//	contaRepository.save(contaPedro);
		
		
	// When
		Conta conta = contaRepository.save(contaPedro);
	//	Conta conta = contaRepository.findByPessoa("Pedro").orElseThrow();
	// then
		assertEquals("Pedro", conta.getPessoa());
		assertEquals("3000", conta.getSaldo().toPlainString());
//		assertEquals(3, conta.getId()); não é reconmendado beseecaso pq o id pode mudar conforme o rollback
	
		
	}
	@Test
	void testupDate() {
		//given 
		Conta contaPedro = new Conta(null, "Pedro", new BigDecimal("3000"));
		
		//When
		Conta conta =contaRepository.save(contaPedro);
		
		//Then
		assertEquals("Pedro", conta.getPessoa());
		assertEquals("3000", conta.getSaldo().toPlainString());
		
		
		//when
		conta.setSaldo(new BigDecimal("3800"));
		Conta contaAtualizada = contaRepository.save(conta);
		
		//then
		assertEquals("Pedro", contaAtualizada.getPessoa());
		assertEquals("3800", contaAtualizada.getSaldo().toPlainString());
	}
	
	@Test
	void testDelete() {
		
		Conta conta = contaRepository.findById(2L).orElseThrow();
		assertEquals("Carlos", conta.getPessoa());
		
		contaRepository.delete(conta);
		
		assertThrows(NoSuchElementException.class, () -> {
//			contaRepository.findByPessoa("carlos").orElseThrow(); assim ou de outra maneira
			contaRepository.findById(2L).orElseThrow();
		});
		 
		assertEquals(1, contaRepository.findAll().size());
	}
}