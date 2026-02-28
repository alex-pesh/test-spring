package com.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.entity.Product;
import com.example.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	@Override
	@Transactional
	public void updatePrice(Long id, double newPrice) {
		Product product = productRepository
				.findByIdLocked(id)
//				.findById(id)
				.orElseThrow(EntityNotFoundException::new);

		product.setPrice(newPrice);
		productRepository.save(product);

	}

}
