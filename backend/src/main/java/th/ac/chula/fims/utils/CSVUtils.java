package th.ac.chula.fims.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import th.ac.chula.fims.payload.projection.ExportDetailDto;

import java.util.ArrayList;
import java.util.List;

public class CSVUtils {
    public static List<String> getCells(Row row) {
        List<String> data = new ArrayList<>();
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case STRING:
                    data.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    data.add(String.valueOf(cell.getNumericCellValue()));
                    break;
                case BOOLEAN:
                    data.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                default:
                    data.add("n/a");
            }
        }
        return data;
    }

    public static List<String> getCellsReplaceEmptyCell(Row row, String word) {
        List<String> data = new ArrayList<>();
        if (row == null) {
            return data;
        }
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                data.add(word);
                continue;
            }
            switch (cell.getCellType()) {
                case STRING:
                    data.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    data.add(String.valueOf(cell.getNumericCellValue()));
                    break;
                case BOOLEAN:
                    data.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                default:
                    data.add("n/a");
            }
        }
        return data;
    }

    public static List<String> getExportDetailDataByColumns(List<String> columns, ExportDetailDto detailDto) {
        List<String> result = new ArrayList<>();
        if (columns.contains("sampleId")) {
            result.add(detailDto.getSampleId());
        }
        if (columns.contains("sampleYear")) {
            result.add(detailDto.getSampleYear());
        }
        if (columns.contains("allele")) {
            result.add(detailDto.getAllele());
        }
        if (columns.contains("STRRepeatMotifs")) {
            if (detailDto.getRepeatMotif() == null) {
                result.add(detailDto.getSequence());
            } else {
                result.add(detailDto.getRepeatMotif());
            }
        }
        if (columns.contains("sequence")) {
            if (detailDto.getRepeatMotif() == null) {
                result.add(detailDto.getSequence());
            } else {
                result.addAll(SequenceUtils.convertRepeatMotifsToStringList(detailDto.getRepeatMotif()));
            }
        }
        return result;
    }

    public static List<String> getExportDetailHeaderByColumns(List<String> columns) {
        List<String> result = new ArrayList<>();
        if (columns.contains("sampleId")) {
            result.add("Sample ID");
        }
        if (columns.contains("sampleYear")) {
            result.add("Sample year");
        }
        if (columns.contains("allele")) {
            result.add("Allele");
        }
        if (columns.contains("STRRepeatMotifs")) {
            result.add("STR repeat motifs");
        }
        if (columns.contains("sequence")) {
            result.add("Sequence");
        }
        return result;
    }

    public static void addExportDetailDataToRow(Row row, List<String> data) {
        int size = data.size();
        for (int i = 0; i < size; i++) {
            row.createCell(i).setCellValue(data.get(i));
        }
    }
}
