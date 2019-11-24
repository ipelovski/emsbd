package emsbd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/term-marks")
public class TermMarksController {
    @Autowired
    private MarkRepository markRepository;

    @GetMapping("")
    public String get(Model model) {
        List<Mark> marks = StreamSupport
            .stream(markRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
        model.addAttribute("marks", marks);
        return "term-marks.html";
    }
}
