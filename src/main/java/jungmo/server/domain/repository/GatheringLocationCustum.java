package jungmo.server.domain.repository;

public interface GatheringLocationCustum {

    boolean isPlaceAlreadyExists(Long gatheringId, String placeId);
}
