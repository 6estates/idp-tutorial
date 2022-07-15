package com.sixestates.controller;

import com.alibaba.fastjson.JSONObject;
import com.sixestates.Idp;
import com.sixestates.dao.Storeage;
import com.sixestates.exception.ApiException;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.rest.v1.ResultExtractor;
import com.sixestates.type.ResultDTO;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class FormController {
    private static final Logger logger = LoggerFactory.getLogger(FormController.class);

    @PostMapping
    @ResponseBody
    @RequestMapping("/taskSubmit")
    public ResponseEntity taskSubmit(@RequestParam( value ="callback", required=false) String callback, @RequestParam(value="callbackMode", required=false) Integer callbackMode,
                                     @RequestParam("file") MultipartFile file, @RequestParam("mode") int mode,
                                     @RequestParam("fileType") String FileType, @RequestParam("token") String token) throws IOException {

        Idp.init(token);

        Idp.setSubmitUrl("https://idp.6estates.com/customer/extraction/fields/async");
        Idp.setExtractUrl("https://idp.6estates.com/customer/extraction/field/async/result/");

        TaskDTO taskDto = null;
        TaskInfo taskInfo = null;
        if(mode == 1) {
            // loop
            taskInfo = TaskInfo.builder()
                    .fileName(file.getOriginalFilename())
                    .inputStream(new ByteArrayInputStream(file.getBytes()))
                    .fileType(FileType)
                    .build();
        }else {
            // callback
            taskInfo = TaskInfo.builder()
                    .fileName(file.getOriginalFilename())
                    .inputStream(new ByteArrayInputStream(file.getBytes()))
                    .callback(callback)
                    .callbackMode(callbackMode)
                    .fileType(FileType)
                    .build();
        }
        taskDto = ExtractSubmitter.submit(taskInfo);
        logger.info("taskId" + taskDto.getData());
        Storeage.taskMap.put(taskDto.getData(), mode);
        return ResponseEntity.ok(taskDto);
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity requestTask(@RequestBody String jsonStr) {
        logger.info(jsonStr);
        JSONObject arr = JSONObject.parseObject(jsonStr);
        String taskId = arr.getString("taskId");
        String token = arr.getString("token");
        if(!Storeage.taskMap.containsKey(taskId)) return ResponseEntity.ok("Not found Task");

        int mode = Storeage.taskMap.get(taskId);
        if(mode == 2) {
            // callback
            return ResponseEntity.ok(Storeage.jsonMap.get(taskId));
        }else {
            // loop
            Idp.init(token);
            Idp.setSubmitUrl("https://idp.6estates.com/customer/extraction/fields/async");
            Idp.setExtractUrl("https://idp.6estates.com/customer/extraction/field/async/result/");
            boolean taskDone = false;
            ResultDTO resultDto = null;
            try{
                while(!taskDone){
                    resultDto = ResultExtractor.extractResultByTaskid(taskId);
                    if(resultDto.getTaskStatus().equals("Done")) {

                        //Print the response json string
                        System.out.println("node1: " + resultDto.getRespJson());
                        taskDone = true;
                    }else {
                        logger.info("The status is Doing or Init, please request again after 30 seconds ");
                        Thread.sleep( 1000 * 10);
                    }
                }
            }catch(ApiException | InterruptedException e){
                System.err.println(e);
            }
            if(resultDto != null)
                return ResponseEntity.ok(resultDto.getRespJson());
            else
                return ResponseEntity.ok("sorry, not found result");
        }
    }
}