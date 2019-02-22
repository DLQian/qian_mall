package com.mmall.services;

import org.springframework.web.multipart.MultipartFile;

public interface FilerService {

    String upload(MultipartFile file, String path);
}
