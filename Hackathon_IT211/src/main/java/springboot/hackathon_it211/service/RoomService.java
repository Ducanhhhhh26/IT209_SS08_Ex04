package springboot.hackathon_it211.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springboot.hackathon_it211.model.dto.RoomDTO;

import java.util.List;

public interface RoomService {
    RoomDTO createRoom(RoomDTO roomDTO);
    RoomDTO getRoomById(Long id);
    List<RoomDTO> getAllRooms();
    RoomDTO updateRoom(Long id, RoomDTO roomDTO);
    RoomDTO partialUpdateRoom(Long id, RoomDTO roomDTO);
    void deleteRoom(Long id);
    Page<RoomDTO> searchAndPaginate(String keyword, Pageable pageable);
}
