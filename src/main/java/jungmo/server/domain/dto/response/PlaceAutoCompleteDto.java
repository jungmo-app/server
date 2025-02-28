package jungmo.server.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceAutoCompleteDto {

    private String description;    // 검색어 (예: "서울, 대한민국")
    private String placeId;        // 장소 ID

}
