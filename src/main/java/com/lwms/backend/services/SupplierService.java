package com.lwms.backend.services;

import com.lwms.backend.dao.SuppliersRepository;
import com.lwms.backend.dto.SupplierCreateRequest;
import com.lwms.backend.dto.SupplierSummaryDto;
import com.lwms.backend.dto.SupplierUpdateRequest;
import com.lwms.backend.entities.Suppliers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {
	private final SuppliersRepository suppliersRepository;

	public SupplierService(SuppliersRepository suppliersRepository) {
		this.suppliersRepository = suppliersRepository;
	}

	@Transactional(readOnly = true)
	public List<SupplierSummaryDto> listSuppliers(String nameOrContactQuery, Boolean active) {
		List<Suppliers> results = suppliersRepository.search(
				StringUtils.hasText(nameOrContactQuery) ? nameOrContactQuery : null,
				active);
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public SupplierSummaryDto createSupplier(SupplierCreateRequest req) {
		Suppliers s = new Suppliers();
		s.setSupplierName(req.getSupplierName());
		s.setContactPerson(req.getContactPerson());
		s.setEmail(req.getEmail());
		s.setPhone(req.getPhone());
		s.setAddress(req.getAddress());
		s.setIsActive(req.getActive() != null ? req.getActive() : Boolean.TRUE);
		Suppliers saved = suppliersRepository.save(s);
		return toDto(saved);
	}

	@Transactional
	public SupplierSummaryDto updateSupplier(Integer supplierId, SupplierUpdateRequest req) {
		Suppliers s = suppliersRepository.findById(supplierId)
				.orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));
		if (req.getSupplierName() != null) s.setSupplierName(req.getSupplierName());
		if (req.getContactPerson() != null) s.setContactPerson(req.getContactPerson());
		if (req.getEmail() != null) s.setEmail(req.getEmail());
		if (req.getPhone() != null) s.setPhone(req.getPhone());
		if (req.getAddress() != null) s.setAddress(req.getAddress());
		if (req.getActive() != null) s.setIsActive(req.getActive());
		Suppliers saved = suppliersRepository.save(s);
		return toDto(saved);
	}

	@Transactional
	public void deleteSupplier(Integer supplierId) {
		if (!suppliersRepository.existsById(supplierId)) {
			throw new RuntimeException("Supplier not found: " + supplierId);
		}
		suppliersRepository.deleteById(supplierId);
	}

	private SupplierSummaryDto toDto(Suppliers s) {
		SupplierSummaryDto dto = new SupplierSummaryDto();
		dto.setSupplierId(s.getSupplierId());
		dto.setSupplierName(s.getSupplierName());
		dto.setContactPerson(s.getContactPerson());
		dto.setEmail(s.getEmail());
		dto.setPhone(s.getPhone());
		dto.setAddress(s.getAddress());
		dto.setActive(s.getIsActive());
		return dto;
	}
} 