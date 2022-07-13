package com.sixestates.controller;

import com.sixestates.Idp;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/taskSubmit")
public class FormController {
    @PostMapping
    @ResponseBody
    public ResponseEntity taskSubmit(@RequestParam("callback") String callback, @RequestParam("callbackMode") String callbackMode, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        System.out.println("->>" + callback);
        System.out.println("->>" + callbackMode);
        System.out.println("->>" + file.getOriginalFilename());
        String TOKEN = "QWhPM7Wxqr3xc4dCQFmXhH8xYD8CTq3N41XnvV38OblJQpTw5R9DyKHA0coN5m81";
        Idp.init(TOKEN);

        Idp.setSubmitUrl("https://idp.6estates.com/customer/extraction/fields/async");
        Idp.setExtractUrl("https://idp.6estates.com/customer/extraction/field/async/result/");

        TaskDTO taskDto = null;

        TaskInfo taskInfo1 = TaskInfo.builder()
                .fileName(file.getOriginalFilename())
                .inputStream(new ByteArrayInputStream(file.getBytes()))
                .callback(callback)
                .callbackMode(Integer.valueOf(callbackMode))
                .fileType("CBKS")
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo1);
        System.out.println(taskDto.getData());
        return    ResponseEntity.ok(taskDto);
    }
}