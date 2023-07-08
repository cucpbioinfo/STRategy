const resolveAlleleFreq = (count, N) => {
  let alleleFreq = count / N;
  let lowerBound = 5 / N;
  if (alleleFreq < lowerBound) {
    return lowerBound.toFixed(4);
  }
  return alleleFreq.toFixed(4);
}

const resolveGraphInfoWithType = (graphInfo, type) => {
  let diploid = graphInfo.diploid;
  let haploid = graphInfo.haploid;
  if (!type && diploid) {
    return graphInfo.diploid;
  } else if (!type && haploid) {
    return graphInfo.haploid;
  } else if (type === "Diploid" && diploid) {
    return graphInfo.diploid;
  } else if (type === "Haploid" && haploid) {
    return graphInfo.haploid;
  }
  return {};
}

const StatisticUtils = {
  resolveAlleleFreq,
  resolveGraphInfoWithType
}

export default StatisticUtils;