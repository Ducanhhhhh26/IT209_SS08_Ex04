package springboot.hackathon_it211.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot.hackathon_it211.model.entity.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByIsDeletedFalse();

    Page<Room> findAllByIsDeletedFalse(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.isDeleted = false AND " +
           "(LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.roomType) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Room> searchRooms(@Param("keyword") String keyword, Pageable pageable);
}

