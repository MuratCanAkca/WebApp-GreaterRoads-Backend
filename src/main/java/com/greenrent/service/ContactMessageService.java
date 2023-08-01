package com.greenrent.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.greenrent.domain.ContactMessage;
import com.greenrent.exception.ResourceNotFoundException;
import com.greenrent.exception.message.ErrorMEssage;
import com.greenrent.repository.ContactMessageRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ContactMessageService {
	
	@Autowired
	private ContactMessageRepository repository;

	public void createContactMessage(ContactMessage contactMessage) {
		repository.save(contactMessage);
	}
	
	public List<ContactMessage> getAll(){
		return repository.findAll();
	}
	
	public ContactMessage getContactMessage(Long id) throws ResourceNotFoundException{
		return repository.findById(id).orElseThrow(()->
		new ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,id)));
		//burada olmayan bır id ile gelındıgınde clıente bızım yazdıgımız tıpde bır exception verıyoruz 
		//exceptionları bız yazdık ve format metodunu kullandık
	}
	
	
	public void deleteContactMessage(Long id) throws ResourceNotFoundException{
		ContactMessage message = getContactMessage(id);
		repository.deleteById(message.getId());
		/*
		repository.delete(message);
		//buda kullanılabilirdi 
	    */
	}
	
	public void updateContactMessage(Long id , ContactMessage newContactMessage) {
		ContactMessage foundMessage=getContactMessage(id);
		
		foundMessage.setName(newContactMessage.getName());
		foundMessage.setEmail(newContactMessage.getEmail());
		foundMessage.setSubject(newContactMessage.getSubject());
		foundMessage.setBody(newContactMessage.getBody());
		//dısardan visitor yanlıs seyler yazdı onu düzeltmek ıcın
		
		repository.save(foundMessage);
	}
	
	public Page<ContactMessage> getAllWithPage(Pageable pageable){
		return repository.findAll(pageable);
	}
	
}
