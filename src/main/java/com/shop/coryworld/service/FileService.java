package com.shop.coryworld.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    // apache Tika
    private final Tika tika = new Tika();

    private final List<String> imageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");;

    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID();
        String extension  = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) {
        File deleteFile = new File(filePath);

        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }

    public boolean checkImgFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        // 파일이름 체크
        if (fileName == null || !fileName.contains(".")) {
            return false;
        }

        String extension  = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();

        String detect;
        try (InputStream fileInputStream = file.getInputStream()) {
            detect = tika.detect(fileInputStream);
        } catch (IOException e) {
            return false;
        }

        return imageExtensions.contains(extension)
                && detect.startsWith("image/");
    }
}
