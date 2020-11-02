package com.fedex.systemForm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fedex.systemForm.dto.CpmsSystemFormServiceDTO;

@Repository
public interface CpmsSystemFormServiceRepo extends JpaRepository<CpmsSystemFormServiceDTO, Long>{
	
}
