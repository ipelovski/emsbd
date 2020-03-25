package emsbj.admin;

import emsbj.weeklyslot.WeeklySlot;
import emsbj.weeklyslot.WeeklySlotRepository;
import emsbj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/schedule")
public class AdminScheduleController implements AuthorizedController {
    public static final String weeklySlotsList = "weeklySlotsList";
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;

    @GetMapping(value = "/weekly-slots", name = weeklySlotsList)
    public String weeklySlotsList(Model model) {
        Iterable<WeeklySlot> weeklySlots = weeklySlotRepository.findAll();
        model.addAttribute("weeklySlots", weeklySlots);
        return "admin/weekly-slots";
    }
}
