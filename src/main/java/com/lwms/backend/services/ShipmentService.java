package com.lwms.backend.services;

import com.lwms.backend.dao.ShipmentsRepository;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.dao.SuppliersRepository;
import com.lwms.backend.dto.ShipmentCreateRequest;
import com.lwms.backend.dto.ShipmentSummaryDto;
import com.lwms.backend.dto.ShipmentUpdateRequest;
import com.lwms.backend.entities.Shipments;
import com.lwms.backend.entities.Suppliers;
import com.lwms.backend.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShipmentService {
	private final ShipmentsRepository shipmentsRepository;
	private final UserRepository userRepository;
	private final SuppliersRepository suppliersRepository;

	public ShipmentService(ShipmentsRepository shipmentsRepository, UserRepository userRepository, SuppliersRepository suppliersRepository) {
		this.shipmentsRepository = shipmentsRepository;
		this.userRepository = userRepository;
		this.suppliersRepository = suppliersRepository;
	}

	@Transactional(readOnly = true)
	public List<ShipmentSummaryDto> listShipments(String q) {
		List<Shipments> results;
		if (StringUtils.hasText(q)) {
			String query = q.trim();
			List<Shipments> byNumber = shipmentsRepository.findByShipmentNumberContainingIgnoreCase(query);
			List<Shipments> agg = new ArrayList<>(byNumber);
			// Type match (Inbound/Outbound)
			try {
				Shipments.ShipmentType type = Shipments.ShipmentType.valueOf(query.substring(0,1).toUpperCase() + query.substring(1).toLowerCase());
				agg.addAll(shipmentsRepository.findAll().stream().filter(s -> s.getShipmentType() == type).collect(Collectors.toList()));
			} catch (IllegalArgumentException ignore) {}
			results = new ArrayList<>(Map.copyOf(agg.stream().collect(Collectors.toMap(Shipments::getShipmentId, s -> s, (a,b)->a))).values());
		} else {
			results = shipmentsRepository.findAll();
		}
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public ShipmentSummaryDto createShipment(ShipmentCreateRequest req) {
		Shipments s = new Shipments();
		s.setShipmentNumber(req.getShipmentNumber());
		if (StringUtils.hasText(req.getShipmentType())) s.setShipmentType(Shipments.ShipmentType.valueOf(req.getShipmentType()));
		if (StringUtils.hasText(req.getStatus())) s.setStatus(parseStatus(req.getStatus()));
		s.setOrigin(req.getOrigin());
		s.setDestination(req.getDestination());
		if (StringUtils.hasText(req.getExpectedDelivery())) s.setExpectedDeliveryDate(LocalDateTime.parse(req.getExpectedDelivery()));
		if (StringUtils.hasText(req.getActualDelivery())) s.setActualDeliveryDate(LocalDateTime.parse(req.getActualDelivery()));
		if (StringUtils.hasText(req.getTotalValue())) s.setTotalValue(new BigDecimal(req.getTotalValue()));
		resolveAndSetSupplier(s, req.getSupplierId(), req.getSupplier());
		s.setCreatedBy(resolveCurrentUser());
		Shipments saved = shipmentsRepository.save(s);
		return toDto(saved);
	}

	@Transactional
	public ShipmentSummaryDto updateShipment(Integer shipmentId, ShipmentUpdateRequest req) {
		Shipments s = shipmentsRepository.findById(shipmentId)
				.orElseThrow(() -> new RuntimeException("Shipment not found: " + shipmentId));
		if (req.getShipmentNumber() != null) s.setShipmentNumber(req.getShipmentNumber());
		if (req.getShipmentType() != null) s.setShipmentType(Shipments.ShipmentType.valueOf(req.getShipmentType()));
		if (req.getStatus() != null) s.setStatus(parseStatus(req.getStatus()));
		if (req.getOrigin() != null) s.setOrigin(req.getOrigin());
		if (req.getDestination() != null) s.setDestination(req.getDestination());
		if (req.getExpectedDelivery() != null) s.setExpectedDeliveryDate(LocalDateTime.parse(req.getExpectedDelivery()));
		if (req.getActualDelivery() != null) s.setActualDeliveryDate(LocalDateTime.parse(req.getActualDelivery()));
		if (req.getTotalValue() != null) s.setTotalValue(new BigDecimal(req.getTotalValue()));
		if (req.getSupplier() != null || req.getSupplierId() != null) resolveAndSetSupplier(s, req.getSupplierId(), req.getSupplier());
		Shipments saved = shipmentsRepository.save(s);
		return toDto(saved);
	}

	@Transactional
	public void deleteShipment(Integer shipmentId) {
		shipmentsRepository.deleteById(shipmentId);
	}

	private Shipments.Status parseStatus(String s) {
		return switch (s) {
			case "Planned" -> Shipments.Status.Planned;
			case "In Transit" -> Shipments.Status.In_Transit;
			case "Delivered" -> Shipments.Status.Delivered;
			case "Cancelled" -> Shipments.Status.Cancelled;
			default -> throw new IllegalArgumentException("Unknown status: " + s);
		};
	}

	private ShipmentSummaryDto toDto(Shipments s) {
		ShipmentSummaryDto dto = new ShipmentSummaryDto();
		dto.setShipmentId(s.getShipmentId());
		dto.setShipmentNumber(s.getShipmentNumber());
		dto.setShipmentType(s.getShipmentType() != null ? s.getShipmentType().name() : null);
		dto.setStatus(s.getStatus() != null ? switch (s.getStatus()) {
			case Planned -> "Planned";
			case In_Transit -> "In Transit";
			case Delivered -> "Delivered";
			case Cancelled -> "Cancelled";
		} : null);
		dto.setOrigin(s.getOrigin());
		dto.setDestination(s.getDestination());
		dto.setExpectedDelivery(s.getExpectedDeliveryDate() != null ? s.getExpectedDeliveryDate().toString() : null);
		dto.setActualDelivery(s.getActualDeliveryDate() != null ? s.getActualDeliveryDate().toString() : null);
		dto.setTotalValue(s.getTotalValue() != null ? s.getTotalValue().toString() : null);
		dto.setSupplier(s.getSupplier() != null ? s.getSupplier().getSupplierName() : null);
		return dto;
	}

	private void resolveAndSetSupplier(Shipments s, Integer supplierId, String supplierNameFallback) {
		if (supplierId != null) {
			Suppliers sup = suppliersRepository.findById(supplierId).orElse(null);
			s.setSupplier(sup);
			return;
		}
		if (!StringUtils.hasText(supplierNameFallback)) { s.setSupplier(null); return; }
		String v = supplierNameFallback.trim();
		Suppliers sup = suppliersRepository.search(v, null).stream()
				.filter(x -> x.getSupplierName() != null && x.getSupplierName().equalsIgnoreCase(v))
				.findFirst()
				.orElseGet(() -> suppliersRepository.search(v, null).stream().findFirst().orElse(null));
		s.setSupplier(sup);
	}

	private User resolveCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User springUser)) {
			throw new RuntimeException("Unauthenticated: createdBy required");
		}
		String username = springUser.getUsername();
		try {
			User u = new User();
			u.setUserId(Integer.parseInt(username));
			return u;
		} catch (NumberFormatException ignore) {
			return userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("User not found for username: " + username));
		}
	}
} 