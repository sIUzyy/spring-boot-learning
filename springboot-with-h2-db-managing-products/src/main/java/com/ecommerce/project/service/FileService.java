package com.ecommerce.project.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    // method for upload image
    String uploadImage(String path, MultipartFile img) throws IOException;
}
