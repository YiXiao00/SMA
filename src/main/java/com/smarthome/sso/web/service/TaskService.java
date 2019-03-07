package com.smarthome.sso.web.service;



import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository tRepo;

    public void addOneTask(Task t){
        tRepo.save(t);
    }

    public void deleteTask(Task t){
        tRepo.delete(t);
    }

    public void deleteTasks(List<Task> tasks){
        tRepo.deleteAll(tasks);
    }

    public List<Task> findTasksByDeviceId(String deviceId){
        return tRepo.findByDeviceId(deviceId);
    }

    public ServiceResult deleteOneTaskByTaskId(String id){
        //FIX - DOES NOT HAVE THE VERIFICAITON USER SERVICE HAS
        tRepo.deleteByTaskId(id);
        return ServiceResult.SERVICE_SUCCESS;
    }

    public List<Task> findAllTasks() {
        return tRepo.findAll();
    }

    public void deleteSelf() {tRepo.deleteAll();}

}