package de.oth.seproject.controller;

import de.oth.seproject.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/student")
public class StudentController {

    @RequestMapping("/add")
    public ModelAndView showAddStudentForm() {
        var mv = new ModelAndView();
        var studentBlank = new Student();
        mv.addObject("studentForm", studentBlank);
        mv.setViewName("/student");
        return null;
    }
}
