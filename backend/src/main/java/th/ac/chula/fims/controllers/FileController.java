package th.ac.chula.fims.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.payload.dto.ExcelPersonDataUploadResponse;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.services.interfaces.FileService;
import th.ac.chula.fims.utils.FileServiceUtils;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileservice;

    public FileController(FileService fileservice) {
        this.fileservice = fileservice;
    }

    @PostMapping("/forenseq")
    public ResponseEntity<?> uploadForenseqFile(@RequestParam("file") MultipartFile file, @RequestParam(required = false, name = "sampleId") String sampleId) throws IOException {
        Boolean isDirty = fileservice.readExcelForenseqData(file, sampleId);
        if (isDirty) {
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Success"));
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageResponse("Bad format file."));
        }
    }

    @PostMapping("/person")
    public ResponseEntity<?> uploadPersonFile(@RequestParam("file") MultipartFile file) throws IOException {
        ExcelPersonDataUploadResponse excelPersonData = fileservice.readExcelPersonData(file);
        if (excelPersonData.isDirty()) {
            return ResponseEntity.status(HttpStatus.OK).body(excelPersonData);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageResponse("Bad format file."));
        }
    }

    @PostMapping("/cedata")
    public ResponseEntity<?> uploadCEFile(@RequestParam("file") MultipartFile file) throws IOException {
        Boolean isDirty = fileservice.readTextFileCEData(file);
        if (isDirty) {
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Success"));
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageResponse("Bad format file."));
        }
    }

    @GetMapping("/example-excel")
    public ResponseEntity<?> downloadExcelExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/excel-example/example.xlsx");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }

    @GetMapping("/example-cedata")
    public ResponseEntity<?> downloadCEDataExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/upload-example/ce-text.txt");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }

    @GetMapping("/example-person")
    public ResponseEntity<?> downloadPersonExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/upload-example/person-upload.xlsx");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }


    @GetMapping("/example-seq-guide")
    public ResponseEntity<?> downloadSeqGuideExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/upload-example/sequence guide.xlsx");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }

    @GetMapping("/add-edit-provinces")
    public ResponseEntity<?> downloadAddRemoveProvincesExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/upload-example/add-edit-provinces.xlsx");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }

    @GetMapping("/core-loci")
    public ResponseEntity<?> downloadCoreLociExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/upload-example/core-loci.xlsx");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }

    @GetMapping("/profile-search/country")
    public ResponseEntity<?> downloadProfileSearchByCountryExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/upload-example/profile-search-by-country.csv");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }

    @GetMapping("/profile-search/all")
    public ResponseEntity<?> downloadProfileSearchAllExampleFile() throws MalformedURLException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("src/main/resources/upload-example/profile-search-all.csv");

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetFile.getFilename() + "\"")
                .body(targetFile);
    }
}
