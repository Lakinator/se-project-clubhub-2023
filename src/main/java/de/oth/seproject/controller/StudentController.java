package de.oth.seproject.controller;

import de.oth.seproject.model.Student;
import de.oth.seproject.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @RequestMapping("/add")
    public ModelAndView showAddStudentForm() {
        ModelAndView modelAndView = new ModelAndView();
        Student s = new Student();
        modelAndView.addObject("studentForm", s);
        modelAndView.setViewName("/student/student-add");

        return modelAndView;
    }

    @RequestMapping("/add/process")
    public ModelAndView process(@ModelAttribute Student student) {
        studentRepository.save(student);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", student.getName());
        modelAndView.addObject("email", student.getEmail());
        modelAndView.setViewName("/student/student-added");

        return modelAndView;
    }

}
