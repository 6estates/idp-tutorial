package com.sixestates.controller;
import com.alibaba.fastjson.JSONObject;
import com.sixestates.dao.Storeage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
@Controller
public class CallbackController {
    private static final Logger logger = LoggerFactory.getLogger(CallbackController.class);
    @RequestMapping(value = "/mode0or1", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity mode0or1CallBack(@RequestBody String jsonStr,HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Receive a callback request!");
        logger.info("Callback body: " + jsonStr);
        String taskId = JSONObject.parseObject(jsonStr).getJSONObject("data").getString("taskId");
        Storeage.jsonMap.put(taskId, jsonStr);

        return ResponseEntity.ok(200);
    }

    @RequestMapping(value = "/mode2")
    @ResponseBody
    public ResponseEntity mode2CallBack(@RequestParam("file") MultipartFile file,@RequestParam("result") MultipartFile json, HttpServletRequest request, HttpServletResponse response){
        try {
            logger.info("Receive a callback request!");
            // Receive the result json file
            String jsonStr = new String(json.getBytes());
            logger.info("Callback body: " + jsonStr);

            // Receive the file bytes
            logger.info("Callback fileName: " + file.getOriginalFilename());
            byte[] fileBytes = file.getBytes();
            String taskId = JSONObject.parseObject(jsonStr).getJSONObject("data").getString("taskId");
            Storeage.jsonMap.put(taskId, jsonStr);
            Storeage.fileMap.put(taskId, fileBytes);
            logger.info("jsonMap size:" + Storeage.jsonMap.size());
            return ResponseEntity.ok(200);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(500);
        }
    }
}
