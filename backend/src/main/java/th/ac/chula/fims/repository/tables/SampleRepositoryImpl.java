package th.ac.chula.fims.repository.tables;

import th.ac.chula.fims.payload.request.LocusAllele;
import th.ac.chula.fims.utils.NumberUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

public class SampleRepositoryImpl implements SampleRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> searchMatchedSample(List<LocusAllele> lAList) {
        if (lAList.size() == 0) {
            return new ArrayList<>();
        }
        int matchedCount = 0;
        StringBuilder query = new StringBuilder("SELECT max(fs.id), fs.sample_id, fs.sample_year, max(fs.person_id) FROM forenseq ff inner join "
                + "forenseq_sequence ffs on ff.id = ffs.forenseq_id inner join samples fs "
                + "on ff.sample_id = fs.id where ");

        for (LocusAllele lA : lAList) {
            if (lA.getAllele().equals("INC") || lA.getAllele().equals("X,X") || lA.getAllele().equals("X,Y")) {
                continue;
            }

            String[] alleleList = lA.getAllele().split(",");

            for (String allele : alleleList) {
                if (matchedCount != 0) {
                    query.append(" OR ");
                }
                String locus = lA.getLocus().replaceAll("\\s", "");
                if (NumberUtils.isNumeric(lA.getAllele())) {
                    query.append(String.format("(ff.locus = \"%s\" AND ffs.allele = \"%.1f\")", locus,
                            Float.parseFloat(allele)));
                } else {
                    query.append(String.format("(ff.locus = \"%s\" AND ffs.allele = \"%s\")", locus, allele));
                }
                matchedCount += 1;
            }
        }

        query.append(String.format(" GROUP BY fs.sample_id, fs.sample_year HAVING count(*) = %d;", matchedCount));
        return entityManager.createNativeQuery(query.toString()).getResultList();
    }
}
