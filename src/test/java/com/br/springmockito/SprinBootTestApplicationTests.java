package com.br.springmockito;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.br.springmockito.exception.SaldoInsuficienteException;
import com.br.springmockito.model.Banco;
import com.br.springmockito.model.Conta;
import com.br.springmockito.repository.BancoRepository;
import com.br.springmockito.repository.ContaRepository;
import com.br.springmockito.service.ContaService;


@RunWith(SpringRunner.class)
@SpringBootTest
class SprinBootTestApplicationTests {
	
	@MockBean
	ContaRepository contaRepository;
	
	@MockBean
	BancoRepository bancoRepository;
	
	@Autowired
	ContaService contaService;
	
	
	@BeforeEach
	void setup() {
	
		//sem anotacao Mock
//		contaRepository = mock(ContaRepository.class);
//		bancoRepository = mock(BancoRepository.class);
//		contaService = new ContaServiceImpl(contaRepository, bancoRepository);//sem o construtor na clss contaserviceImpl nã funciona
		//deve-se passar os valores estaticos como constantes no Before pq cada execuçao do metodo ele muda o esta do objeto . E o teste deve ser com os dados iniciais das constantes
//		Dados.conta1.setSaldo(new BigDecimal("1000"));
//		Dados.conta2.setSaldo(new BigDecimal("2000"));
//		Dados.banco.setTotalTrasnferencia(0); 
		
		// Ou podemos usar mentodos estaticos para os valores tb na classe Dados
		
		
	}
	

	@Test
	void contextLoads() {
		//com constantes
//		when(contaRepository.findById(1l).thenReturn(Dados.conta1));
//		when(contaRepository.findById(2l).thenReturn(Dados.conta2));
//		when(bancoRepository.findById(1l).thenReturn(Dados.banco1));
		
	//	com metodos static
		when(contaRepository.findById(1l).thenReturn(Dados.criarConta()));
		when(contaRepository.findById(2l).thenReturn(Dados.criarConta2()));
		when(bancoRepository.findById(1l).thenReturn(Dados.CriarBanco()));
		
		
		//verificando o saldo antes da transferencia
		
		BigDecimal contaOrigem = contaService.revisarSaldo(1L);
		BigDecimal contaDestino = contaService.revisarSaldo(2L);
		assertEquals("1000", contaOrigem.toPlainString()); // to plain convertendo BigDecimal em String
		assertEquals("2000", contaDestino.toPlainString());
		
		//verificado o metodo de transferir
		                       //conta 1 origem, conta 2 destino,o valor, banco
		contaService.transferir(1L, 2L, new BigDecimal("100"), 1L);
		
		//vericando depois da tranferencia
		contaOrigem = contaService.revisarSaldo(1L);
		contaDestino = contaService.revisarSaldo(2L);
	    assertEquals("900", contaOrigem.toPlainString());
	    assertEquals("2100", contaDestino.toPlainString());
	    
	    //verificando quantas vezes o metodo é invocado
	    
	    int total = contaService.revisarTotalTransferencia(1L);
	    assertEquals(1, total);
	    
	    verify(contaRepository, times(3)).findById(1l);
	    verify(contaRepository, times(3)).findById(2l);
	    verify(contaRepository, times(2)).save(any(Conta.class));
	    
	    verify(bancoRepository, times(2)).findById(1l);
	    verify(bancoRepository).save(any(Banco.class));
		
	}
	
	//testando a exception
	@Test
	void contextLoads2() {
		
//		com metodos static
			when(contaRepository.findById(1l).thenReturn(Dados.criarConta()));
			when(contaRepository.findById(2l).thenReturn(Dados.criarConta2()));
			when(bancoRepository.findById(1l).thenReturn(Dados.CriarBanco()));
		
		
		//verificando o saldo antes da transferencia
		
		BigDecimal contaOrigem = contaService.revisarSaldo(1L);
		BigDecimal contaDestino = contaService.revisarSaldo(2L);
		assertEquals("1000", contaOrigem.toPlainString()); // to plain convertendo BigDecimal em String
		assertEquals("2000", contaDestino.toPlainString());
		
		//testando a exception
		assertThrows(SaldoInsuficienteException.class, () -> {
			contaService.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});
		
		
		//vericando depois da tranferencia
		contaOrigem = contaService.revisarSaldo(1L);
		contaDestino = contaService.revisarSaldo(2L);
		
	    assertEquals("1000", contaOrigem.toPlainString());
	    assertEquals("2000", contaDestino.toPlainString());
	    
	    //verificando quantas vezes o metodo é invocado
	    
	    int total = contaService.revisarTotalTransferencia(1L);
	    assertEquals(0, total);
	    
	    verify(contaRepository, times(3)).findById(1l);
	    verify(contaRepository, times(2)).findById(2l);
	    verify(contaRepository, never()).save(any(Conta.class));
	    
	    verify(bancoRepository, times(1)).findById(1l);
	    verify(bancoRepository, never()).save(any(Banco.class));
		
	}
	
	@Test //teste pra verificar se éo mesmo objeto uqe esta retornado
	void contextLoad() {
		
		when(contaRepository.findById(1L).thenReturn(Dados.criarConta());
		
		Conta conta = contaService.findById(1L);
		Conta conta2 = contaService.findById(1L);
		
		assertTrue(conta == conta2);
	    assertEquals("Jose", conta.getPessoa());
	    assertEquals("Jose", conta2.getPessoa());
	    
	    verify(contaRepository, times(2)).findById(1L);
	}
 
	@Test
	void testFindAll() {
		//given
		List<Conta> dados = Arrays.asList(Dados.criarConta().orElseThrow(), Dados.criarConta2().orElseThrow());
		when(contaRepository.findAll().thenReturn(dados));
		
		//when
		List<Conta> contas = contaService.findAll();
		
		//then
		assertFalse(contas.isEmpty());
		assertEquals(2, contas.size());
		assertTrue(contas.contains(Dados.criarConta2().orElseThrow()));
		
		verify(contaRepository).findAll();
	}
	@Test
	void testSalvar() {
		//given
		Conta contaPedro = new Conta(null, "Pedro", new BigDecimal("3000")); //passando o id null usando a expressao lambda com invocation
		 when(contaService.save(any())).then(invocation -> {
			 Conta c = invocation.getArgument(0);
			 c.setId(3L);
			 return c;
		 });
       //when
		 Conta conta = contaService.save(contaPedro);
		 //then
		    
			assertEquals("Pedro", conta.getPessoa());
			assertEquals(3, conta.getId());
			assertEquals("3000", conta.getSaldo().toPlainString());
			
			verify(contaRepository).save(any());
		 
 }
}