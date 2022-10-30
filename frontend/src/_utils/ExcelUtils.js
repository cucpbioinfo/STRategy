import XLSX from "xlsx";

function convertToXlsx(workbook, file) {
  let fileBits = XLSX.write(workbook, {
    type: "base64",
    bookType: "xlsx",
    ignoreEC: false
  });
  let bstr = atob(fileBits)
  let n = bstr.length
  let u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new File([u8arr], file.name.replace(".xlsb", "") + ".xlsx", {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
}

const ExcelUtils = {
  convertToXlsx,
}

export default ExcelUtils;