package com.br.springmockito.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.br.springmockito.dto.TransacaoDto;
import com.br.springmockito.model.Conta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tag("integracao_rt") //quando subir todos os teste deve-se passar a tag de teste para não causar alteraçao de teste dos objetos.
@TestMethodOrder(MethodOrderer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContaControllerTestRestTemplate {
	

	@Autowired
	TestRestTemplate client;
	
	private ObjectMapper objectMapper;
	
	 private String porta;
	
	
	@BeforeEach
	void steUp() {
		 objectMapper = new ObjectMapper();
	}
	
	
	@Test
	@Order(1)
	void testTransferir() throws JsonMappingException, JsonProcessingException  {
		
		//given
		TransacaoDto dto =  new TransacaoDto();
		dto.setValor(new BigDecimal("100"));
		dto.setContaOrigem(1L);
		dto.setContaDestino(2L);
		dto.setBancoId(1L);
		
	//com metodo rnadomico para porta	
//		ResponseEntity<String> response = client.postForEntity("/contas/transferir",  dto, String.class);
		
		ResponseEntity<String> response = client.postForEntity(criaUri("/contas/transferir"),  dto, String.class);
//		System.out.println(porta);
		String json = response.getBody();
//	    System.out.println(json);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(json);
        assertTrue(json.contains("Transferencia realisada com sucesso"));		
			
        
        //convertendo String em Json e conferindo o Json( alem do context , cada elemeto de resposta do json)
        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realisada com sucesso", jsonNode.path("menssagem").asText());
        assertEquals(LocalDate.now().toString(),jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transação").path("valor").asText());
        assertEquals(1L, jsonNode.path("transação").path("contaOrigem").asLong());
        
        
        //comparando o Json  de responsta como String e a String original
        Map<String, Object> response2 = new HashMap<>();
		response2.put("date", LocalDate.now().toString());
		response2.put("status", "ok");
		response2.put("menssagem", "Transferencia realisada com sucesso");
		response2.put("transação", dto);
        
        
        assertEquals(objectMapper.writeValueAsString(response2), json);
	}
	
	
	@Order(2)
	@Test
	void testDetalhe() {
		
		ResponseEntity<Conta> response = client.getForEntity(criaUri("/contas/detalhe"), Conta.class);
		Conta conta =  response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		
		
		assertNotNull(conta);
		assertEquals(1L, conta.getId());
		assertEquals("Jose", conta.getPessoa());
		assertEquals("900", conta.getSaldo().toPlainString());
		assertEquals(new Conta(1L, "Jose", new BigDecimal("900")), conta);
		
		
	}
	 
	@Test
	@Order(3)
	void testLista() throws JsonMappingException, JsonProcessingException {
	       
		ResponseEntity<Conta[]> response = client.getForEntity(criaUri("/contas"), Conta[].class);
		
		List<Conta> conta = Arrays.asList(response.getBody());
		
		assertEquals(2, conta.size());
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		
		assertEquals(1L, conta.get(0).getId());
		assertEquals("Jose", conta.get(0).getPessoa());
		assertEquals("900", conta.get(0).getSaldo().toPlainString());
		assertEquals(2L, conta.get(1).getId());
		assertEquals("Marcos", conta.get(1).getPessoa());
		assertEquals("2100", conta.get(1).getSaldo().toPlainString());
	
		//outra maneira de testar como o json
		JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(conta));
		assertEquals(1L,  json.get(0).path("id").asLong());
		assertEquals("Jose", json.get(0).path("pessoa").asText());
		assertEquals("900", json.get(0).path("saldo").asText());
		assertEquals(2L,  json.get(1).path("id").asLong());
		assertEquals("Marcos", json.get(1).path("pessoa").asText());
		assertEquals("2100", json.get(1).path("saldo").asText());
	}
	
	
	 @Test
	 @Order(4)
	 void testSalvar() {
		 //tem criar uma conta para salvar
		  Conta conta = new Conta(null, "Pedro", new BigDecimal("3000"));
		  
		  ResponseEntity<Conta> response = client.postForEntity(criaUri("/conta"), conta, Conta.class);
		   
		  // o status é created
		  assertEquals(HttpStatus.CREATED, response.getStatusCode());
		  assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		 
		  Conta contaSalva = response.getBody();
		  assertNotNull(contaSalva);
		  assertEquals(3L, contaSalva.getId());
		  assertEquals("Pedro", contaSalva.getPessoa());
		  assertEquals("3000", contaSalva.getSaldo().toPlainString());

	 }
	 
	 @Test
	 @Order(5)
	 void testDelete() {
	      
		 ResponseEntity<Conta[]> response = client.getForEntity(criaUri("/contas"), Conta[].class);
		 List<Conta> conta = Arrays.asList(response.getBody());
		 assertEquals(3, conta.size()); //conferindo a lista de contas
		 
		 client.delete(criaUri("/contas/3"));
		 
		 response = client.getForEntity(criaUri("/contas"), Conta[].class);
		 List<Conta> conta2 = Arrays.asList(response.getBody());
		 assertEquals(2, conta2.size()); //conferindo a lista de contasv depois de eliminar o id 3 
		 
		 
		 ResponseEntity<Conta> responseDetalhe = client.getForEntity(criaUri("/contas/3"), Conta.class);
		 assertEquals(HttpStatus.NOT_FOUND, responseDetalhe.getStatusCode());
		 assertEquals(2, responseDetalhe.hasBody());
		 
	 }
	
	//exemplo de metodo para criar uma url 
	private String criaUri(String uri) {
		return "http://localhost:" + porta + uri;
	}
	
	
	
	
	
	
	
}
