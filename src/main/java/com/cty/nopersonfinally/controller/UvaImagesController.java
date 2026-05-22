package com.cty.nopersonfinally.controller;

import com.cty.nopersonfinally.pojo.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UVA图片控制器
 * 用于获取UVAImages文件夹中的图片列表
 */
@Slf4j
@RestController
@RequestMapping("/uva-images")
public class UvaImagesController {

    @Value("${server.url:http://localhost:8082}")
    private String serverUrl;

    private List<String> imageUrls = new ArrayList<>();
    
    private static final String IMAGE_FOLDER = "src/main/resources/static/UVAImages";
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif");

    @PostConstruct
    public void init() {
        loadImages();
    }

    /**
     * 加载UVAImages文件夹中的所有图片
     */
    private void loadImages() {
        File folder = new File(IMAGE_FOLDER);
        imageUrls.clear();
        
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName().toLowerCase();
                    if (ALLOWED_EXTENSIONS.stream().anyMatch(fileName::endsWith)) {
                        String imageUrl = serverUrl + "/UVAImages/" + file.getName();
                        imageUrls.add(imageUrl);
                        log.info("加载UVA图片: {}", imageUrl);
                    }
                }
            }
        } else {
            log.warn("UVAImages文件夹不存在: {}", folder.getAbsolutePath());
        }
        
        log.info("共加载{}张UVA图片", imageUrls.size());
    }

    /**
     * 获取所有UVA图片列表
     */
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public Result<?> getImageList() {
        return new Result<>(200, "操作成功", imageUrls);
    }

    /**
     * 获取随机图片
     * @param count 获取的图片数量
     */
    @GetMapping("/random")
    @PreAuthorize("permitAll()")
    public Result<?> getRandomImages(@RequestParam(defaultValue = "3") int count) {
        if (imageUrls.isEmpty()) {
            return Result.error("暂无图片");
        }
        
        List<String> shuffled = new ArrayList<>(imageUrls);
        Collections.shuffle(shuffled);
        
        int actualCount = Math.min(count, shuffled.size());
        List<String> result = shuffled.stream()
                .limit(actualCount)
                .collect(Collectors.toList());
        
        return new Result<>(200, "操作成功", result);
    }

    /**
     * 获取图片数量
     */
    @GetMapping("/count")
    @PreAuthorize("permitAll()")
    public Result<?> getImageCount() {
        return new Result<>(200, "操作成功", imageUrls.size());
    }
}
