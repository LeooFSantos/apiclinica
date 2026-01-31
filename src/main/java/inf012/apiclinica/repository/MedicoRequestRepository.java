package inf012.apiclinica.repository;

import inf012.apiclinica.model.MedicoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRequestRepository extends JpaRepository<MedicoRequest, Long> {
	List<MedicoRequest> findByApprovedFalseAndRejectedFalse();
}
