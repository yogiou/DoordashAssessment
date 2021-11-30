package jie.wen.doordash.assessment.springboot.data.repository;

import jie.wen.doordash.assessment.springboot.data.entities.PhoneNumberData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneNumberDataRepository extends JpaRepository<PhoneNumberData, String> {
    @Query(value = "SELECT * FROM phone_number_data WHERE search_key IN (:searchKeys)",
            nativeQuery =true)
    List<PhoneNumberData> findBySearchKeyIn(@Param("searchKeys") List<String> searchKeys);
}
