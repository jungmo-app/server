package jungmo.server.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "gathering")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Gathering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_id")
    private Long id;
    @Column(name = "gathering_title")
    private String title;
    @Column(name = "gathering_start_date")
    private String startDate;
    @Column(name = "gathering_end_date")
    private String endDate;
    @Column(name = "gathering_start_time")
    private String startTime;
    @Column(name = "gathering_end_time")
    private String endTime;
    @Column(name = "gathering_memo")
    private String memo;
    @Column(name = "gathering_all_expense")
    private Long allExpense;
    @OneToMany(mappedBy = "gathering")
    private List<Expense> expenseList = new ArrayList<>();
    @OneToMany(mappedBy = "gathering")
    private List<GatheringUser> gatheringUsers = new ArrayList<>();
    @OneToMany(mappedBy = "gathering",cascade = CascadeType.PERSIST,orphanRemoval = true)
    private List<GatheringLocation> gatheringLocations = new ArrayList<>();

}
