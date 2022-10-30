const queryParams = (searchString) => {
  if (searchString === "") return {}
  const sString = searchString.slice(1)
  const queriesParams = sString.split("&");
  const params = {}

  queriesParams.forEach(query => {
    const [key, value] = query.split("=")
    params[key] = value;
  });

  return params;
}

const resolveSorter = (sortObj, url) => {
  let newUrl = url;
  const isArray = Array.isArray(sortObj);
  if (isArray && sortObj.length !== 0) {
    sortObj.forEach(({field, order}) => {
      newUrl += `&sort=${field},${toShortSorter(order)}`;
    })
    return newUrl;
  } else if (isArray && sortObj.length === 0) {
    return `${newUrl}&sort=id,asc`;
  } else {
    const {field, order} = sortObj;
    if (order) {
      return `${newUrl}&sort=${field},${toShortSorter(order)}`;
    } else {
      return `${newUrl}&sort=id,asc`;
    }
  }
}

const toShortSorter = (keyword) => {
  const caseSort = keyword.toLowerCase();
  switch (caseSort) {
    case "ascend":
      return "asc"
    case "descend":
      return "desc"
    default :
      throw new Error("Something went wrong.");
  }
}

const resolveFilter = (filterObj, url) => {
  let newUrl = url;

  for (let filter of Object.keys(filterObj)) {
    const value = filterObj[filter];
    if (Array.isArray(value) && value.length !== 0) {
      switch (filter) {
        case "status":
        case "roles":
        case "gender":
        case "age":
          newUrl += `&${filter}=${value}`;
          break;
        default:
          newUrl += `&${filter}=%25${value}%25`;
      }
    }
  }

  return newUrl;
}

const StringUtils = {
  queryParams,
  resolveSorter,
  resolveFilter,
}

export default StringUtils;