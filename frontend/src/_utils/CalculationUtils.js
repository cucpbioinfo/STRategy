function countItemAndCount(items) {
  let sum = 0;
  for (let item of items) {
    sum += +item.amount
  }
  return sum
}

function countMapData(items) {
  const alphaColor = {};

  for (let i = 0; i < items.length; i++) {
    const {alleleAmountList, province} = items[i];
    const sum = countItemAndCount(alleleAmountList)
    const curRegion = province.region.region
    const alphaColorElement = alphaColor[curRegion];
    if (typeof alphaColorElement === "undefined") {
      alphaColor[curRegion] = sum
    } else {
      alphaColor[curRegion] = alphaColorElement + sum
    }
  }

  return alphaColor
}

function mergeKeyAndCount(original, mergeObj) {
  const result = {...original}
  const toMerged = Object.entries(mergeObj)
  for (let i = 0; i < toMerged.length; i++) {
    const [allele, count] = toMerged[i];
    const resultElement = result[allele];
    if (typeof resultElement !== "undefined") {
      result[allele] = resultElement + count
    } else {
      result[allele] = count
    }
  }
  return result;
}

function countByRegion(data) {
  const region = {}

  for (let d of data) {
    const curData = {}
    const {province, alleleAmountList} = d;
    const curRegion = province?.region?.region;
    const regionElement = region[curRegion];

    for (let i = 0; i < alleleAmountList.length; i++) {
      const {allele, amount} = alleleAmountList[i];
      curData[allele] = amount
    }

    if (typeof regionElement !== "undefined") {
      region[curRegion] = mergeKeyAndCount(regionElement, curData)
    } else {
      region[curRegion] = curData;
    }
  }

  for (let k in region) {
    region[k] = Object.entries(region[k]).map(([allele, amount]) => ({
      allele,
      amount,
    }))
  }

  return region
}

const CalculationUtils = {
  countItemAndCount,
  countMapData,
  countByRegion,
};

export default CalculationUtils;