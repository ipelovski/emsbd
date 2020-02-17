package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/blobs")
public class BlobController {
    public static final String uploadProfilePicture = "uploadProfilePicture";
    @Autowired
    private BlobDataRepository blobDataRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping(WebMvcConfig.objectIdPathParam)
    @ResponseBody
    public ResponseEntity<byte[]> details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long blobId
    ) {
        Optional<BlobData> optionalBlobData = blobDataRepository.findById(blobId);
        if (optionalBlobData.isPresent()) {
            BlobData blobData = optionalBlobData.get();
            return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(blobData.getMimeType()))
                .body(blobData.getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/profile-picture" + WebMvcConfig.objectIdPathParam,
        name = uploadProfilePicture)
    public String uploadProfilePicture(
        @PathVariable(WebMvcConfig.objectIdParamName) Long userId,
        @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes
    ) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            BlobData blobData = new BlobData();
            blobData.setMimeType(file.getContentType());
            try {
                blobData.setData(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blobDataRepository.save(blobData);
            if (user.getPersonalInfo().getPicture() != null) {
                Long oldBlobId = user.getPersonalInfo().getPicture().getId();
                user.getPersonalInfo().setPicture(null);
                userRepository.save(user);
                blobDataRepository.deleteById(oldBlobId);
            }
            user.getPersonalInfo().setPicture(blobData);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

            return "redirect:/";
        } else {
            return "";
        }
    }
}
