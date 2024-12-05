package myaong.popolog.prejobsservice.repository;

import myaong.popolog.prejobsservice.entity.Category;
import myaong.popolog.prejobsservice.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {
    boolean existsByCategoryAndName(Category category, String name);

    @Modifying
    @Query("UPDATE Job j SET j.index = j.index - 1 " +
            "WHERE j.category = :category AND j.index BETWEEN :startIndex AND :endIndex")
    void updateIndexRangeDecrement(@Param("category") Category category,
                                   @Param("startIndex") Integer startIndex,
                                   @Param("endIndex") Integer endIndex);

    @Modifying
    @Query("UPDATE Job j SET j.index = j.index + 1 " +
            "WHERE j.category = :category AND j.index BETWEEN :startIndex AND :endIndex")
    void updateIndexRangeIncrement(@Param("category") Category category,
                                   @Param("startIndex") Integer startIndex,
                                   @Param("endIndex") Integer endIndex);

}
