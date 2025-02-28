package jungmo.server.domain.entity;

import jakarta.persistence.*;
import jungmo.server.domain.dto.request.GatheringRequest;
import jungmo.server.domain.dto.response.GatheringResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "gathering")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gathering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_id")
    private Long id;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private String memo;

    private Long allExpense;

    private Boolean isDeleted;  //삭제된 모임여부
    @OneToMany(mappedBy = "gathering")
    private List<Expense> expenseList = new ArrayList<>();
    @OneToMany(mappedBy = "gathering", cascade = CascadeType.PERSIST,orphanRemoval = true)
    private List<GatheringUser> gatheringUsers = new ArrayList<>();
    @OneToMany(mappedBy = "gathering",cascade = CascadeType.PERSIST,orphanRemoval = true)
    private List<GatheringLocation> gatheringLocations = new ArrayList<>();

    public void update(GatheringRequest gatheringDto) {
        this.title = gatheringDto.getTitle();
        this.startDate = gatheringDto.getStartDate();
        this.endDate = gatheringDto.getEndDate();
        this.startTime = gatheringDto.getStartTime();
        this.memo = gatheringDto.getMemo();
    }

    public GatheringResponse toDto() {
        return GatheringResponse.builder()
                .id(this.id)
                .title(this.title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .startTime(this.startTime)
                .memo(this.memo)
                .build();

    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Builder
    public Gathering(Long id, String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, Long allExpense, Boolean isDeleted, List<Expense> expenseList, List<GatheringUser> gatheringUsers, List<GatheringLocation> gatheringLocations) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.memo = memo;
        this.allExpense = allExpense;
        this.isDeleted = isDeleted;
        this.expenseList = expenseList!= null ? expenseList : new ArrayList<>();
        this.gatheringUsers = gatheringUsers!= null ? gatheringUsers : new ArrayList<>();
        this.gatheringLocations = gatheringLocations!= null ? gatheringLocations : new ArrayList<>();
    }
}
