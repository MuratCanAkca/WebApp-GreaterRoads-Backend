package com.greenrent.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.greenrent.domain.ImageFile;
import com.greenrent.dto.ImageFileDTO;
import com.greenrent.exception.ImageFileException;
import com.greenrent.exception.ResourceNotFoundException;
import com.greenrent.exception.message.ErrorMEssage;
import com.greenrent.repository.ImageFileRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ImageFileService {

	private ImageFileRepository imageFileRepository;
	
	public String saveImage(MultipartFile file) {
		String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		ImageFile imageFile=null;
		try {
			imageFile = new ImageFile(fileName, file.getContentType() , file.getBytes());
		} catch (IOException e) {
			throw new ImageFileException(e.getMessage());
		}
		imageFileRepository.save(imageFile);
		return imageFile.getId();
	}
	
	public ImageFile getImageById(String id) {
		ImageFile imageFile=imageFileRepository.findById(id).orElseThrow(()->
			new ResourceNotFoundException(String.format(ErrorMEssage.IMAGE_NOT_FOUND_MESSAGE, id)));
		return imageFile;
	}
	
	//*****************************
	//7 baslangıc
	
	//link create ediyor sanırım
	public List<ImageFileDTO> getAllImages(){
		List<ImageFile> imageList =imageFileRepository.findAll();
		List <ImageFileDTO> files =imageList.stream().map(imFile->{
			String imageUri = ServletUriComponentsBuilder.fromCurrentContextPath().
			path("/files/download/").
			path(imFile.getId()).toUriString();
			
			return new ImageFileDTO(imFile.getName(), imageUri , imFile.getType(), imFile.getData().length);
		}).collect(Collectors.toList());
		return files;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
