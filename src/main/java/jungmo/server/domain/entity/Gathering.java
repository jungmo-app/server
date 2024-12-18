package jungmo.server.domain.entity;

import jakarta.persistence.*;
import jungmo.server.domain.dto.request.GatheringDto;
import jungmo.server.domain.dto.response.GatheringResponseDto;
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
    @Column(name = "gathering_title")
    private String title;
    @Column(name = "gathering_start_date")
    private LocalDate startDate;
    @Column(name = "gathering_end_date")
    private LocalDate endDate;
    @Column(name = "gathering_start_time")
    private LocalTime startTime;
    @Column(name = "gathering_memo")
    private String memo;
    @Column(name = "gathering_all_expense")
    private Long allExpense;
    @Column(name = "is_connected")
    private Boolean isConnected;  //가계부와 연결됐는지
    @OneToMany(mappedBy = "gathering")
    private List<Expense> expenseList = new ArrayList<>();
    @OneToMany(mappedBy = "gathering")
    private List<GatheringUser> gatheringUsers = new ArrayList<>();
    @OneToMany(mappedBy = "gathering",cascade = CascadeType.PERSIST,orphanRemoval = true)
    private List<GatheringLocation> gatheringLocations = new ArrayList<>();

    public void update(GatheringDto gatheringDto) {
        this.title = gatheringDto.getTitle();
        this.startDate = gatheringDto.getStartDate();
        this.endDate = gatheringDto.getEndDate();
        this.startTime = gatheringDto.getStartTime();
        this.memo = gatheringDto.getMemo();
    }

    public GatheringResponseDto toDto() {
        return GatheringResponseDto.builder()
                .title(this.title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .startTime(this.startTime)
                .isConnected(this.isConnected)
                .memo(this.memo)
                .build();

    }

    @Builder
    public Gathering(Long id, String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, Long allExpense,Boolean isConnected, List<Expense> expenseList, List<GatheringUser> gatheringUsers, List<GatheringLocation> gatheringLocations) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.memo = memo;
        this.allExpense = allExpense;
        this.isConnected = isConnected;
        this.expenseList = expenseList!= null ? expenseList : new ArrayList<>();
        this.gatheringUsers = gatheringUsers!= null ? gatheringUsers : new ArrayList<>();
        this.gatheringLocations = gatheringLocations!= null ? gatheringLocations : new ArrayList<>();
    }
}
