package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    // method for upload image
    @Override
    public String uploadImage(String path, MultipartFile img) throws IOException {
        // file names of current / original file
        String originalFileName = img.getOriginalFilename(); // give us a file name, path, ext.

        // generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.'))); // image.jpg --> 123 --> 1234.jpg
        String filePath = path + File.separator + fileName; // separator = /

        // check if path exist and create
        File folder = new File(path);

        if(!folder.exists()) {
            folder.mkdir();
        }

        // upload to server
        Files.copy(img.getInputStream(), Paths.get(filePath));

        // returning file name
        return fileName;
    }
}
