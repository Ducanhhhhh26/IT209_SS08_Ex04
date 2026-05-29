package springboot.hackathon_it211.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.hackathon_it211.model.entity.Room.RoomStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Long id;

    @NotBlank(message = "Số phòng không được để trống")
    private String roomNumber;

    @NotBlank(message = "Loại phòng không được để trống")
    private String roomType;

    @NotNull(message = "Giá mỗi đêm là bắt buộc")
    @Min(value = 1, message = "Giá mỗi đêm phải lớn hơn 0")
    private Double pricePerNight;

    private RoomStatus status;
    private Boolean isDeleted;
}
