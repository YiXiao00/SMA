package com.smarthome.sso.web.service;



import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.DeviceRespository;
import com.smarthome.sso.web.domain.Task;
import com.smarthome.sso.web.domain.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository tRepo;

    public void addOneTask(Task t){ tRepo.save(t) ;
    }

    public List<Task> findAllTasks() { return tRepo.findAll();}

    public void deleteSelf() {tRepo.deleteAll();}

}