package com.greenrent.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greenrent.domain.ContactMessage;
import com.greenrent.service.ContactMessageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/contactmessage")
@AllArgsConstructor
public class ContactMessageController {

	@Autowired
	private ContactMessageService contactMessageService;
	
	@PostMapping("/visitor")
	public ResponseEntity<Map<String , String>> createMessage(@Valid @RequestBody ContactMessage contactMessage){
		
		contactMessageService.createContactMessage(contactMessage);
		
		Map<String , String> map = new HashMap<>();
		map.put("message", "Contact Message Successfully created");
		map.put("status", "true");
		
		return new ResponseEntity<>(map, HttpStatus.CREATED);
		//create edildiği için client 201 gönderdik httStatus aslında bu demek 
		
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ContactMessage>> getAllContactMessage(){
		List <ContactMessage> list =contactMessageService.getAll();
		return ResponseEntity.ok(list);
	}
	
	//id ile getirme işlemi
	//localhost:8080/contactmessage/2
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ContactMessage> getMessage(@PathVariable("id") Long id){
		//pathveriable getmappıngle gelen id'yi(endpoint) parametre olan id ye  eşitler
		
		ContactMessage contactMessage =contactMessageService.getContactMessage(id);
		return ResponseEntity.ok(contactMessage);
		
	}
	//yukarıdakının requestlı halı 
	//localhost:8080/contactmessage/request?id=2
	@GetMapping("/request")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ContactMessage> getMessageWithRequestParam(@RequestParam("id") Long id){
		ContactMessage contactMessage =contactMessageService.getContactMessage(id);
		return ResponseEntity.ok(contactMessage);
	}
	
	
	//localhost:8080/contactmessage/1
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String , String>> updateContactMessage(@PathVariable Long id , @Valid @RequestBody ContactMessage contactMessage){
		
		contactMessageService.updateContactMessage(id, contactMessage);
		
		Map<String , String> map = new HashMap<>();
		map.put("message", "Contact Message Successfully updated");
		map.put("status", "true");
		
		return new ResponseEntity<>(map, HttpStatus.CREATED);
	}
	
	//localhost:8080/contactmessage/1
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String , String>> deleteContactMessage(@PathVariable Long id){
		contactMessageService.deleteContactMessage(id);
		
		Map<String , String> map = new HashMap<>();
		map.put("message", "Contact Message Successfully deleted");
		map.put("status", "true");
		
		return new ResponseEntity<>(map, HttpStatus.CREATED);
	}
	
	@GetMapping("/pages")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<ContactMessage>> getAllWithPage(
			@RequestParam("page") int page , @RequestParam("size")int size,
			@RequestParam("sort") String prop , @RequestParam("direction") Direction direction){
		
		Pageable pageable = PageRequest.of(page, size , Sort.by(direction,prop));
		Page<ContactMessage> contactMessagePage = contactMessageService.getAllWithPage(pageable);
		return ResponseEntity.ok(contactMessagePage);
	}
	
	
}
