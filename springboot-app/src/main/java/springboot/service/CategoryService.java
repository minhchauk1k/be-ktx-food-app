package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.exception.EntityNotFoundException;
import springboot.model.Category;
import springboot.repository.CategoryRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepo;

	public Category add(Category category) {
		log.info("Added new Category: {}", category.getCategoryKey());
		return categoryRepo.save(category);
	}

	public List<Category> getCategorys() {
		try {
			return categoryRepo.findAll();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public Category findById(Long id) {
		try {
			return categoryRepo.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Category with id: " + id + "was not found!"));
		} catch (Exception e) {
			return null;
		}
	}

	public void deleteById(Long id) {
		Category entity = findById(id);
		if (entity != null) {
			categoryRepo.deleteById(id);
		} else {
			throw new EntityNotFoundException("Category with id: " + id + " was not found!");
		}
	}
}
