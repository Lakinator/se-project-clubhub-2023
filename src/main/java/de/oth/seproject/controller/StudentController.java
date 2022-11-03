package de.oth.seproject.controller;

import de.oth.seproject.model.Student;
import de.oth.seproject.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    // just shows the form
    @RequestMapping("/add")
    public ModelAndView showAddStudentForm() {
        var mv = new ModelAndView();
        var studentBlank = new Student();
        mv.addObject("studentForm", studentBlank);
        mv.setViewName("/student");
        return mv;
    }

    // actually process the form request
    @RequestMapping("/add/process")
    public ModelAndView addStudentProcess(@ModelAttribute Student student) {
        studentRepository.save(student);
        var mv = new ModelAndView();
        mv.setViewName("/student/student-added");
        return mv;
    }
}
