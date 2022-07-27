package com.br.springmockito.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.br.springmockito.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

import org.springframework.test.web.reactive.server.WebTestClient;

import com.br.springmockito.dto.TransacaoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



//CLASSE PARA TESTE DO CONTROLLER COM CHAMADAS WEB reais 
@Tag("integracao_wt") //passando a tag que quer fazer o teate pq da conflito de altearcao do estado do objeto no ContaControllerTestRestTemplate (passa a tag que quer provar nessa caso com dois retstes de controllers)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)// ANOTACAO PARA PASSAR A ORDEM DOS TESTES PARA NÃO AFETAR O ESTADO DO OBJETO (NO CASO TRANSFERIR SERIA POR ULTIMO)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContaControllerWebTestClient {
	
	
	@Autowired
	WebTestClient client;
	
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}	
	
	
	@Test
	@Order(1)
	void testTransferir() throws JsonProcessingException {
		
		//given
		TransacaoDto dto =  new TransacaoDto();
		dto.setContaOrigem(1L);
		dto.setContaDestino(2L);
		dto.setBancoId(1L);
		dto.setValor(new BigDecimal("100"));
		
		
		// o Json de resposta:
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "ok");
		response.put("menssagem", "Transferencia realisada com sucesso");
		response.put("transação", dto);
		
		
		
		//when
		client.post().uri("/contas/transferir") //não passa o localhost pq esta levantando em uma porta aleatoria
		           .contentType(MediaType.APPLICATION_JSON)
		           .bodyValue(dto)
		           .exchange()
		//then           
		           .expectStatus().isOk()
		           .expectHeader().contentType(MediaType.APPLICATION_JSON)
		           .expectBody()
		           .jsonPath("$.menssagem").isNotEmpty()
//		           .jsonPath("$menssagem").value(is("Transferencia realisada com sucesso")) //usando matchers 
                   .jsonPath("$menssagem").value(valor -> assertEquals("Transferencia realisada com sucesso", valor)) //usando lambda
//		           .jsonPath("$.menssagem").isEqualTo("Transferencia realisada com sucesso"); outra forma de verificacao
		           .jsonPath("$transacao.contaOrigem").isEqualTo(dto.getContaOrigem())
		           .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
	               .json(objectMapper.writeValueAsString(response));//convertendo o MAp em string com objectMapper
		
	}
	
	@Test
	@Order(2)
	void testDetalhe() throws JsonProcessingException {
		
		 Conta conta = new Conta(1L, "Jose", new BigDecimal("900"));
		
		client.get().uri("/contas/detalhe/1").exchange()
		           .expectStatus().isOk()
		           .expectHeader().contentType(MediaType.APPLICATION_JSON)
		           .expectBody()
		           .jsonPath("$.pessoa").isEqualTo("Jose")
		           .jsonPath("$.saldo").isEqualTo("900")
		           .json(objectMapper.writeValueAsString(conta));
                  
	}
	 @Test
	    @Order(3)
	    void testDetalhe2() { //usando exmplo da com lambda

	        client.get().uri("/contas/2").exchange()
	                .expectStatus().isOk()
	                .expectHeader().contentType(MediaType.APPLICATION_JSON)
	                .expectBody(Conta.class)
	                .consumeWith(response -> {
	                    Conta conta = response.getResponseBody();
	                    assertNotNull(conta);
	                    assertEquals("Marcos", conta.getPessoa());
	                    assertEquals("2100.00", conta.getSaldo().toPlainString());
	                });
	    }
	
	@Test
	@Order(4)
	void testLista() {
		
		   client.get().uri("/contas").exchange()
		           .expectStatus().isOk()
		           .expectHeader().contentType(MediaType.APPLICATION_JSON)
		           .expectBody()
		           .jsonPath("$[0].pessoa").isEqualTo("Jose")
		           .jsonPath("$[0].id").isEqualTo(1)
		           .jsonPath("$[0].saldo").isEqualTo(900)
		           .jsonPath("$[1].pessoa").isEqualTo("Marcos")
		           .jsonPath("$[0].id").isEqualTo(2)
		           .jsonPath("$[0].saldo").isEqualTo(2100)
		           .jsonPath("$").isArray() //conferindo se vindo como um array
		           .jsonPath("$").value(hasSize(2));
			
	}
	 //usando lambdas
	   @Test
	    @Order(5)
	    void testLista2() {
	        client.get().uri("/contas").exchange()
	                .expectStatus().isOk()
	                .expectHeader().contentType(MediaType.APPLICATION_JSON)
	                .expectBodyList(Conta.class) //esperrando o body como uma lista
	        .consumeWith(response -> {
	            List<Conta> conta = response.getResponseBody(); //obtendo a lista
	            assertNotNull(conta);
	            assertEquals(2, conta.size());
	            assertEquals(1L, conta.get(0).getId());
	            assertEquals("Jose", conta.get(0).getPessoa());
	            assertEquals(900, conta.get(0).getSaldo().intValue());//passando um inteiro
	            assertEquals(2L, conta.get(1).getId());
	            assertEquals("Marcos", conta.get(1).getPessoa());
	            assertEquals("2100.0", conta.get(1).getSaldo().toPlainString());
	        })
	        .hasSize(2)
	        .value(hasSize(2));
	   }

	 @Test
	 @Order(6)
	 void testSalvar() {
		 
		 //given
		 Conta conta = new Conta(null, "Pedro", new BigDecimal("3000"));
		 //when 
		 
		   client.post().uri("/contas")
		            .contentType(MediaType.APPLICATION_JSON)
		            .bodyValue(conta)
		            .exchange()
		 //then           
		            .expectStatus().isCreated()
		            .expectHeader().contentType(MediaType.APPLICATION_JSON)
		            .expectBody()
		            .jsonPath("$.id").isEqualTo(3)
		            .jsonPath("$.pessoa").isEqualTo("Pedro")
		            .jsonPath("$.pessoa").value(is("Pedro"))
		            .jsonPath("$.saldo").isEqualTo(3000);
	 }
	 //teste de listar com lambda 
	 @Test
	    @Order(7)
	    void testSalvar2() {
	        // given
	        Conta conta = new Conta(null, "Julia", new BigDecimal("3500"));

	        // when
	        client.post().uri("/api/cuentas")
	                .contentType(MediaType.APPLICATION_JSON)
	                .bodyValue(conta)
	                .exchange()
	         // then
	                .expectStatus().isCreated()
	                .expectHeader().contentType(MediaType.APPLICATION_JSON)
	                .expectBody(Conta.class)
	        .consumeWith(response -> {
	            Conta c = response.getResponseBody();
	            assertNotNull(c);
	            assertEquals(4L, c.getId());
	            assertEquals("Pepa", c.getPessoa());
	            assertEquals("3500", c.getSaldo().toPlainString());
	        });
	    }
	 
	 @Test
	 @Order(8)
	 void testDelete() {
		 
		 //conferindo a lista onde os elementos para deletar estao
		 client.get().uri("/contas").exchange()
		         .expectStatus().isOk()
		         .expectHeader().contentType(MediaType.APPLICATION_JSON)
		         .expectBodyList(Conta.class)
		         .hasSize(4);
		 
		 // agora deleta
		 client.delete().uri("/contas/3")
		          .exchange()
		          .expectStatus().isNoContent()
		          .expectBody().isEmpty();
		
		//conferindo a lista onde os elementos para deletar estao
		 client.get().uri("/contas").exchange()
		         .expectStatus().isOk()
		         .expectHeader().contentType(MediaType.APPLICATION_JSON)
		         .expectBodyList(Conta.class)
		         .hasSize(3); //agora a lista é de 3 elemenytos
		 
		 
		  //conferindo o retorno http do id deletado
		 client.get().uri("/contas/3").exchange()
	//	        .expectStatus().is5xxServerError(); //ele retorna um erro 500 mas deveria se lançar u 404 not-found 
		        .expectStatus().isNotFound()
		        .expectBody().isEmpty();
		 
	 }
}
