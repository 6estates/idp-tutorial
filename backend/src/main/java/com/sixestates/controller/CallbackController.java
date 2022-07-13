package com.sixestates.controller;
import com.alibaba.fastjson.JSONObject;
import com.sixestates.Dao.Storeage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
// CallbackController
@Controller
public class CallbackController {

    @RequestMapping(value = "/mode0or1", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity mode0or1CallBack(@RequestBody String jsonStr,HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Receive a callback request!");
        System.out.println("Callback body: " + jsonStr);
        String taskId = JSONObject.parseObject(jsonStr).getJSONObject("data").getString("taskId");
        Storeage.jsonMap.put(taskId, jsonStr);

        return ResponseEntity.ok(200);
    }

    @RequestMapping(value = "/mode2")
    @ResponseBody
    public ResponseEntity mode2CallBack(@RequestParam("file") MultipartFile file,@RequestParam("result") MultipartFile json, HttpServletRequest request, HttpServletResponse response){
        try {
            System.out.println("Receive a callback request!");
            // Receive the result json file
            String jsonStr = new String(json.getBytes());
            System.out.println("Callback body: " + jsonStr);

            // Receive the file bytes
            System.out.println("Callback fileName: " + file.getOriginalFilename());
            byte[] fileBytes = file.getBytes();
            //FileStoreUtils.localFileSystemStore("/home/lay/Documents/tmp/spring/", file.getOriginalFilename(), fileBytes);
            String taskId = JSONObject.parseObject(jsonStr).getJSONObject("data").getString("taskId");
            Storeage.jsonMap.put(taskId, jsonStr);
            Storeage.fileMap.put(taskId, fileBytes);
            System.out.println("jsonMap size:" + Storeage.jsonMap.size());
            return ResponseEntity.ok(200);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(500);
        }
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String requestTask(@RequestBody String taskId) {
        System.out.println("requestTask!: " + taskId);
        System.out.println("jsonMap size:" + Storeage.jsonMap.size());
        for (String key : Storeage.jsonMap.keySet()) {
            System.out.println("----> " + key + ": " + Storeage.jsonMap.get(key));
        }
        if(Storeage.jsonMap.containsKey(taskId)) {
            return Storeage.jsonMap.get(taskId);
        }
        else {
            return "Not Found!";
        }
    }

    @RequestMapping(value = "/req", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity reqTask(@RequestBody String taskId) {
        System.out.println("requestTask!: " + taskId);
        System.out.println("jsonMap size:" + Storeage.jsonMap.size());
        byte[] fileB = Storeage.fileMap.get(taskId);
        String jsonStr =  Storeage.jsonMap.get(taskId);
        String filename = JSONObject.parseObject(jsonStr).getJSONObject("data").getString("taskFileName");
        InputStream sbs = new ByteArrayInputStream(fileB);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok().headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(sbs));
    }
}
