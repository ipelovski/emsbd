package sasj.controller.principal;

import sasj.controller.Extensions;
import sasj.data.room.Room;
import sasj.data.room.RoomRepository;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/principal/rooms")
public class PrincipalRoomController implements AuthorizedController {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private Extensions extensions;

    @GetMapping
    public String list(Model model) {
        Iterable<Room> rooms = roomRepository.findAll();
        model.addAttribute("rooms", rooms);
        return "principal/rooms";
    }

    @GetMapping(WebMvcConfig.addPath)
    public String add(Model model) {
        model.addAttribute("room", new Room());
        return "principal/room-details";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String addSubmit(Room room) {
        roomRepository.save(room);
        return "redirect:" + extensions.getPrincipalUrls().rooms();
    }
}
