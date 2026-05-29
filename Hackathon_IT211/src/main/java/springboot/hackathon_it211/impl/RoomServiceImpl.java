package springboot.hackathon_it211.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import springboot.hackathon_it211.exception.ResourceNotFoundException;
import springboot.hackathon_it211.model.dto.RoomDTO;
import springboot.hackathon_it211.model.entity.Room;
import springboot.hackathon_it211.repository.RoomRepository;
import springboot.hackathon_it211.service.RoomService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        room.setIsDeleted(false);
        room.setStatus(Room.RoomStatus.AVAILABLE);
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }

    @Override
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với mã: " + id));
        if (room.getIsDeleted()) {
            throw new ResourceNotFoundException("Phòng này đã bị xóa với mã: " + id);
        }
        return convertToDTO(room);
    }

    @Override
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAllByIsDeletedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với mã: " + id));

        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setRoomType(roomDTO.getRoomType());
        room.setPricePerNight(roomDTO.getPricePerNight());
        room.setStatus(roomDTO.getStatus());

        Room updatedRoom = roomRepository.save(room);
        return convertToDTO(updatedRoom);
    }

    @Override
    public RoomDTO partialUpdateRoom(Long id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với mã: " + id));

        if (roomDTO.getRoomNumber() != null) {
            if (roomDTO.getRoomNumber().trim().isEmpty()) {
                throw new RuntimeException("Số phòng không được để trống");
            }
            room.setRoomNumber(roomDTO.getRoomNumber());
        }
        if (roomDTO.getRoomType() != null) {
            if (roomDTO.getRoomType().trim().isEmpty()) {
                throw new RuntimeException("Loại phòng không được để trống");
            }
            room.setRoomType(roomDTO.getRoomType());
        }
        if (roomDTO.getPricePerNight() != null) {
            if (roomDTO.getPricePerNight() <= 0) {
                throw new RuntimeException("Giá mỗi đêm phải lớn hơn 0");
            }
            room.setPricePerNight(roomDTO.getPricePerNight());
        }
        if (roomDTO.getStatus() != null) room.setStatus(roomDTO.getStatus());

        Room updatedRoom = roomRepository.save(room);
        return convertToDTO(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với mã: " + id));
        room.setIsDeleted(true);
        roomRepository.save(room);
    }

    @Override
    public Page<RoomDTO> searchAndPaginate(String keyword, Pageable pageable) {
        Page<Room> rooms;
        if (keyword != null && !keyword.isEmpty()) {
            rooms = roomRepository.searchRooms(keyword, pageable);
        } else {
            rooms = roomRepository.findAllByIsDeletedFalse(pageable);
        }
        return rooms.map(this::convertToDTO);
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setStatus(room.getStatus());
        dto.setIsDeleted(room.getIsDeleted());
        return dto;
    }

    private Room convertToEntity(RoomDTO dto) {
        Room room = new Room();
        room.setId(dto.getId());
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setPricePerNight(dto.getPricePerNight());
        room.setStatus(dto.getStatus());
        room.setIsDeleted(dto.getIsDeleted());
        return room;
    }
}
