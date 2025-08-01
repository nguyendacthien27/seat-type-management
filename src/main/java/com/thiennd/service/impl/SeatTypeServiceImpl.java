package com.thiennd.service.impl;

import com.thiennd.dto.SeatTypeDTO;
import com.thiennd.entity.SeatType;
import com.thiennd.exception.SeatTypeException;
import com.thiennd.repository.SeatTypeRepository;
import com.thiennd.service.SeatTypeService;
import com.thiennd.service.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatTypeServiceImpl implements SeatTypeService {
    private final SeatTypeRepository seatTypeRepository;
    private final ValidationService validationService;

    @Autowired
    public SeatTypeServiceImpl(SeatTypeRepository seatTypeRepository, ValidationService validationService) {
        this.seatTypeRepository = seatTypeRepository;
        this.validationService = validationService;
    }

    @Override
    public void create(SeatTypeDTO seatTypeDTO) {
        SeatType seatType = new SeatType();

        seatType.setSeatTypeCode(validationService.getNextSeatTypeCode());
        seatType.setSeatTypeName(StringUtils.trim(seatTypeDTO.getSeatTypeName()));
        seatType.setWorkerMemo(StringUtils.trim(seatTypeDTO.getWorkerMemo()));

        seatTypeRepository.save(seatType);
    }

    @Override
    public SeatType findById(Long id) {
        Optional<SeatType> seatTypeOptional = seatTypeRepository.findById(id);

        if (seatTypeOptional.isEmpty()) {
            throw new SeatTypeException("The seat type is not exists");
        }
        return seatTypeOptional.get();
    }

    @Override
    public List<SeatType> findAllOrderBySeatTypeCode() {
        return seatTypeRepository.findAll(Sort.by("seatTypeCode"));
    }

    @Override
    public Long countSeatType() {
        return seatTypeRepository.countByDeletedFalse();
    }

    @Override
    public void updateBooking(Long id) {
        SeatType seatType = findById(id);
        seatType.setBooked(true);

        seatTypeRepository.save(seatType);
    }

    @Override
    public void delete(Long id) {
        SeatType seatType = findById(id);

        if (seatType.isBooked()) {
            throw new SeatTypeException("Cannot delete this seat type as it is currently in use.");
        }

        seatType.setDeleted(true);
        seatTypeRepository.save(seatType);
    }

    @Override
    public void duplicate(Long id) {
        SeatType seatType = findById(id);
        SeatTypeDTO seatTypeDTO = new SeatTypeDTO();

        seatTypeDTO.setSeatTypeName(seatType.getSeatTypeName());
        seatTypeDTO.setWorkerMemo(seatType.getWorkerMemo());
        create(seatTypeDTO);
    }

    @Override
    public void update(SeatTypeDTO seatTypeDTO) {
        SeatType seatType = findById(seatTypeDTO.getId());

        seatType.setSeatTypeName(StringUtils.trim(seatTypeDTO.getSeatTypeName()));
        seatType.setWorkerMemo(StringUtils.trim(seatTypeDTO.getWorkerMemo()));

        seatTypeRepository.save(seatType);
    }
}
