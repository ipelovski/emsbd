package sasj.controller.principal;

import sasj.data.weeklyslot.WeeklySlot;
import sasj.data.weeklyslot.WeeklySlotRepository;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/principal/schedule")
public class PrincipalScheduleController implements AuthorizedController {
    public static final String weeklySlotsList = "weeklySlotsList";
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;

    @GetMapping(value = "/weekly-slots", name = weeklySlotsList)
    public String weeklySlotsList(Model model) {
        Iterable<WeeklySlot> weeklySlots = weeklySlotRepository.findAll();
        model.addAttribute("weeklySlots", weeklySlots);
        return "principal/weekly-slots";
    }
}
