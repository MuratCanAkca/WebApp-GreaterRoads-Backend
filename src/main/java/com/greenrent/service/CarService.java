package com.greenrent.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greenrent.domain.Car;
import com.greenrent.domain.ImageFile;
import com.greenrent.dto.CarDTO;
import com.greenrent.dto.mapper.CarMapper;
import com.greenrent.exception.BadRequestException;
import com.greenrent.exception.ResourceNotFoundException;
import com.greenrent.exception.message.ErrorMEssage;
import com.greenrent.repository.CarRepository;
import com.greenrent.repository.ImageFileRepository;
import com.greenrent.repository.ReservationRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CarService {

	private CarRepository carRepository;
	private ImageFileRepository imageFileRepository;
	private CarMapper carMapper;
	private ReservationRepository reservationRepository;
	
	
	@Transactional(readOnly = true)
	public List<CarDTO> getAllCars(){
	
		List<Car> carList = carRepository.findAll();
		return carMapper.map(carList);
		
	}
	
	@Transactional(readOnly = true)
	public CarDTO findById(Long id) {
		
		Car car =carRepository.findById(id).orElseThrow(()-> new 
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, id)));
		
		return carMapper.carToCarDTO(car);
		
	}
	
	
	public void saveCar(CarDTO carDTO , String imageId) {
		
		ImageFile imFile=imageFileRepository.findById(imageId).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.IMAGE_NOT_FOUND_MESSAGE,imageId)));
		
		//Car car=CarMapper.INSTANCE.carDTOToCar(carDTO);
		
		Car car=carMapper.carDTOToCar(carDTO);
		
		Set<ImageFile> imFiles= new HashSet<>();
		imFiles.add(imFile);
		
		car.setImage(imFiles);
		
		carRepository.save(car);
		
		
	}
	
	//repository kısmında query ıcın metot
	@Transactional(readOnly = true)
	public Page<CarDTO> findAllWithPage(Pageable pageable){
		return carRepository.findAllCarWithPage(pageable);
	}
	
	/**
	 * this method is used to update a car
	 * @param id Car id of the car that will be updated
	 * @param imageId this is image id
	 * @param carDTO This is carDTO to keep data about the car
	 */
	@Transactional(readOnly = true)
	public void updateCar(Long id , String imageId , CarDTO carDTO) {
		
		Car foundCar =carRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,id)));
		
		ImageFile imFile =imageFileRepository.findById(imageId).
		orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMEssage.IMAGE_NOT_FOUND_MESSAGE, imageId)));
		
		if(foundCar.getBuiltIn()) {
			throw new BadRequestException(ErrorMEssage.NOT_PERMITTED_METHOT_MESSAGE);
		}
		
		Set<ImageFile> imgs =foundCar.getImage();
		imgs.add(imFile);
		
		Car car = carMapper.carDTOToCar(carDTO);
		
		car.setId(foundCar.getId());
		car.setImage(imgs);
		carRepository.save(car);
	
	}
	
	public void removeById(Long id) {
		
		Car car =carRepository.findById(id).orElseThrow(()-> 
		new ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,id)));
		
		//eger rezervasyonda araba varsa silme işlemi yapılmaması ıcın sonradan eklenen kod burası
		boolean exists=reservationRepository.existsByCarId(car);
		
		if(exists) {
			throw new BadRequestException(ErrorMEssage.CAR_USED_BY_RESERVATION_MESSAGE);
		}
		
		//buraya kadardı
		
		if(car.getBuiltIn()){
			throw new BadRequestException(ErrorMEssage.NOT_PERMITTED_METHOT_MESSAGE);
		}
		
		carRepository.deleteById(id);
		
		
	}
	
}
