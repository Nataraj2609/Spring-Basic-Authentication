package com.learn.springlearn.controller;

import com.learn.springlearn.model.User;
import com.learn.springlearn.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pat")
public class PatientController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public User getH() {
        User user = userRepository.findByUsername("nat");
        return user;
    }

    @GetMapping("/report/{format}")
    public String getReport(@PathVariable String format) throws FileNotFoundException, JRException {
        User user = userRepository.findByUsername("nat");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        String path = "C:\\Users\\Windows\\Downloads";

        //load file and compile it
        File file = ResourceUtils.getFile("classpath:invoice.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(userList);

        Map<String, Object> parameters = new HashedMap();
        parameters.put("createdBy","John Wick");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        if(("pdf").equalsIgnoreCase(format))
            JasperExportManager.exportReportToPdfFile(jasperPrint, path+"\\invoice.pdf");
        else
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path+"\\invoice.html");

        return "Report generated! :)";
    }
}
