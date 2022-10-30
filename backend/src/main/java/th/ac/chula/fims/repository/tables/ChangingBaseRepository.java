package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.ChangedBase;
import th.ac.chula.fims.models.tables.ForenseqSequence;

import java.util.List;

@Repository
public interface ChangingBaseRepository extends JpaRepository<ChangedBase, Integer> {
    List<ChangedBase> findAllByForenseqSeq(ForenseqSequence forenseqSeq);

    List<ChangedBase> findAllByForenseqSeqOrderByPosition(ForenseqSequence forenseqSeq);
}
