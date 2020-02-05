package emsbj.admin;

import emsbj.WeeklySlot;
import emsbj.WeeklySlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/schedule")
public class AdminScheduleController {
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;

    @GetMapping("/weekly-slots")
    public String listWeeklySlots(Model model) {
        Iterable<WeeklySlot> weeklySlots = weeklySlotRepository.findAll();
        model.addAttribute("weeklySlots", weeklySlots);
        return "admin/weekly-slots.html";
    }
}
