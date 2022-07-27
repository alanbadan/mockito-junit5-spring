package com.br.springmockito.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.br.springmockito.dto.TransacaoDto;
import com.br.springmockito.model.Conta;
import com.br.springmockito.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {
	
	
	@Autowired
	private ContaService contaService;
	
	
	@GetMapping
	@ResponseStatus(OK)
	public List<Conta> listarContas(){
		return null;
	}
	
	@PostMapping
	@ResponseStatus(CREATED)
	public Conta salvarConta(@RequestBody Conta conta) {
		return contaService.save(conta);
	
	
  }
	@GetMapping("/{id}")
	public ResponseEntity<?> detalhe(@PathVariable Long id) { //colocando try por causa do retorno 500 no caso de teste delete
		
		Conta conta = null;
		
		try {
			conta = contaService.findById(id);
		}catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(conta);
	}
    
	@PostMapping("/transferir")
	public ResponseEntity<?> tranferir (@RequestBody TransacaoDto dto) {
		contaService.transferir(dto.getContaOrigem(), dto.getContaDestino(), dto.getValor(), dto.getBancoId());
		
		//mantando o Json de resposta:
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "ok");
		response.put("menssagem", "Transferencia realisada com sucesso");
		response.put("transação", dto);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(NO_CONTENT) // senão estivesse declarando a rsposta de forma staic o retorno seria um responEntity
	public void delete (@PathVariable Long id) {
		contaService.deleteById(id);
	}
	
}
