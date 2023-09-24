package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/admin/common")
@Api("通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(@RequestBody MultipartFile file) {
        log.info("文件上传：{}", file);
        try {

            //使用随机UUID拼接文件名
            String originalFilename = file.getOriginalFilename();
            String FileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.indexOf("."));

            //上传文件
            String upload = aliOssUtil.upload(file.getBytes(), FileName);
            return Result.success(upload);

        } catch (IOException e) {
            log.error("文件上传失败：{}", e.toString());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }


}
