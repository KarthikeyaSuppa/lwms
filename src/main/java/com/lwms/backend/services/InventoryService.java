package com.lwms.backend.services;

import com.lwms.backend.dao.InventoryRepository;
import com.lwms.backend.dao.CategoriesRepository;
import com.lwms.backend.dao.LocationsRepository;
import com.lwms.backend.dto.InventoryCreateRequest;
import com.lwms.backend.dto.InventorySummaryDto;
import com.lwms.backend.dto.InventoryUpdateRequest;
import com.lwms.backend.entities.Categories;
import com.lwms.backend.entities.Inventory;
import com.lwms.backend.entities.Locations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {
	private final InventoryRepository inventoryRepository;
	private final CategoriesRepository categoriesRepository;
	private final LocationsRepository locationsRepository;

	public InventoryService(InventoryRepository inventoryRepository, CategoriesRepository categoriesRepository, LocationsRepository locationsRepository) {
		this.inventoryRepository = inventoryRepository;
		this.categoriesRepository = categoriesRepository;
		this.locationsRepository = locationsRepository;
	}

	@Transactional(readOnly = true)
	public List<InventorySummaryDto> listInventory(String query) { // query means search from frontend
		List<Inventory> results;
		if (StringUtils.hasText(query)) { //Uses Spring’s StringUtils.hasText() to check if the query is not null and not just whitespace.
			String q = query.trim();
			List<Inventory> byCodeOrName = inventoryRepository.findByItemCodeContainingIgnoreCaseOrItemNameContainingIgnoreCase(q, q); //here findByItemCodeContaining = where its containing by ignoringcase
			List<Inventory> byCategory = inventoryRepository.findByCategory_CategoryNameContainingIgnoreCase(q);
			List<Inventory> agg = new ArrayList<>();
			agg.addAll(byCodeOrName); 
			agg.addAll(byCategory);
			results = new ArrayList<>(Map.copyOf(agg.stream().collect(Collectors.toMap(Inventory::getItemId, i -> i, (a,b)->a))).values());//Uses a Map to remove duplicates based on itemId. 
																																		   //If duplicates exist, keeps the first occurrence ((a,b) -> a).
																																		   //Converts the map values back to a list.
		} else {
			results = inventoryRepository.findAll();
		}
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public InventorySummaryDto create(InventoryCreateRequest req) {
		validateCreate(req);//Ensures the request meets business rules (e.g., required fields, valid values).
							//Likely throws an exception if validation fails.
		Inventory inv = new Inventory();
		inv.setItemCode(req.getItemCode());
		inv.setItemName(req.getItemName());
		inv.setQuantity(Optional.ofNullable(req.getQuantity()).orElse(0));//Uses Optional.ofNullable(...).orElse(...)
																		  //to provide default values if the request fields are null
		inv.setMinStockLevel(Optional.ofNullable(req.getMinStockLevel()).orElse(0));
		inv.setMaxStockLevel(Optional.ofNullable(req.getMaxStockLevel()).orElse(0));
		inv.setUnitPrice(Optional.ofNullable(req.getUnitPrice()).orElse(BigDecimal.ZERO));
		inv.setCategory(resolveCategory(req.getCategory()));
		inv.setLocation(resolveLocation(req.getLocation()));
		Inventory saved = inventoryRepository.save(inv);
		return toDto(saved);
	}

	@Transactional
	public InventorySummaryDto update(Integer itemId, InventoryUpdateRequest req) {
		Inventory inv = inventoryRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Inventory item not found: " + itemId));
		if (req.getItemCode() != null) inv.setItemCode(req.getItemCode());
		if (req.getItemName() != null) inv.setItemName(req.getItemName());
		if (req.getQuantity() != null) inv.setQuantity(req.getQuantity());
		if (req.getMinStockLevel() != null) inv.setMinStockLevel(req.getMinStockLevel());
		if (req.getMaxStockLevel() != null) inv.setMaxStockLevel(req.getMaxStockLevel());
		if (req.getUnitPrice() != null) inv.setUnitPrice(req.getUnitPrice());
		if (StringUtils.hasText(req.getCategory())) inv.setCategory(resolveCategory(req.getCategory()));
		if (StringUtils.hasText(req.getLocation())) inv.setLocation(resolveLocation(req.getLocation()));
		Inventory saved = inventoryRepository.save(inv);
		return toDto(saved);
	}

	@Transactional
	public void delete(Integer itemId) {
		if (!inventoryRepository.existsById(itemId)) throw new RuntimeException("Inventory item not found: " + itemId);
		inventoryRepository.deleteById(itemId);
	}

	private Categories resolveCategory(String labelOrCode) {
		if (!StringUtils.hasText(labelOrCode)) throw new IllegalArgumentException("category is required");
		String val = labelOrCode.trim();
		// Prefer exact code match, otherwise exact name (case-insensitive)
		return categoriesRepository.findByCategoryCode(val)
				.orElseGet(() -> categoriesRepository.findByCategoryNameContainingIgnoreCaseOrCategoryCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(val, val, val)
						.stream().filter(c -> c.getCategoryName().equalsIgnoreCase(val)).findFirst()
						.orElseThrow(() -> new RuntimeException("Category not found: " + val)));
	}

	private Locations resolveLocation(String label) {
		if (!StringUtils.hasText(label)) throw new IllegalArgumentException("location is required");
		String target = normalizeLocationLabel(label);
		return locationsRepository.findAll().stream()
				.filter(l -> normalizeLocationLabel(computeLocationCode(l)).equals(target))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Location not found for: " + label));
	}

	private String normalizeLocationLabel(String s) {
		if (s == null) return "";
		return s.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
	}

	private String computeLocationCode(Inventory inv) {
		if (inv.getLocation() == null) return null;
		return computeLocationCode(inv.getLocation());
	}

	private String computeLocationCode(Locations loc) {
		String zone = loc.getZone();
		String aisle = loc.getAisle();
		String rack = loc.getRack();
		String shelf = loc.getShelf();
		return (zone != null ? zone : "") + (aisle != null ? aisle : "") + "-" + (rack != null ? rack : "") + "-" + (shelf != null ? shelf : "");
	}

	private InventorySummaryDto toDto(Inventory inv) {
		InventorySummaryDto dto = new InventorySummaryDto();
		dto.setItemId(inv.getItemId());
		dto.setItemCode(inv.getItemCode());
		dto.setItemName(inv.getItemName());
		dto.setQuantity(inv.getQuantity());
		dto.setMinStockLevel(inv.getMinStockLevel());
		dto.setMaxStockLevel(inv.getMaxStockLevel());
		dto.setUnitPrice(inv.getUnitPrice());
		dto.setCategory(inv.getCategory() != null ? inv.getCategory().getCategoryName() : null);
		dto.setLocation(computeLocationCode(inv));
		return dto;
	}

	private void validateCreate(InventoryCreateRequest req) {
		if (!StringUtils.hasText(req.getItemCode())) throw new IllegalArgumentException("itemCode is required");
		if (!StringUtils.hasText(req.getItemName())) throw new IllegalArgumentException("itemName is required");
		if (!StringUtils.hasText(req.getCategory())) throw new IllegalArgumentException("category is required");
		if (!StringUtils.hasText(req.getLocation())) throw new IllegalArgumentException("location is required");
	}
} 