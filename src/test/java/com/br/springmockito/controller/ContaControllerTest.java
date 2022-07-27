package com.br.springmockito.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.InvocationInterceptor.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.br.springmockito.Dados;
import com.br.springmockito.dto.TransacaoDto;
import com.br.springmockito.model.Conta;
import com.br.springmockito.service.ContaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ContaController.class)
class ContaControllerTest {
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	ContaService contaService;
	
	ObjectMapper objectMapper;
	
	@BeforeEach
	void steup() { //para conversao do dto
		objectMapper = new ObjectMapper();
	}
	
	@Test
	void detalhe() throws Exception {
		//given 
		when(contaService.findById(1L)).thenReturn(Dados.criarConta().orElseThrow());
		//when
		//chamando o controldor
		mvc.perform(MockMvcRequestBuilders.get("/contas/1").contentType(MediaType.APPLICATION_JSON))
		//then
		           .andExpect(status().isOk())
		           .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //coferindo se é um json
		           .andExpect(jsonPath("$.pessoa").value("Jose"))		   
		           .andExpect(jsonPath("$.saldo").value("1000"));
	}

	@Test
	void testTranferir() throws Exception{
		
		//given
		TransacaoDto dto = new TransacaoDto();
		dto.setContaOrigem(1L);
		dto.setContaDestino(2L);
		dto.setValor(new BigDecimal("100"));
		dto.setBancoId(1L);
	
	//when
	    mvc.perform(MockMvcRequestBuilders.post("/contas/transferir")
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(objectMapper.writeValueAsString(dto)))
	    //then  .
	    		.andExpect(status().isOk())
	    		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	    		.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
	    		.andExpect(jsonPath("$.menssagem").value("Transferencia realisada com sucesso"))
	    		.andExpect(jsonPath("$.transacao.contaOrigemId").value(dto.getContaOrigem()));
	}
	
	@Test
	void testListaContas() throws Exception{
		//given
		List<Conta> contas = Arrays.asList(Dados.criarConta().orElseThrow(), Dados.criarConta2().orElseThrow());
		
		when(contaService.findAll().thenReturn(contas));
		
		
		//When
		mvc.perform(MockMvcRequestBuilders.get("/contas").contentType(MediaType.APPLICATION_JSON))
		//then
		         .andExpect(status().isOk())
		         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		         .andExpect(jsonPath("$.[0].pessoa").value("Jose"))
		         .andExpect(jsonPath("$.[1].pessoa").value("Carlos"))
		         .andExpect(jsonPath("$.[0].saldo").value("1000"))
		         .andExpect(jsonPath("$.[1].saldo").value("2000"))
		         .andExpect(jsonPath("$", hasSize(2)))
		         .andexpected(content().json(objectMapper.writeValueAsString(contas)));
		         
	}
	@Test
	void testSalvar() {
		//given
		Conta conta = new Conta(null, "Pedro", new BigDecimal("3000")); //passando o id null usando a expressao lambda com invocation
		 when(contaService.save(any())).then(invocation -> {
			 Conta c = invocation.getArgument(0);
			 c.setId(3L);
			 return c;
		 });
	  //when
		 mvc.perform(MockMvcRequestBuilders.post("/contas")
				 .contentType(MediaType.APPLICATION_JSON)
		         .content(objectMapper.writeValueAsString(conta)))
       //then
                 .andExpect(jsonPath("$.id" , is(3))) //usando Matcher is (é como o equals
		         .andExpect(jsonPath("$.pessoa", is("Pedro")))
		         .andExpect(jsonPath("$.saldo", is(3000)));//passando como inteiro
		         
		         
	}
	
	
	
}
