package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.entity.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long>, GatheringRepositoryCustom{

}
