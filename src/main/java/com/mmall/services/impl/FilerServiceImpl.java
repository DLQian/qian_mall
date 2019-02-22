package com.mmall.services.impl;

import com.mmall.services.FilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("filerService")
public class FilerServiceImpl implements FilerService {
    private Logger logger = LoggerFactory.getLogger(FilerServiceImpl.class);


    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        //提取文件扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //避免文件重复
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        //判断文件是否存在，设置权限，mkdirs 可以创建多个级别的文件夹
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);//文件已经上传成功了
//            FTPUtil.uploadFile(Lists.newArrayList(targetFile));//已经上传到ftp服务器上
//            targetFile.delete();//删除本地图片
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
